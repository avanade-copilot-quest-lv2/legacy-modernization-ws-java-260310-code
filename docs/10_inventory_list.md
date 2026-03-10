# 10. 在庫一覧画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/inventory/list.jsp`  
**URL**: `/inventory/list.do?method=list`  
**必要権限**: ログイン済み  
**Actionクラス**: `com.example.bookstore.action.InventoryAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} ({role})[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Inventory List                                                            ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  Total Books: {n}   Low Stock: {lowStockCount}   Critical: {cnt}   │   ║
║  │                                        [View Low Stock »]          │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  Search: [____________________]   [ Filter ]   [ Show All ]        │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │ ISBN          │ Title                │  Cat │ Price│Stock│Status│Action║
║  ├───────────────┼──────────────────────┼──────┼──────┼─────┼──────┼─────║
║  │ 9784000000001 │ Java プログラミング    │  001 │$1,200│  45 │ACTIVE│[Det]║
║  │               │                      │      │      │     │      │[Adj]║
║  │               │                      │      │      │     │      │[Led]║
║  ├───────────────┼──────────────────────┼──────┼──────┼─────┼──────┼─────║
║  │ 9784000000002 │ Spring Boot 入門      │  001 │$1,500│   3 │ACTIVE│[Det]║
║  │               │(リンク)               │      │      │LOW  │      │[Adj]║
║  │               │                      │      │      │     │      │[Led]║
║  ├───────────────┼──────────────────────┼──────┼──────┼─────┼──────┼─────║
║  │ 9784000000003 │ データベース設計       │  002 │$  980│   0 │ACTIVE│[Det]║
║  │               │                      │      │      │OUT  │      │[Led]║
║  └────────────────────────────────────────────────────────────────────┘   ║
║  Total: {n} books                                                          ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## サマリーバー - 表示項目

| 項目名 | ソース | 備考 |
|--------|--------|------|
| Total Books | `books.size()` | |
| Low Stock | `session["lowStockCount"]` | 橙色 |
| Critical | `session["criticalCount"]` | 赤色 |

---

## フィルターフォーム - 入力項目

| # | 項目名 | name | 備考 |
|---|--------|------|------|
| 1 | キーワード | `keyword` | タイトル等の部分一致 |

---

## 一覧テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| ISBN | `book.isbn` | |
| Title | `book.title` | 詳細画面へのリンク |
| Category | `book.categoryId` | |
| Price | `book.listPrice` | `$` 付き右揃え |
| Stock | `book.qtyInStock` | 在庫カラーで色分け |
| Status | `book.status` | |
| Action | Detail / Adjust / Ledger | |

---

## 在庫表示カラー

| ステータス | 条件 | 色 |
|----------|------|-----|
| (正常) | 在庫 > 10 | 緑 |
| LOW | 在庫 4〜10 | 橙・太字 |
| CRITICAL | 在庫 1〜3 | 赤・太字 |
| OUT | 在庫 = 0 | 濃赤・太字・小文字 |

---

## アクションリンク

| リンク | 遷移先 | 表示条件 |
|--------|--------|---------|
| Detail | `/inventory/detail.do?method=detail&bookId={id}` | 全ロール |
| Adjust | `/inventory/adjust.do?method=adjustStock&bookId={id}` | MANAGER/ADMIN のみ |
| Ledger | `/inventory/ledger.do?method=ledger&bookId={id}` | 全ロール |

---

## 画面遷移

```
[Detail]        >>> /inventory/detail.do   (11. 在庫詳細)
[Adjust]        >>> /inventory/adjust.do   (12. 在庫調整)  ※MANAGER/ADMIN
[Ledger]        >>> /inventory/ledger.do   (13. 在庫台帳)
[View Low Stock] >>> /inventory/lowstock.do (14. 低在庫アラート)
[Filter]        >>> 同画面 (絞り込み)
[Show All]      >>> /inventory/list.do     (同画面・全件表示)
未ログイン       >>> /login.do             (01. ログイン)
```
