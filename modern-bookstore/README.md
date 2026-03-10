# ? Modern Bookstore - 書籍検索機能

レガシーシステム (Struts 1.x + Java 5) からの段階的モダナイゼーションプロジェクト

## ? 技術スタック比較

| 項目 | レガシー | モダン |
|------|---------|--------|
| Java | 5 (1.5) | **21** |
| Framework | Struts 1.3 | **Spring Boot 3.2** |
| ORM | 直接JDBC + Hibernate 2.x | **Spring Data JPA** |
| Template | JSP | **Thymeleaf** |
| Build | Ant | **Maven** |
| Container | Tomcat 6 | **Embedded Tomcat 10** |

## ? URL マッピング

```
レガシー:  http://localhost:8080/bookstore/book/search.do
  ↓ Nginx等でURL振り分け
モダン:    http://localhost:8081/book/search
```

## ? プロジェクト構成

```
modern-bookstore/
├── src/main/java/com/example/bookstore/modern/
│   ├── ModernBookstoreApplication.java   # Spring Boot メイン
│   ├── entity/
│   │   ├── Book.java                     # 書籍エンティティ
│   │   └── Category.java                 # カテゴリエンティティ
│   ├── repository/
│   │   ├── BookRepository.java          # Spring Data JPA
│   │   └── CategoryRepository.java
│   ├── service/
│   │   └── BookSearchService.java       # ビジネスロジック
│   └── controller/
│       ├── BookSearchController.java    # Web UI (Thymeleaf)
│       └── BookSearchApiController.java # REST API
├── src/main/resources/
│   ├── application.yml                   # 設定ファイル
│   └── templates/
│       └── search.html                   # 検索画面
├── Dockerfile
└── pom.xml
```

## ? モダナイゼーションのポイント

### 1. クリーンなコード
**Before (305行 + 直接JDBC):**
```java
// レガシー: BookAction.java
Connection conn = null;
Statement stmt = null;
ResultSet rs = null;
try {
    Class.forName("com.mysql.jdbc.Driver");
    conn = DriverManager.getConnection(
        "jdbc:mysql://legacy-mysql:3306/legacy_db?useSSL=false", 
        "legacy_user", "legacy_pass");
    stmt = conn.createStatement();
    StringBuffer sql = new StringBuffer("SELECT * FROM books WHERE...");
    // 100行以上のSQL組み立てロジック...
}
```

**After (Spring Data JPA):**
```java
// モダン: BookRepository.java
@Query("SELECT b FROM Book b WHERE (:title IS NULL OR b.title LIKE %:title%)")
List<Book> searchBooks(@Param("title") String title);
```

### 2. 依存性注入
**Before:**
```java
BookstoreManager.getInstance().searchBooks(...)  // シングルトン
```

**After:**
```java
@Autowired
private BookSearchService bookSearchService;  // DI
```

### 3. レスポンシブUI
- モダンなThymeleafテンプレート
- グリッドレイアウト
- 検索時間表示（パフォーマンス可視化）

## ? 起動方法

```bash
# .devcontainer ディレクトリで実行
docker compose -f compose.services.yaml up -d

# アクセス先
# レガシー: http://localhost:8080/bookstore/book/search.do
# モダン:   http://localhost:8081/book/search
# API:     http://localhost:8081/api/books/search
```

## ? 発表デモ用シナリオ

1. **両方の画面を並べて表示**
   - レガシー (8080) vs モダン (8081)

2. **同じ検索を実行**
   - 検索時間の違いを比較
   - UIの違いを見せる

3. **コード比較**
   - BookAction.java (305行) vs BookSearchService.java (100行)
   - 直接JDBC vs Spring Data JPA

4. **アーキテクチャ説明**
   ```
   ┌─────────────┐     ┌─────────────┐
   │  Legacy     │     │  Modern     │
   │  Struts 1.x │     │  Spring     │
   │  :8080      │     │  Boot :8081 │
   └──────┬──────┘     └──────┬──────┘
          │                   │
          └─────────┬─────────┘
                    │
            ┌───────┴───────┐
            │   MySQL 5.7   │
            │  (共有DB)     │
            └───────────────┘
   ```

## ? 次のステップ（将来の拡張）

- [ ] 認証機能の移行 (Spring Security)
- [ ] 他の機能の段階的移行
- [ ] フロントエンドのReact化
- [ ] API Gateway の導入
- [ ] マイクロサービス化

---

**? コパクエ ハッカソン - レガシーモダナイゼーションクエスト**
