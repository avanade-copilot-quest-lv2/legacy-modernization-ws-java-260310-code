package com.example.bookstore.modern.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 書籍エンティティ
 * 
 * レガシーDBの books テーブルにマッピング
 * ※ レガシーDBのカラム名に合わせて @Column を設定
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isbn", length = 13)
    private String isbn;

    @Column(name = "title")
    private String title;

    @Column(name = "category_id", length = 20)
    private String categoryId;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "pub_dt", length = 8)
    private String pubDt;

    @Column(name = "list_price")
    private Double listPrice;

    @Column(name = "tax_rate", length = 10)
    private String taxRate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "descr", columnDefinition = "TEXT")
    private String description;

    @Column(name = "qty_in_stock", length = 10)
    private String qtyInStock;

    @Column(name = "preferred_supplier_id", length = 20)
    private String preferredSupplierId;

    @Column(name = "crt_dt", length = 8)
    private String createdDate;

    @Column(name = "upd_dt", length = 8)
    private String updatedDate;

    @Column(name = "del_flg", length = 1)
    private String deleteFlag;

    // Many-to-One relationship with Category (optional join)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Category category;

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubDt() {
        return pubDt;
    }

    public void setPubDt(String pubDt) {
        this.pubDt = pubDt;
    }

    public Double getListPrice() {
        return listPrice;
    }

    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQtyInStock() {
        return qtyInStock;
    }

    public void setQtyInStock(String qtyInStock) {
        this.qtyInStock = qtyInStock;
    }

    public String getPreferredSupplierId() {
        return preferredSupplierId;
    }

    public void setPreferredSupplierId(String preferredSupplierId) {
        this.preferredSupplierId = preferredSupplierId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // === ビジネスロジック ===

    /**
     * 税込み価格を計算 (Java 21 style)
     */
    public double calculatePriceWithTax() {
        double tax = 10.0;
        if (taxRate != null && !taxRate.isBlank()) {
            try {
                tax = Double.parseDouble(taxRate);
            } catch (NumberFormatException e) {
                tax = 10.0;
            }
        }
        double price = listPrice != null ? listPrice : 0.0;
        return Math.ceil(price * (1.0 + tax / 100.0) * 100.0) / 100.0;
    }

    /**
     * 在庫数を数値で取得
     */
    public int getStockQuantity() {
        if (qtyInStock == null || qtyInStock.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(qtyInStock);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 在庫切れかどうか
     */
    public boolean isOutOfStock() {
        return getStockQuantity() <= 0;
    }

    /**
     * 在庫少かどうか (10冊未満)
     */
    public boolean isLowStock() {
        int qty = getStockQuantity();
        return qty > 0 && qty < 10;
    }

    /**
     * 削除されていないかどうか
     */
    public boolean isActive() {
        return !"1".equals(deleteFlag) && "ACTIVE".equals(status);
    }
}
