package com.example.bookstore.modern.repository;

import com.example.bookstore.modern.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * カテゴリリポジトリ
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * カテゴリ一覧 (ソート順)
     */
    @Query("SELECT c FROM Category c ORDER BY c.sortOrder, c.name")
    List<Category> findAllSorted();
}
