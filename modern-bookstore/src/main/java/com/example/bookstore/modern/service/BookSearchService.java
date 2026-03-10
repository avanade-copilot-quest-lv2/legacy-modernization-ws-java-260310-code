package com.example.bookstore.modern.service;

import com.example.bookstore.modern.entity.Book;
import com.example.bookstore.modern.entity.Category;
import com.example.bookstore.modern.repository.BookRepository;
import com.example.bookstore.modern.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 書籍検索サービス
 * 
 * レガシーの BookstoreManager.searchBooks() を置き換え
 * - 直接JDBCを排除
 * - Spring Data JPA で簡潔に実装
 * - キャッシュはSpring Cacheで後から追加可能な設計
 */
@Service
@Transactional(readOnly = true)
public class BookSearchService {

    private static final Logger log = LoggerFactory.getLogger(BookSearchService.class);

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookSearchService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 書籍を検索
     * 
     * @param isbn ISBNの部分一致 (nullまたは空の場合は条件なし)
     * @param title タイトルの部分一致 (nullまたは空の場合は条件なし)
     * @param categoryId カテゴリID (nullまたは空の場合は条件なし)
     * @return 検索結果
     */
    public List<Book> searchBooks(String isbn, String title, String categoryId) {
        log.info("Book search: isbn={}, title={}, categoryId={}", isbn, title, categoryId);

        // 空文字列をnullに正規化
        String normalizedIsbn = isBlank(isbn) ? null : isbn.trim();
        String normalizedTitle = isBlank(title) ? null : title.trim();
        String normalizedCategoryId = isBlank(categoryId) ? null : categoryId.trim();

        // 検索条件がない場合は全件取得
        if (normalizedIsbn == null && normalizedTitle == null && normalizedCategoryId == null) {
            log.debug("No search criteria, returning all active books");
            return bookRepository.findAllActive();
        }

        // 複合検索
        List<Book> results = bookRepository.searchBooks(normalizedIsbn, normalizedTitle, normalizedCategoryId);
        log.info("Search returned {} results", results.size());
        return results;
    }

    /**
     * ISBN検索
     */
    public List<Book> searchByIsbn(String isbn) {
        if (isBlank(isbn)) {
            return List.of();
        }
        log.info("ISBN search: {}", isbn);
        return bookRepository.findByIsbnContaining(isbn.trim());
    }

    /**
     * タイトル検索
     */
    public List<Book> searchByTitle(String title) {
        if (isBlank(title)) {
            return List.of();
        }
        log.info("Title search: {}", title);
        return bookRepository.findByTitleContaining(title.trim());
    }

    /**
     * カテゴリ検索
     */
    public List<Book> searchByCategory(String categoryId) {
        if (isBlank(categoryId)) {
            return List.of();
        }
        log.info("Category search: {}", categoryId);
        return bookRepository.findByCategoryId(categoryId.trim());
    }

    /**
     * 全カテゴリ一覧を取得
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAllSorted();
    }

    /**
     * 在庫切れ書籍一覧
     */
    public List<Book> getOutOfStockBooks() {
        return bookRepository.findOutOfStock();
    }

    /**
     * 在庫少書籍一覧
     */
    public List<Book> getLowStockBooks() {
        return bookRepository.findLowStock();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
