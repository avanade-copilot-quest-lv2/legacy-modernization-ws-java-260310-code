# 17. レポートメニュー画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/reports/menu.jsp`  
**URL**: `/reports/menu.do?method=reportsMenu`  
**必要権限**: MANAGER / ADMIN  
**Actionクラス**: `com.example.bookstore.action.ReportAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} ({role})[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Reports                                                                   ║
║                                                                            ║
║  ┌──────────────────────────┐  ┌──────────────────────────┐               ║
║  │  📊 Daily Sales Report   │  │  📚 Sales by Book        │               ║
║  │                          │  │                          │               ║
║  │  View daily sales        │  │  Analyze book sales      │               ║
║  │  summary with date       │  │  performance and         │               ║
║  │  range filtering.        │  │  revenue by title.       │               ║
║  │                          │  │                          │               ║
║  │  [View Report]           │  │  [View Report]           │               ║
║  └──────────────────────────┘  └──────────────────────────┘               ║
║                                                                            ║
║  ┌──────────────────────────┐  ┌──────────────────────────┐               ║
║  │  🏆 Top Books            │  │  ⚠️ Inventory Valuation  │               ║
║  │                          │  │  (ADMIN / Enhanced only) │               ║
║  │  See top performing      │  │                          │               ║
║  │  books by quantity or    │  │  [broken link - 未実装]  │               ║
║  │  revenue.                │  │                          │               ║
║  │                          │  │                          │               ║
║  │  [View Report]           │  │  [View Report]           │               ║
║  └──────────────────────────┘  └──────────────────────────┘               ║
║                                                                            ║
║  « Home                                                                    ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 表示カード一覧

| # | カードタイトル | 説明 | 対応URL | 権限条件 |
|---|--------------|------|--------|----------|
| 1 | Daily Sales Report | 日付範囲でフィルタした日別売上一覧 | `/reports/dailySales.do` | MANAGER/ADMIN |
| 2 | Sales by Book | 書籍別売上パフォーマンス | `/reports/salesByBook.do` | MANAGER/ADMIN |
| 3 | Top Books | 数量/売上別ランキング | `/reports/topBooks.do` | MANAGER/ADMIN |
| 4 | Inventory Valuation | 在庫評価レポート (**未実装 / broken URL**) | `#` (無効リンク) | ADMIN / `ENHANCED_REPORTS`権限 |

> **実装上の問題**: Inventory Valuation カードはURL未設定でリンク無効。Monthly Summaryも未実装。

---

## 権限チェック

```
REQ_ROLE: MANAGER or ADMIN
  (CLERK/USERはアクセス不可 → 04_unauthorized.md へ)

ENHANCED_REPORTS: Inventory Valuationカード表示条件
  session["user"].hasPermission("ENHANCED_REPORTS")
```

---

## ボタン・リンク

| 名前 | 遷移先 |
|------|--------|
| View Report (Daily Sales) | `/reports/dailySales.do?method=dailySales` |
| View Report (Sales by Book) | `/reports/salesByBook.do?method=salesByBook` |
| View Report (Top Books) | `/reports/topBooks.do?method=topBooks` |
| View Report (Inventory Val.) | `#` (未実装) |
| « Home | `/home.do` |

---

## 画面遷移

```
[Daily Sales]   >>> /reports/dailySales.do    (18. 日別売上レポート)
[Sales by Book] >>> /reports/salesByBook.do   (19. 書籍別売上レポート)
[Top Books]     >>> /reports/topBooks.do      (20. 人気書籍ランキング)
[Inventory Val] >>> # (未実装・リンク無効)
« Home          >>> /home.do                  (05. ダッシュボード)
権限不足         >>> /unauthorized.do          (04. 権限エラー)
未ログイン        >>> /login.do               (01. ログイン)
```
