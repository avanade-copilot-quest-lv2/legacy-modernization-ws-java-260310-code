# 23. ユーザー一覧画面 (管理者)

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/admin/user/list.jsp`  
**URL**: `/admin/user/list.do?method=userList`  
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
║  User Management                                                           ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  [ + Add New User ]                                     Admin Panel│   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │ #  │ Username  │ Role    │ Status   │ Created     │ Actions        │   ║
║  ├────┼───────────┼─────────┼──────────┼─────────────┼────────────────┤   ║
║  │  1 │ admin     │ ADMIN   │ [Active] │ 2025-01-01  │ [Edit]         │   ║
║  │  2 │ manager1  │ MANAGER │ [Active] │ 2025-02-15  │ [Edit][Deact.] │   ║
║  │  3 │ clerk1    │ CLERK   │ [Active] │ 2025-03-10  │ [Edit][Deact.] │   ║
║  │  4 │ user001   │ USER    │[Inactive]│ 2025-04-20  │ [Edit][Activ.] │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║  Total: {n} users                                                          ║
║                                                                            ║
║  « Back to Admin                                                           ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 一覧テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| # | 行番号 | 1始まり |
| Username | `user.username` | 太字 |
| Role | `user.role` | |
| Status | `user.active` | ステータスバッジ (下記参照) |
| Created | `user.createdDate` | `yyyy-MM-dd` |
| Actions | アクションボタン群 | (下記参照) |

データソース: `session["userList"]`

---

## ステータスバッジ

| active | 表示 | 色 |
|--------|------|-----|
| `true` | [Active] | 緑 (`text-success`) |
| `false` | [Inactive] | 赤 (`text-danger`) |

---

## アクションボタン

| ボタン | 表示条件 | アクション |
|--------|----------|----------|
| Edit | 全ユーザー | `/admin/user/edit.do?id={userId}` |
| Deactivate | `active=true` | `/admin/user/deactivate.do?id={userId}` |
| Activate | `active=false` | `/admin/user/activate.do?id={userId}` |

---

## ボタン・リンク

| 名前 | 遷移先 |
|------|--------|
| + Add New User | `/admin/user/add.do` |
| « Back to Admin | `/admin/home.do` |

---

## 画面遷移

```
[+ Add New User] >>> /admin/user/add.do    (24. ユーザー追加フォーム)
[Edit]           >>> /admin/user/edit.do   (24. ユーザー編集フォーム)
[Deactivate]     >>> /admin/user/deactivate.do (同画面リロード)
[Activate]       >>> /admin/user/activate.do   (同画面リロード)
« Back to Admin  >>> /admin/home.do        (22. 管理者ダッシュボード)
権限不足           >>> /unauthorized.do    (04. 権限エラー)
未ログイン          >>> /login.do          (01. ログイン)
```
