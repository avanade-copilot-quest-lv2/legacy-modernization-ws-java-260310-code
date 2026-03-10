package com.example.bookstore.modern.controller;

import com.example.bookstore.modern.entity.Book;
import com.example.bookstore.modern.entity.Category;
import com.example.bookstore.modern.service.BookSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 書籍検索コントローラー (Web UI)
 * 
 * レガシーの BookAction を置き換え
 * URL: /book/search → Thymeleaf テンプレートで表示
 */
@Controller
@RequestMapping("/book")
public class BookSearchController {

    private static final Logger log = LoggerFactory.getLogger(BookSearchController.class);

    private final BookSearchService bookSearchService;

    public BookSearchController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    /**
     * 書籍検索画面
     * 
     * レガシーURL: /book/search.do
     * モダンURL: /book/search
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "catId", required = false) String categoryId,
            Model model) {

        long startTime = System.currentTimeMillis();

        // カテゴリ一覧を取得 (検索フォームのプルダウン用)
        List<Category> categories = bookSearchService.getAllCategories();
        model.addAttribute("categories", categories);

        // 検索実行
        List<Book> books = bookSearchService.searchBooks(isbn, title, categoryId);

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Search completed in {}ms, found {} books", elapsed, books.size());

        // 検索条件をフォームに戻す
        model.addAttribute("isbn", isbn);
        model.addAttribute("title", title);
        model.addAttribute("catId", categoryId);
        
        // 検索結果
        model.addAttribute("books", books);
        model.addAttribute("resultCount", books.size());
        model.addAttribute("searchTime", elapsed);

        return "search";
    }

    /**
     * トップページ → 検索画面にリダイレクト
     */
    @GetMapping("")
    public String index() {
        return "redirect:/book/search";
    }
}
