# 05. ダッシュボード

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/home.jsp`  
**URL**: `/home.do`  
**必要権限**: ログイン済み  
**Actionクラス**: `com.example.bookstore.action.HomeAction`

---

## 画面レイアウト

```
╔══════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]       ║
║                                         User: {username} ({role})[Logout]║
╠══════════════════════════════════════════════════════════════════════════╣
║                                                                          ║
║  ┌──────────────────────────────────────────────────────────────────┐   ║
║  │  Dashboard                                                        │   ║
║  │  Welcome, {username} ({role}) | {currentTime}                    │   ║
║  └──────────────────────────────────────────────────────────────────┘   ║
║                                                                          ║
║  [エラーメッセージ / 成功メッセージ (条件付き表示)]                         ║
║                                                                          ║
║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐         ║
║  │ 📚 Books        │  │ 💰 Sales        │  │ 📦 Inventory    │         ║
║  │                 │  │                 │  │                 │         ║
║  │   {bookCount}   │  │  {orderCount}   │  │  {lowStockCount}│         ║
║  │                 │  │                 │  │  ※低在庫数      │         ║
║  │ Total books in  │  │ Total orders    │  │ Low stock items │         ║
║  │ catalog         │  │                 │  │                 │         ║
║  │ [Search Books]  │  │ [New Sale]      │  │ [View Inventory]│         ║
║  │                 │  │                 │  │ [Low Stock]     │         ║
║  └─────────────────┘  └─────────────────┘  └─────────────────┘         ║
║                                                                          ║
║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐         ║
║  │ 🏪 Suppliers    │  │ 📊 Reports      │  │ ⚙ Admin        │         ║
║  │  ※全ロール      │  │  ※MANAGER以上  │  │  ※ADMINのみ    │         ║
║  │                 │  │                 │  │                 │         ║
║  │ [View Suppliers]│  │ [View Reports]  │  │ [Admin Panel]   │         ║
║  │ [Purchase Orders│  │                 │  │                 │         ║
║  └─────────────────┘  └─────────────────┘  └─────────────────┘         ║
║                                                                          ║
║  ┌──────────────────────────────────────────────────────────────────┐   ║
║  │  Quick Actions                                                    │   ║
║  │  [New Sale] [Search Books] [Low Stock Alert] [View Reports]       │   ║
║  └──────────────────────────────────────────────────────────────────┘   ║
║                                                                          ║
╠══════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                      ║
╚══════════════════════════════════════════════════════════════════════════╝
```

---

## 表示項目 (ダッシュボードカード)

| カード | 表示データ | ソース (セッション属性) | 表示条件 |
|--------|-----------|----------------------|---------|
| Books | 書籍総数 | `session["bookCount"]` | 全ロール |
| Sales | 総注文数 | `session["orderCount"]` | 全ロール |
| Inventory | 低在庫書籍数 | `session["lowStockCount"]` | 全ロール |
| Suppliers | — | — | 全ロール |
| Reports | — | — | MANAGER/ADMIN のみ |
| Admin | — | — | ADMIN のみ |

---

## ボタン・リンク

| リンク名 | 遷移先 URL | 表示条件 |
|---------|----------|---------|
| Search Books | `/book/search.do` | 全ロール |
| New Sale | `/sales/entry.do?method=entry` | 全ロール |
| View Inventory | `/inventory/list.do?method=list` | 全ロール |
| Low Stock | `/inventory/lowstock.do?method=lowStock` | 全ロール |
| View Suppliers | `/supplier/list.do?method=supplierList` | 全ロール |
| Purchase Orders | `/purchaseorder/list.do?method=poList` | 全ロール |
| View Reports | `/reports/menu.do` | MANAGER/ADMIN |
| Admin Panel | `/admin/home.do?method=home` (予想) | ADMIN |
| Audit Log | `/audit/log.do` | MANAGER/ADMIN |

---

## 画面遷移

```
[Search Books]    >>> /book/search.do              (06. 書籍検索)
[New Sale]        >>> /sales/entry.do?method=entry (07. 販売入力)
[View Inventory]  >>> /inventory/list.do            (10. 在庫一覧)
[Low Stock]       >>> /inventory/lowstock.do        (14. 低在庫アラート)
[View Suppliers]  >>> /supplier/list.do             (16. 仕入先一覧)
[Purchase Orders] >>> /purchaseorder/list.do        (15. 発注書一覧)
[View Reports]    >>> /reports/menu.do              (17. レポートメニュー)
[Admin Panel]     >>> /admin/home.do                (22. 管理者ダッシュボード)
[Logout]          >>> /logout.do                   (01. ログイン)
```
