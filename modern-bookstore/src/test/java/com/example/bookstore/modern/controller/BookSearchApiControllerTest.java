package com.example.bookstore.modern.controller;

import com.example.bookstore.modern.entity.Book;
import com.example.bookstore.modern.service.BookSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BookSearchApiController の統合テスト
 * 
 * REST API のレスポンスをテスト
 */
@WebMvcTest(BookSearchApiController.class)
class BookSearchApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookSearchService bookSearchService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setIsbn("9780061120084");
        sampleBook.setTitle("To Kill a Mockingbird");
        sampleBook.setPublisher("HarperCollins");
        sampleBook.setListPrice(12.99);
        sampleBook.setQtyInStock("45");
    }

    @Test
    @DisplayName("検索APIが正常にJSONを返す")
    void searchApi_returnsJson() throws Exception {
        // Given
        when(bookSearchService.searchBooks(any(), any(), any()))
                .thenReturn(Arrays.asList(sampleBook));

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("title", "Mockingbird")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.searchTimeMs", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.books[0].title", is("To Kill a Mockingbird")))
                .andExpect(jsonPath("$.books[0].isbn", is("9780061120084")));
    }

    @Test
    @DisplayName("検索結果なしの場合、空の配列を返す")
    void searchApi_noResults_returnsEmptyArray() throws Exception {
        // Given
        when(bookSearchService.searchBooks(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("title", "nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.books", hasSize(0)));
    }

    @Test
    @DisplayName("全書籍一覧APIが動作する")
    void findAll_returnsList() throws Exception {
        // Given
        when(bookSearchService.searchBooks(null, null, null))
                .thenReturn(Arrays.asList(sampleBook));

        // When & Then
        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("ISBN検索APIが動作する")
    void findByIsbn_returnsList() throws Exception {
        // Given
        when(bookSearchService.searchByIsbn("978"))
                .thenReturn(Arrays.asList(sampleBook));

        // When & Then
        mockMvc.perform(get("/api/books/isbn/978")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn", is("9780061120084")));
    }

    @Test
    @DisplayName("カテゴリ一覧APIが動作する")
    void getCategories_returnsList() throws Exception {
        // Given
        when(bookSearchService.getAllCategories()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/books/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
