package com.coffeeshop.api;

import com.coffeeshop.AppContext;
import com.coffeeshop.infrastructure.DatabaseConnection;
import com.coffeeshop.infrastructure.MenuItemRecord;
import com.coffeeshop.infrastructure.MySqlStateRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.sql.SQLException;

public class WebServer {
    private final AppContext context;
    private final int port;
    private final Path frontendRoot;
    private final MySqlStateRepository stateRepository;
    private HttpServer server;

    public WebServer(AppContext context, int port) throws SQLException {
        this.context = context;
        this.port = port;
        this.frontendRoot = Paths.get("frontend-react", "dist").toAbsolutePath().normalize();
        this.stateRepository = new MySqlStateRepository(DatabaseConnection.getInstance());
    }

    public void start() throws IOException {
        if (!Files.exists(frontendRoot.resolve("index.html"))) {
            throw new IOException("React build not found. Run npm install and npm run build in frontend-react first.");
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoints
        server.createContext("/api/health", new HealthHandler(stateRepository));
        server.createContext("/api/menu", new MenuHandler(context));
        server.createContext("/api/state", new StateHandler(stateRepository));

        // Static Frontend Files handler
        server.createContext("/", new StaticFileHandler(frontendRoot));

        server.setExecutor(null);
        server.start();
        System.out.println("=================================================");
        System.out.println("  Coffee Shop POS Web Server running!");
        System.out.println("  URL: http://localhost:" + port);
        System.out.println("  Frontend directory: " + frontendRoot);
        System.out.println("=================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        stateRepository.close();
    }

    private static class HealthHandler implements HttpHandler {
        private final MySqlStateRepository stateRepository;

        HealthHandler(MySqlStateRepository stateRepository) {
            this.stateRepository = stateRepository;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            boolean databaseReady = stateRepository.ping();
            String response = "{\"status\":\"" + (databaseReady ? "ok" : "degraded")
                    + "\",\"service\":\"CoffeeShopPOS\",\"database\":\"mysql\"}";
            sendJsonResponse(exchange, databaseReady ? 200 : 503, response);
        }
    }

    private static class StateHandler implements HttpHandler {
        private static final int MAX_STATE_BYTES = 5 * 1024 * 1024;
        private final MySqlStateRepository repository;

        StateHandler(MySqlStateRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange);
            String method = exchange.getRequestMethod().toUpperCase();
            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                switch (method) {
                    case "GET" -> sendJsonResponse(exchange, 200, repository.load().orElse("null"));
                    case "PUT" -> {
                        byte[] body = exchange.getRequestBody().readNBytes(MAX_STATE_BYTES + 1);
                        if (body.length > MAX_STATE_BYTES) {
                            sendJsonResponse(exchange, 413, "{\"error\":\"State payload is too large.\"}");
                            return;
                        }
                        repository.save(new String(body, StandardCharsets.UTF_8));
                        sendJsonResponse(exchange, 200, "{\"saved\":true}");
                    }
                    case "DELETE" -> {
                        repository.clear();
                        exchange.sendResponseHeaders(204, -1);
                    }
                    default -> sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed.\"}");
                }
            } catch (IllegalArgumentException exception) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Invalid JSON state.\"}");
            } catch (RuntimeException exception) {
                exception.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\":\"MySQL persistence failed.\"}");
            }
        }
    }

    private static class MenuHandler implements HttpHandler {
        private final AppContext context;

        MenuHandler(AppContext context) {
            this.context = context;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            List<MenuItemRecord> items = context.repository.getMenu();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < items.size(); i++) {
                MenuItemRecord item = items.get(i);
                if (i > 0) sb.append(",");
                sb.append(String.format("{\"id\":%d,\"name\":\"%s\",\"price\":%.0f,\"category\":\"%s\"}",
                        item.getId(), escape(item.getName()), item.getBasePrice(), item.getCategory()));
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        private final Path frontendRoot;

        StaticFileHandler(Path frontendRoot) {
            this.frontendRoot = frontendRoot;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            Path target = frontendRoot.resolve(path.substring(1)).normalize();
            if (!target.startsWith(frontendRoot) || !Files.exists(target) || Files.isDirectory(target)) {
                // If not found, try serving index.html
                target = frontendRoot.resolve("index.html");
            }

            if (!Files.exists(target)) {
                String notFound = "404 Not Found";
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String mime = getMimeType(target.toString());
            exchange.getResponseHeaders().set("Content-Type", mime);
            byte[] bytes = Files.readAllBytes(target);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    private static String getMimeType(String file) {
        String lower = file.toLowerCase();
        if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            int port = 8088;
            if (args.length > 0) {
                try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
            }
            AppContext context = new AppContext();
            WebServer server = new WebServer(context, port);
            server.start();

        } catch (Exception e) {
            System.err.println("Cannot start Coffee Shop POS. Check that MySQL is running and that MYSQL_* variables are correct.");
            e.printStackTrace();
        }
    }
}
