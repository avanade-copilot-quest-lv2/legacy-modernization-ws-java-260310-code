# 22. 管理者ダッシュボード画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/admin/home.jsp`  
**URL**: `/admin/home.do?method=adminHome`  
**必要権限**: ADMIN のみ  
**Actionクラス**: `com.example.bookstore.action.AdminAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} (ADMIN)[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Admin Dashboard                                                           ║
║                                                                            ║
║  ╔═══════════════════╗  ╔═══════════════════╗                              ║
║  ║  👤 Total Users   ║  ║  📖 Total Books   ║                              ║
║  ║                   ║  ║                   ║                              ║
║  ║       42          ║  ║      1,250        ║                              ║
║  ║                   ║  ║                   ║                              ║
║  ║  Registered users ║  ║  In catalog       ║                              ║
║  ╚═══════════════════╝  ╚═══════════════════╝                              ║
║                                                                            ║
║  ╔═══════════════════╗  ╔═══════════════════╗                              ║
║  ║  🛒 Total Orders  ║  ║  💰 Sales (7 days)║                              ║
║  ║                   ║  ║                   ║                              ║
║  ║       387         ║  ║    $24,500.00     ║                              ║
║  ║                   ║  ║                   ║                              ║
║  ║  All time         ║  ║  Last 7 days      ║                              ║
║  ╚═══════════════════╝  ╚═══════════════════╝                              ║
║                                                                            ║
║  ── Admin Operations ────────────────────────────────────                 ║
║                                                                            ║
║  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐        ║
║  │  👤 User Mgmt    │  │  📁 Category Mgmt│  │  📊 Reports      │        ║
║  │                  │  │                  │  │                  │        ║
║  │  Manage system   │  │  Manage book     │  │  View system     │        ║
║  │  users & roles   │  │  categories      │  │  reports         │        ║
║  │                  │  │                  │  │                  │        ║
║  │  [Manage Users]  │  │  [Manage Cat.]   │  │  [View Reports]  │        ║
║  └──────────────────┘  └──────────────────┘  └──────────────────┘        ║
║                                                                            ║
║  ┌──────────────────┐                                                      ║
║  │  📋 Audit Log    │                                                      ║
║  │                  │                                                      ║
║  │  View system     │                                                      ║
║  │  activity log    │                                                      ║
║  │                  │                                                      ║
║  │  [View Audit Log]│                                                      ║
║  └──────────────────┘                                                      ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 統計カード

| カード | データ | データソース | 備考 |
|--------|--------|------------|------|
| Total Users | ユーザー総数 | JSP内JDBC直接クエリ | 5分間キャッシュ |
| Total Books | 書籍カタログ総数 | JSP内JDBC直接クエリ | 5分間キャッシュ |
| Total Orders | 累計注文数 | JSP内JDBC直接クエリ | 5分間キャッシュ |
| Sales (7 days) | 直近7日間売上額 | JSP内JDBC直接クエリ | `$` 付き・5分間キャッシュ |

---

## 管理操作カード

| カード | ボタン | 遷移先 |
|--------|--------|--------|
| User Management | Manage Users | `/admin/user/list.do` |
| Category Management | Manage Categories | `/admin/category/list.do` |
| Reports | View Reports | `/reports/menu.do` |
| Audit Log | View Audit Log | `/audit/log.do` |

---

## セキュリティ上の問題点 ⚠️

| 問題 | 詳細 |
|------|------|
| JSP内でJDBC直接SQL実行 | DAO/Serviceレイヤを経由せずJSP内で直接DBアクセス |
| 静的変数でのキャッシュ | JSP内の静的変数フィールドでキャッシュ (クラスタ環境で非同期) |
| 5分TTLキャッシュ | `System.currentTimeMillis()` ベースの簡易キャッシュ |

---

## 画面遷移

```
[Manage Users]     >>> /admin/user/list.do      (23. ユーザー一覧)
[Manage Categories]>>> /admin/category/list.do  (25. カテゴリ一覧)
[View Reports]     >>> /reports/menu.do         (17. レポートメニュー)
[View Audit Log]   >>> /audit/log.do            (21. 監査ログ)
権限不足 (非ADMIN)  >>> /unauthorized.do        (04. 権限エラー)
未ログイン           >>> /login.do              (01. ログイン)
```
