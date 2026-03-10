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

## ? URL マッピング（Nginx による自動振り分け）

```
http://localhost/...
        ↓
    [Nginx :80]
        ↓ URLパスで振り分け
   ┌────┴────┐
   ↓         ↓
/book/search  それ以外
   ↓         ↓
[モダン]   [レガシー]
```

| URL | 振り分け先 |
|-----|-----------|
| `http://localhost/book/search` | **モダン** (Spring Boot) |
| `http://localhost/api/*` | **モダン** (Spring Boot) |
| `http://localhost/bookstore/*` | レガシー (Struts) |
| その他 | レガシー (Struts) |

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

# アクセス先（Nginx経由 - ポート80）
# トップ:       http://localhost/bookstore/
# 書籍検索:     http://localhost/book/search     ← モダン版に自動ルーティング！
# API:         http://localhost/api/books/search

# 直接アクセス（デバッグ用）
# レガシー直:   http://localhost:8080/bookstore/book/search.do
# モダン直:     http://localhost:8081/book/search
```

## ? 発表デモ用シナリオ

1. **トップページからスタート**
   - `http://localhost/bookstore/` にアクセス（レガシー）
   - ログインして操作

2. **書籍検索をクリック**
   - URLが `/book/search` に変わる
   - → **自動的にモダン版にルーティング！** ?
   - UIが明らかに変わる（グラデーション背景、カード表示）

3. **検索実行して速度比較**
   - モダン版: 検索時間が画面に表示される
   - 開発者ツールで `X-Routed-To: modern-bookstore` ヘッダーを確認

4. **コード比較**
   - BookAction.java (305行) vs BookSearchService.java (100行)
   - 直接JDBC vs Spring Data JPA

4. **アーキテクチャ説明**
   ```
                    [ユーザー]
                        │
                        ▼
               ┌────────────────┐
               │  Nginx :80     │
               │  (リバプロ)     │
               └───────┬────────┘
                       │ URLで振り分け
          ┌────────────┴────────────┐
          ▼                         ▼
   ┌─────────────┐          ┌─────────────┐
   │  Legacy     │          │  Modern     │
   │  Struts 1.x │          │  Spring     │
   │  :8080      │          │  Boot :8081 │
   └──────┬──────┘          └──────┬──────┘
          │                        │
          └───────────┬────────────┘
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
