package com.example.bookstore.modern.service;

import com.example.bookstore.modern.entity.Book;
import com.example.bookstore.modern.entity.Category;
import com.example.bookstore.modern.repository.BookRepository;
import com.example.bookstore.modern.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * BookSearchService のユニットテスト
 * 
 * レガシーコードにはテストがなかったが、
 * モダナイズによりテスト可能な設計になった
 */
@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

    private Book sampleBook1;
    private Book sampleBook2;

    @BeforeEach
    void setUp() {
        sampleBook1 = createBook(1L, "9780061120084", "To Kill a Mockingbird", "1", 12.99);
        sampleBook2 = createBook(2L, "9780451524935", "1984", "1", 9.99);
    }

    private Book createBook(Long id, String isbn, String title, String categoryId, double price) {
        Book book = new Book();
        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setCategoryId(categoryId);
        book.setListPrice(price);
        book.setStatus("ACTIVE");
        book.setDeleteFlag("0");
        book.setQtyInStock("10");
        return book;
    }

    @Nested
    @DisplayName("searchBooks - 書籍検索")
    class SearchBooksTest {

        @Test
        @DisplayName("検索条件なしの場合、全件取得する")
        void searchBooks_noCondition_returnsAllActive() {
            // Given
            List<Book> expectedBooks = Arrays.asList(sampleBook1, sampleBook2);
            when(bookRepository.findAllActive()).thenReturn(expectedBooks);

            // When
            List<Book> result = bookSearchService.searchBooks(null, null, null);

            // Then
            assertThat(result).hasSize(2);
            verify(bookRepository).findAllActive();
            verify(bookRepository, never()).searchBooks(any(), any(), any());
        }

        @Test
        @DisplayName("空文字の場合も検索条件なしとして扱う")
        void searchBooks_emptyStrings_returnsAllActive() {
            // Given
            List<Book> expectedBooks = Arrays.asList(sampleBook1);
            when(bookRepository.findAllActive()).thenReturn(expectedBooks);

            // When
            List<Book> result = bookSearchService.searchBooks("", "  ", "");

            // Then
            assertThat(result).hasSize(1);
            verify(bookRepository).findAllActive();
        }

        @Test
        @DisplayName("ISBNで検索できる")
        void searchBooks_byIsbn_returnsMatchingBooks() {
            // Given
            String isbn = "9780061120084";
            when(bookRepository.searchBooks(eq(isbn), any(), any()))
                    .thenReturn(Collections.singletonList(sampleBook1));

            // When
            List<Book> result = bookSearchService.searchBooks(isbn, null, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIsbn()).isEqualTo(isbn);
        }

        @Test
        @DisplayName("タイトルで検索できる")
        void searchBooks_byTitle_returnsMatchingBooks() {
            // Given
            String title = "Mockingbird";
            when(bookRepository.searchBooks(any(), eq(title), any()))
                    .thenReturn(Collections.singletonList(sampleBook1));

            // When
            List<Book> result = bookSearchService.searchBooks(null, title, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).contains("Mockingbird");
        }

        @Test
        @DisplayName("カテゴリIDで検索できる")
        void searchBooks_byCategoryId_returnsMatchingBooks() {
            // Given
            String categoryId = "1";
            when(bookRepository.searchBooks(any(), any(), eq(categoryId)))
                    .thenReturn(Arrays.asList(sampleBook1, sampleBook2));

            // When
            List<Book> result = bookSearchService.searchBooks(null, null, categoryId);

            // Then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("複合条件で検索できる")
        void searchBooks_multipleConditions_returnsMatchingBooks() {
            // Given
            String isbn = "978";
            String title = "1984";
            when(bookRepository.searchBooks(eq(isbn), eq(title), any()))
                    .thenReturn(Collections.singletonList(sampleBook2));

            // When
            List<Book> result = bookSearchService.searchBooks(isbn, title, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("1984");
        }

        @Test
        @DisplayName("該当なしの場合、空リストを返す")
        void searchBooks_noMatch_returnsEmptyList() {
            // Given
            when(bookRepository.searchBooks(any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            // When
            List<Book> result = bookSearchService.searchBooks("nonexistent", null, null);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchByIsbn - ISBN検索")
    class SearchByIsbnTest {

        @Test
        @DisplayName("ISBNがnullの場合、空リストを返す")
        void searchByIsbn_null_returnsEmptyList() {
            List<Book> result = bookSearchService.searchByIsbn(null);
            assertThat(result).isEmpty();
            verify(bookRepository, never()).findByIsbnContaining(any());
        }

        @Test
        @DisplayName("ISBNが空の場合、空リストを返す")
        void searchByIsbn_empty_returnsEmptyList() {
            List<Book> result = bookSearchService.searchByIsbn("  ");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllCategories - カテゴリ一覧")
    class GetAllCategoriesTest {

        @Test
        @DisplayName("カテゴリ一覧を取得できる")
        void getAllCategories_returnsSortedList() {
            // Given
            Category cat1 = new Category();
            cat1.setId(1L);
            cat1.setName("Fiction");
            Category cat2 = new Category();
            cat2.setId(2L);
            cat2.setName("Non-Fiction");
            when(categoryRepository.findAllSorted()).thenReturn(Arrays.asList(cat1, cat2));

            // When
            List<Category> result = bookSearchService.getAllCategories();

            // Then
            assertThat(result).hasSize(2);
            verify(categoryRepository).findAllSorted();
        }
    }
}
