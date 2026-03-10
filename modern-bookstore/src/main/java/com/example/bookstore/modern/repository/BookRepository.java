package com.example.bookstore.modern.repository;

import com.example.bookstore.modern.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 書籍リポジトリ
 * 
 * Spring Data JPA による自動実装
 * レガシーの BookDAO + BookDAOImpl + 直接JDBC を置き換え
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * ISBNで検索
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * ISBN部分一致検索 (削除フラグ考慮)
     */
    @Query("SELECT b FROM Book b WHERE b.isbn LIKE %:isbn% AND (b.deleteFlag = '0' OR b.deleteFlag IS NULL)")
    List<Book> findByIsbnContaining(@Param("isbn") String isbn);

    /**
     * タイトル部分一致検索 (削除フラグ考慮)
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) AND (b.deleteFlag = '0' OR b.deleteFlag IS NULL)")
    List<Book> findByTitleContaining(@Param("title") String title);

    /**
     * カテゴリIDで検索 (削除フラグ考慮)
     */
    @Query("SELECT b FROM Book b WHERE b.categoryId = :categoryId AND (b.deleteFlag = '0' OR b.deleteFlag IS NULL)")
    List<Book> findByCategoryId(@Param("categoryId") String categoryId);

    /**
     * 複合検索 (ISBN, タイトル, カテゴリ)
     */
    @Query("""
        SELECT b FROM Book b 
        WHERE (b.deleteFlag = '0' OR b.deleteFlag IS NULL)
        AND (:isbn IS NULL OR b.isbn LIKE %:isbn%)
        AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:categoryId IS NULL OR b.categoryId = :categoryId)
        ORDER BY b.title
        """)
    List<Book> searchBooks(
        @Param("isbn") String isbn,
        @Param("title") String title,
        @Param("categoryId") String categoryId
    );

    /**
     * アクティブな書籍一覧 (削除フラグ考慮)
     */
    @Query("SELECT b FROM Book b WHERE (b.deleteFlag = '0' OR b.deleteFlag IS NULL) ORDER BY b.title")
    List<Book> findAllActive();

    /**
     * 在庫切れ書籍一覧
     */
    @Query("SELECT b FROM Book b WHERE (b.deleteFlag = '0' OR b.deleteFlag IS NULL) AND (b.qtyInStock = '0' OR b.qtyInStock IS NULL)")
    List<Book> findOutOfStock();

    /**
     * 在庫少の書籍一覧
     */
    @Query(value = "SELECT * FROM books WHERE (del_flg = '0' OR del_flg IS NULL) AND CAST(qty_in_stock AS SIGNED) > 0 AND CAST(qty_in_stock AS SIGNED) < 10", nativeQuery = true)
    List<Book> findLowStock();
}
