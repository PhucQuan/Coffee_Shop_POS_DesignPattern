package com.coffeeshop;

/**
 * Runner chuyên biệt thực thi bộ Kiểm Thử Tích Hợp (Integration Testing - 6 Test Cases).
 * Tập trung kiểm thử tương tác và tích hợp giữa các thành phần:
 * Services <-> SqliteRepository <-> CSDL vật lý SQLite <-> Audit Logs <-> File Backup/Restore.
 */
public class IntegrationTestRunner {
    public static void main(String[] args) {
        TestRunner.main(new String[]{"integration"});
    }
}
