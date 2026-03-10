package com.example.bookstore.modern.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Book エンティティのユニットテスト
 * 
 * ビジネスロジックメソッドをテスト
 */
class BookTest {

    @Nested
    @DisplayName("calculatePriceWithTax - 税込価格計算")
    class CalculatePriceWithTaxTest {

        @Test
        @DisplayName("税率10%で税込価格を計算できる")
        void calculatePriceWithTax_10percent() {
            Book book = new Book();
            book.setListPrice(1000.0);
            book.setTaxRate("10");

            double result = book.calculatePriceWithTax();

            assertThat(result).isEqualTo(1100.0);
        }

        @Test
        @DisplayName("税率8%で税込価格を計算できる")
        void calculatePriceWithTax_8percent() {
            Book book = new Book();
            book.setListPrice(1000.0);
            book.setTaxRate("8");

            double result = book.calculatePriceWithTax();

            assertThat(result).isEqualTo(1080.0);
        }

        @Test
        @DisplayName("税率nullの場合、デフォルト10%を使用する")
        void calculatePriceWithTax_nullRate_usesDefault() {
            Book book = new Book();
            book.setListPrice(1000.0);
            book.setTaxRate(null);

            double result = book.calculatePriceWithTax();

            assertThat(result).isEqualTo(1100.0);
        }

        @Test
        @DisplayName("価格nullの場合、0を返す")
        void calculatePriceWithTax_nullPrice_returnsZero() {
            Book book = new Book();
            book.setListPrice(null);
            book.setTaxRate("10");

            double result = book.calculatePriceWithTax();

            assertThat(result).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("getStockQuantity - 在庫数取得")
    class GetStockQuantityTest {

        @Test
        @DisplayName("在庫数を正しく取得できる")
        void getStockQuantity_valid() {
            Book book = new Book();
            book.setQtyInStock("45");

            int result = book.getStockQuantity();

            assertThat(result).isEqualTo(45);
        }

        @Test
        @DisplayName("在庫nullの場合、0を返す")
        void getStockQuantity_null_returnsZero() {
            Book book = new Book();
            book.setQtyInStock(null);

            int result = book.getStockQuantity();

            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("在庫が空文字の場合、0を返す")
        void getStockQuantity_empty_returnsZero() {
            Book book = new Book();
            book.setQtyInStock("");

            int result = book.getStockQuantity();

            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("在庫が数字でない場合、0を返す")
        void getStockQuantity_notNumber_returnsZero() {
            Book book = new Book();
            book.setQtyInStock("abc");

            int result = book.getStockQuantity();

            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("isOutOfStock - 在庫切れ判定")
    class IsOutOfStockTest {

        @Test
        @DisplayName("在庫0の場合、trueを返す")
        void isOutOfStock_zero_returnsTrue() {
            Book book = new Book();
            book.setQtyInStock("0");

            assertThat(book.isOutOfStock()).isTrue();
        }

        @Test
        @DisplayName("在庫ありの場合、falseを返す")
        void isOutOfStock_hasStock_returnsFalse() {
            Book book = new Book();
            book.setQtyInStock("10");

            assertThat(book.isOutOfStock()).isFalse();
        }
    }

    @Nested
    @DisplayName("isLowStock - 在庫少判定")
    class IsLowStockTest {

        @Test
        @DisplayName("在庫9以下の場合、trueを返す")
        void isLowStock_lessThan10_returnsTrue() {
            Book book = new Book();
            book.setQtyInStock("9");

            assertThat(book.isLowStock()).isTrue();
        }

        @Test
        @DisplayName("在庫10以上の場合、falseを返す")
        void isLowStock_10orMore_returnsFalse() {
            Book book = new Book();
            book.setQtyInStock("10");

            assertThat(book.isLowStock()).isFalse();
        }

        @Test
        @DisplayName("在庫0の場合、falseを返す(在庫切れなのでLowStockではない)")
        void isLowStock_zero_returnsFalse() {
            Book book = new Book();
            book.setQtyInStock("0");

            assertThat(book.isLowStock()).isFalse();
        }
    }

    @Nested
    @DisplayName("isActive - アクティブ判定")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVEで削除フラグなしの場合、trueを返す")
        void isActive_activeNotDeleted_returnsTrue() {
            Book book = new Book();
            book.setStatus("ACTIVE");
            book.setDeleteFlag("0");

            assertThat(book.isActive()).isTrue();
        }

        @Test
        @DisplayName("削除済みの場合、falseを返す")
        void isActive_deleted_returnsFalse() {
            Book book = new Book();
            book.setStatus("ACTIVE");
            book.setDeleteFlag("1");

            assertThat(book.isActive()).isFalse();
        }

        @Test
        @DisplayName("ステータスがACTIVEでない場合、falseを返す")
        void isActive_notActive_returnsFalse() {
            Book book = new Book();
            book.setStatus("DRAFT");
            book.setDeleteFlag("0");

            assertThat(book.isActive()).isFalse();
        }
    }
}
