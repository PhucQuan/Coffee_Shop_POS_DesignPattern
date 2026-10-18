package com.coffeeshop;

/**
 * Runner chuyên biệt thực thi bộ Kiểm Thử Đơn Vị (Unit Testing - 18 Test Cases).
 * Tập trung kiểm thử từng đơn vị độc lập: Design Patterns, Services, Validation logic
 * với Repository giả lập (InMemoryRepository) hoàn toàn cô lập, không phụ thuộc CSDL bên ngoài.
 */
public class UnitTestRunner {
    public static void main(String[] args) {
        TestRunner.main(new String[]{"unit"});
    }
}
