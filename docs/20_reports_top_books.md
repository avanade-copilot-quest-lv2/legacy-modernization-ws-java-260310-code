# 20. 人気書籍ランキングレポート画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/reports/top-books.jsp`  
**URL**: `/reports/topBooks.do?method=topBooks`  
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
║  Top Selling Books                                                         ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  From: [2026-01-01] To: [2026-01-31]                               │   ║
║  │                                                                    │   ║
║  │  Rank By: [ ▼ Quantity Sold ]   Top N: [ ▼ 10 ]                   │   ║
║  │              Quantity Sold                5                        │   ║
║  │              Revenue                     10                        │   ║
║  │                                          20                        │   ║
║  │                                          50                        │   ║
║  │                                                      [ View Report ]│   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ── 結果表示エリア (送信後) ─────────────────────────────────────────────   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  Top 10 Books by Quantity Sold             [Export CSV]            │   ║
║  │  Period: 2026-01-01 to 2026-01-31                                  │   ║
║  │                                                                    │   ║
║  │ Rank│ ISBN          │ Title           │ Category │ Qty │ Revenue   │   ║
║  │ ────┼───────────────┼─────────────────┼──────────┼─────┼──────── │   ║
║  │[🥇1]│978-4-00-0001  │ Java入門         │ Tech     │  45 │  $4,500  │   ║
║  │[🥈2]│978-4-00-0002  │ Spring Boot実践  │ Tech     │  38 │  $5,700  │   ║
║  │[🥉3]│978-4-00-0003  │ データ構造       │ CS       │  30 │  $3,600  │   ║
║  │  4  │978-4-00-0004  │ Python入門       │ Tech     │  25 │  $2,500  │   ║
║  │  5  │978-4-00-0005  │ Clean Code       │ Design   │  20 │  $3,000  │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  « Back to Reports                                                         ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## フィルターフォーム - 入力項目

| # | 項目名 | name | 型 | 選択肢 / バリデーション |
|---|--------|------|----|----------------------|
| 1 | From (開始日) | `startDate` | テキスト | 書式: `yyyy-MM-dd` |
| 2 | To (終了日) | `endDate` | テキスト | 書式: `yyyy-MM-dd` |
| 3 | Rank By | `rankBy` | セレクト | `quantity` = Quantity Sold / `revenue` = Revenue |
| 4 | Top N | `topN` | セレクト | `5` / `10` (デフォルト) / `20` / `50` |

---

## 結果テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| Rank | ランク番号 | 1位: 金バッジ🥇、2位: 銀バッジ🥈、3位: 銅バッジ🥉、4位以降: 数字のみ |
| ISBN | `row.isbn` | 等幅フォント |
| Title | `row.title` | |
| Category | `row.categoryName` | |
| Qty Sold | `row.qtySold` | `rankBy=quantity` の場合は太字 |
| Revenue | `row.revenue` | `$` 付き・`rankBy=revenue` の場合は太字 |

データソース: `session["topBooksData"]`

---

## ランクバッジ カラーコード

| ランク | バッジ | 背景色 |
|-------|--------|-------|
| 1位 | 🥇 `1` | 金色 (`#FFD700`) |
| 2位 | 🥈 `2` | 銀色 (`#C0C0C0`) |
| 3位 | 🥉 `3` | 銅色 (`#CD7F32`) |
| 4位以降 | 数字 | 通常背景 |

---

## ボタン・リンク

| 名前 | アクション |
|------|----------|
| View Report | フォーム送信 (POST) |
| Export CSV | CSVファイルダウンロード |
| « Back to Reports | `/reports/menu.do?method=reportsMenu` |

---

## 画面遷移

```
[View Report]       >>> 同画面 (フィルタ適用後)
[Export CSV]        >>> CSVファイルダウンロード
« Back to Reports   >>> /reports/menu.do  (17. レポートメニュー)
権限不足             >>> /unauthorized.do  (04. 権限エラー)
未ログイン            >>> /login.do        (01. ログイン)
```
