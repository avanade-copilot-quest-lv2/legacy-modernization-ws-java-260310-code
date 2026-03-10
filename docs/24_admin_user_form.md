# 24. ユーザーフォーム画面 (管理者 - 追加/編集)

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/admin/user/form.jsp`  
**URL (追加)**: `/admin/user/add.do`  
**URL (編集)**: `/admin/user/edit.do?id={userId}`  
**必要権限**: ADMIN のみ  
**Actionクラス**: `com.example.bookstore.action.AdminAction`

---

## 画面レイアウト - 追加モード (mode=0)

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} (ADMIN)[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Add New User                                                              ║
║  [エラーメッセージ (条件付き)]                                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │                                                                    │   ║
║  │  Username:         [                              ]                │   ║
║  │                                                                    │   ║
║  │  Password:         [                              ]                │   ║
║  │                    (最低4文字)                                      │   ║
║  │                                                                    │   ║
║  │  Confirm Password: [                              ]                │   ║
║  │                    (追加モードのみ表示)                              │   ║
║  │                                                                    │   ║
║  │  Role:             [▼ CLERK                       ]                │   ║
║  │                        ADMIN                                       │   ║
║  │                        MANAGER                                     │   ║
║  │                        CLERK                                       │   ║
║  │                        USER                                        │   ║
║  │                                                                    │   ║
║  │  Active:           [▼ Yes                         ]                │   ║
║  │                        Yes                                         │   ║
║  │                        No                                          │   ║
║  │                                                                    │   ║
║  │                    [ Save ]   [ Cancel ]                           │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 画面レイアウト - 編集モード (mode=1)

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} (ADMIN)[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Edit User: {username}                                                     ║
║  [エラーメッセージ (条件付き)]                                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │                                                                    │   ║
║  │  Username:   [clerk1          ] (読み取り専用 / disabled)           │   ║
║  │                                                                    │   ║
║  │  Password:   [                ] (空欄 = 変更なし)                   │   ║
║  │                                                                    │   ║
║  │  ※ Confirm Password 欄は表示されない                                 │   ║
║  │                                                                    │   ║
║  │  Role:       [▼ CLERK         ]                                    │   ║
║  │                  ADMIN                                             │   ║
║  │                  MANAGER                                           │   ║
║  │                  CLERK                                             │   ║
║  │                  USER                                              │   ║
║  │                                                                    │   ║
║  │  Active:     [▼ Yes           ]                                    │   ║
║  │                  Yes                                               │   ║
║  │                  No                                                │   ║
║  │                                                                    │   ║
║  │              [ Save ]   [ Cancel ]                                 │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## フォーム入力項目

| # | 項目名 | name | 型 | 表示条件 | バリデーション |
|---|--------|------|----|--------|--------------|
| 1 | Username | `username` | テキスト | 常に表示 (編集時はdisabled) | 必須 |
| 2 | Password | `password` | パスワード | 常に表示 | 必須(追加時)・最低4文字 |
| 3 | Confirm Password | `confirmPassword` | パスワード | **追加モードのみ** | passwordと一致 |
| 4 | Role | `role` | セレクト | 常に表示 | `ADMIN` / `MANAGER` / `CLERK` / `USER` |
| 5 | Active | `active` | セレクト | 常に表示 | `true`=Yes / `false`=No |

ビューモード判定: `request["mode"]` = `0`(追加) / `1`(編集)

---

## バリデーションルール

| 項目 | ルール |
|------|--------|
| Password | 4文字以上必須 |
| Confirm Password | `password` フィールドと一致すること (追加時のみ) |
| Username | 編集時は変更不可 (disabled フィールド) |

---

## ボタン

| ボタン | アクション |
|--------|----------|
| Save | フォーム送信 (追加: `/admin/user/save.do` / 編集: `/admin/user/update.do`) |
| Cancel | `/admin/user/list.do` に戻る |

---

## 画面遷移

```
[Save] (追加成功) >>> /admin/user/list.do  (23. ユーザー一覧) + 成功メッセージ
[Save] (編集成功) >>> /admin/user/list.do  (23. ユーザー一覧) + 成功メッセージ
[Save] (バリデ失敗)>>> 同画面 + エラーメッセージ
[Cancel]          >>> /admin/user/list.do  (23. ユーザー一覧)
権限不足            >>> /unauthorized.do   (04. 権限エラー)
未ログイン           >>> /login.do         (01. ログイン)
```
