package com.coffeeshop.api;

import com.coffeeshop.AppContext;
import com.coffeeshop.infrastructure.MenuItemRecord;
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

public class WebServer {
    private final AppContext context;
    private final int port;
    private HttpServer server;

    public WebServer(AppContext context, int port) {
        this.context = context;
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoints
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/menu", new MenuHandler(context));

        // Static Frontend Files handler
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("=================================================");
        System.out.println("  Coffee Shop POS Web Server running!");
        System.out.println("  URL: http://localhost:" + port);
        System.out.println("  Frontend directory: frontend/");
        System.out.println("=================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String response = "{\"status\":\"ok\",\"service\":\"CoffeeShopPOS\"}";
            sendJsonResponse(exchange, 200, response);
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

        StaticFileHandler() {
            Path reactDist = Paths.get("frontend-react", "dist").toAbsolutePath().normalize();
            if (Files.exists(reactDist)) {
                this.frontendRoot = reactDist;
            } else {
                this.frontendRoot = Paths.get("frontend").toAbsolutePath().normalize();
            }
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
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
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
            int port = 8088;
            if (args.length > 0) {
                try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
            }
            AppContext context = new AppContext();
            WebServer server = new WebServer(context, port);
            server.start();

            // Try opening browser
            try {
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + port));
                }
            } catch (Exception ignored) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
