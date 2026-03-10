package com.example.bookstore.modern.controller;

import com.example.bookstore.modern.entity.Book;
import com.example.bookstore.modern.service.BookSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 書籍検索 REST API コントローラー
 * 
 * フロントエンドをReact/Vue等に置き換える場合に使用
 * URL: /api/books/search
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")  // 開発用、本番では適切に設定
public class BookSearchApiController {

    private static final Logger log = LoggerFactory.getLogger(BookSearchApiController.class);

    private final BookSearchService bookSearchService;

    public BookSearchApiController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    /**
     * 書籍検索 API
     * 
     * GET /api/books/search?isbn=xxx&title=xxx&catId=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "catId", required = false) String categoryId) {

        long startTime = System.currentTimeMillis();

        List<Book> books = bookSearchService.searchBooks(isbn, title, categoryId);

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("API Search completed in {}ms, found {} books", elapsed, books.size());

        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", books.size(),
            "searchTimeMs", elapsed,
            "books", books
        ));
    }

    /**
     * 全書籍一覧 API
     */
    @GetMapping
    public ResponseEntity<List<Book>> findAll() {
        return ResponseEntity.ok(bookSearchService.searchBooks(null, null, null));
    }

    /**
     * ISBN検索 API
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<Book>> findByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookSearchService.searchByIsbn(isbn));
    }

    /**
     * カテゴリ一覧 API
     */
    @GetMapping("/categories")
    public ResponseEntity<List<?>> getCategories() {
        return ResponseEntity.ok(bookSearchService.getAllCategories());
    }
}
