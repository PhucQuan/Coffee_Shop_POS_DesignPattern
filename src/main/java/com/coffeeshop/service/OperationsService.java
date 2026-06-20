package com.coffeeshop.service;

import com.coffeeshop.infrastructure.InventoryTransactionRecord;
import com.coffeeshop.infrastructure.OrderStatusHistoryRecord;
import com.coffeeshop.infrastructure.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OperationsService {
    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final Repository repository;

    public OperationsService(Repository repository) {
        this.repository = repository;
    }

    public List<OrderStatusHistoryRecord> getOrderStatusHistory() {
        return repository.getOrderStatusHistory();
    }

    public List<InventoryTransactionRecord> getInventoryTransactions() {
        return repository.getInventoryTransactions();
    }

    public boolean canBackup() {
        return repository.getStoragePath().isPresent();
    }

    public String getStorageLabel() {
        return repository.getStoragePath().orElse("In-memory repository");
    }

    public Path backupDatabase() {
        String storagePath = repository.getStoragePath()
                .orElseThrow(() -> new IllegalStateException("Current repository does not support database backup."));
        Path dbPath = Paths.get(storagePath).toAbsolutePath().normalize();
        Path parent = dbPath.getParent() == null ? Paths.get(".").toAbsolutePath().normalize() : dbPath.getParent();
        Path backupDirectory = parent.resolve("backups");
        String backupName = "coffee-shop-pos-backup-" + LocalDateTime.now().format(BACKUP_FORMAT) + ".db";
        Path backupPath = backupDirectory.resolve(backupName);
        try {
            Files.createDirectories(backupDirectory);
            repository.backupTo(backupPath.toString());
            return backupPath;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create backup directory.", ex);
        }
    }
}
