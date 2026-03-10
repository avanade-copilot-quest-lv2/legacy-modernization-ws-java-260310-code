package com.example.bookstore.modern.controller;

import com.example.bookstore.modern.entity.Book;
import com.example.bookstore.modern.entity.Category;
import com.example.bookstore.modern.service.BookSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BookSearchController の統合テスト
 * 
 * MockMvc を使用してHTTPリクエスト/レスポンスをテスト
 */
@WebMvcTest(BookSearchController.class)
class BookSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookSearchService bookSearchService;

    private Book sampleBook;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setIsbn("9780061120084");
        sampleBook.setTitle("To Kill a Mockingbird");
        sampleBook.setPublisher("HarperCollins");
        sampleBook.setListPrice(12.99);
        sampleBook.setTaxRate("10");
        sampleBook.setQtyInStock("45");
        sampleBook.setStatus("ACTIVE");
        sampleBook.setDeleteFlag("0");

        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Fiction");
    }

    @Test
    @DisplayName("検索画面が表示される")
    void search_displaysSearchPage() throws Exception {
        // Given
        when(bookSearchService.getAllCategories()).thenReturn(Collections.singletonList(sampleCategory));
        when(bookSearchService.searchBooks(any(), any(), any())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/book/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("resultCount"))
                .andExpect(model().attributeExists("searchTime"));
    }

    @Test
    @DisplayName("検索結果が表示される")
    void search_withResults_displaysBooks() throws Exception {
        // Given
        List<Book> books = Arrays.asList(sampleBook);
        when(bookSearchService.getAllCategories()).thenReturn(Collections.singletonList(sampleCategory));
        when(bookSearchService.searchBooks(any(), any(), any())).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/book/search").param("title", "Mockingbird"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("books", hasSize(1)))
                .andExpect(model().attribute("resultCount", 1))
                .andExpect(model().attribute("title", "Mockingbird"));
    }

    @Test
    @DisplayName("ISBN検索パラメータが渡される")
    void search_withIsbn_passesParameter() throws Exception {
        // Given
        when(bookSearchService.getAllCategories()).thenReturn(Collections.emptyList());
        when(bookSearchService.searchBooks(any(), any(), any())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/book/search").param("isbn", "978"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isbn", "978"));
    }

    @Test
    @DisplayName("カテゴリ検索パラメータが渡される")
    void search_withCategory_passesParameter() throws Exception {
        // Given
        when(bookSearchService.getAllCategories()).thenReturn(Collections.singletonList(sampleCategory));
        when(bookSearchService.searchBooks(any(), any(), any())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/book/search").param("catId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("catId", "1"));
    }

    @Test
    @DisplayName("/book にアクセスすると検索画面にリダイレクトされる")
    void index_redirectsToSearch() throws Exception {
        mockMvc.perform(get("/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/search"));
    }
}
