package com.example.bookstore.modern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Modern Bookstore - Book Search Service
 * 
 * レガシーシステム(Struts 1.x)からの段階的モダナイゼーション
 * このサービスは書籍検索機能を Spring Boot 3 + Java 21 で再実装しています。
 */
@SpringBootApplication
public class ModernBookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModernBookstoreApplication.class, args);
    }
}
