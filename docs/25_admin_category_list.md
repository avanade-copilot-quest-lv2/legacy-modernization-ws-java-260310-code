# 25. カテゴリ一覧画面 (管理者)

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/admin/category/list.jsp`  
**URL**: `/admin/category/list.do?method=categoryList`  
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
║  Category Management                                                       ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  [ + Add New Category ]                             Admin Panel    │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │ #  │ Name           │ Description                │ Created  │Act. │   ║
║  ├────┼────────────────┼────────────────────────────┼──────────┼─────┤   ║
║  │  1 │ Technology     │ Programming & CS books      │2025-01-01│[E][D]│   ║
║  │  2 │ Science        │ Science & nature books      │2025-01-15│[E][D]│   ║
║  │  3 │ Literature     │ Fiction & classic           │2025-02-10│[E][D]│   ║
║  │  4 │ Business       │ Management & finance        │2025-03-05│[E][D]│   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║  Total: {n} categories                                                     ║
║                                                                            ║
║  ── 削除確認ダイアログ (Deleteクリック時) ─────────────────                 ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  ┌──────────────────────────────────────────────────────────────┐ │   ║
║  │  │              Confirm Delete                                  │ │   ║
║  │  │                                                              │ │   ║
║  │  │  Are you sure you want to delete "{categoryName}"?           │ │   ║
║  │  │                                                              │ │   ║
║  │  │                   [ Delete ]  [ Cancel ]                     │ │   ║
║  │  └──────────────────────────────────────────────────────────────┘ │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
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
| Name | `cat.name` | 太字 |
| Description | `cat.description` | 省略表示の場合あり |
| Created | `cat.createdDate` | `yyyy-MM-dd` |
| Actions | [E] Edit / [D] Delete | アクションボタン |

データソース: `session["categoryList"]`

---

## アクションボタン

| ボタン | アクション | 備考 |
|--------|----------|------|
| [E] Edit | `/admin/category/edit.do?id={catId}` | |
| [D] Delete | JavaScript確認ダイアログ後 `/admin/category/delete.do?id={catId}` | 確認ダイアログあり |

---

## 削除確認ダイアログ

JavaScript `confirm()` または モーダルダイアログ:
```
"Are you sure you want to delete '{categoryName}'?"
  [ Delete ]  [ Cancel ]
```

---

## ボタン・リンク

| 名前 | 遷移先 |
|------|--------|
| + Add New Category | `/admin/category/add.do` |
| « Back to Admin | `/admin/home.do` |

---

## 画面遷移

```
[+ Add New Category] >>> /admin/category/add.do    (26. カテゴリフォーム)
[Edit]               >>> /admin/category/edit.do   (26. カテゴリフォーム)
[Delete] (確認後)     >>> /admin/category/delete.do (同画面リロード)
« Back to Admin      >>> /admin/home.do             (22. 管理者ダッシュボード)
権限不足               >>> /unauthorized.do         (04. 権限エラー)
未ログイン              >>> /login.do               (01. ログイン)
```
