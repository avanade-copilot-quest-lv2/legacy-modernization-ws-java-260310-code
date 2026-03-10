# 14. 低在庫アラート画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/inventory/lowstock.jsp`  
**URL**: `/inventory/lowstock.do?method=lowStock`  
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
║  Low Stock Alert                                                           ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │ ⚠ Attention: The following books are running low on stock.         │   ║
║  │   Please review and create purchase orders as needed.              │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  Low Stock: {lowStockCount}   Critical: {criticalCount}            │   ║
║  │  Out of Stock: {outOfStockCount}           [View All Inventory »]  │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │ ISBN          │ Title                 │  Cat │ Price│Stock│Status│Act║
║  ├───────────────┼───────────────────────┼──────┼──────┼─────┼──────┼───║
║  │ 9784000000002 │ Spring Boot 入門       │  001 │$1,500│   3 │ACTIVE│[Det║
║  │               │ (リンク)              │      │      │LOW  │      │[Adj║
║  │               │                       │      │      │     │      │[Led║
║  ├───────────────┼───────────────────────┼──────┼──────┼─────┼──────┼───║
║  │ 9784000000005 │ Kotlin 実践ガイド      │  001 │$1,800│   1 │ACTIVE│[Det║
║  │               │ (リンク)              │      │      │CRIT │      │[Adj║
║  │               │                       │      │      │     │      │[Led║
║  ├───────────────┼───────────────────────┼──────┼──────┼─────┼──────┼───║
║  │ 9784000000003 │ データベース設計        │  002 │$  980│   0 │ACTIVE│[Det║
║  │               │ (リンク)              │      │      │OUT  │      │[Led║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## アラートバナー

- 赤背景・赤文字のアラートバー
- 低在庫・要確認・発注の促し

---

## サマリーバー - 表示項目

| 項目名 | ソース | 備考 |
|--------|--------|------|
| Low Stock | `session["lowStockCount"]` | 橙色 |
| Critical | `session["criticalCount"]` | 赤色 |
| Out of Stock | `session["outOfStockBooks"].size()` | 濃赤 |

---

## 一覧テーブル列

在庫一覧画面 (10) と同列構成。フィルタリング済みで低在庫書籍のみ表示。

| 列名 | データ |
|------|--------|
| ISBN | `book.isbn` |
| Title | `book.title` (詳細へのリンク) |
| Category | `book.categoryId` |
| Price | `book.listPrice` |
| Stock | `book.qtyInStock` (色分け) |
| Status | `book.status` |
| Action | Detail / Adjust / Ledger |

データソース: `session["lowStockBooks"]`

---

## ボタン・リンク

| リンク | 遷移先 | 表示条件 |
|--------|--------|---------|
| View All Inventory » | `/inventory/list.do?method=list` | 全ロール |
| Detail | `/inventory/detail.do?method=detail&bookId={id}` | 全ロール |
| Adjust | `/inventory/adjust.do?method=adjustStock&bookId={id}` | MANAGER/ADMIN |
| Ledger | `/inventory/ledger.do?method=ledger&bookId={id}` | 全ロール |

---

## 画面遷移

```
[View All Inventory] >>> /inventory/list.do    (10. 在庫一覧)
[Detail]             >>> /inventory/detail.do  (11. 在庫詳細)
[Adjust]             >>> /inventory/adjust.do  (12. 在庫調整)  ※MANAGER/ADMIN
[Ledger]             >>> /inventory/ledger.do  (13. 在庫台帳)
未ログイン            >>> /login.do             (01. ログイン)
```
