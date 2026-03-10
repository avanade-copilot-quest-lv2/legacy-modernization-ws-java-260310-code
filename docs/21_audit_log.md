# 21. 監査ログ画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/audit/log.jsp`  
**URL**: `/audit/log.do?method=auditLog`  
**必要権限**: MANAGER / ADMIN  
**Actionクラス**: `com.example.bookstore.action.AuditLogAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} ({role})[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Audit Log                                                                 ║
║  [エラーメッセージ (条件付き)]                                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  From: [           ]  To: [           ]                            │   ║
║  │  Action Type: [▼ All Action Types            ]                     │   ║
║  │                  All Action Types                                  │   ║
║  │                  LOGIN                                             │   ║
║  │                  LOGOUT                                            │   ║
║  │                  CREATE                                            │   ║
║  │                  UPDATE                                            │   ║
║  │                  DELETE                                            │   ║
║  │                  VIEW                                              │   ║
║  │                                          [ Search ]  [ Clear ]    │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ── 結果: {n} records found (Page 1) ───────────────────────────────────   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  # │  ID  │ Date/Time           │ Action  │ User    │ Detail       │   ║
║  │ ───┼──────┼─────────────────────┼─────────┼─────────┼──────────── │   ║
║  │  1 │  101 │ 2026-01-15 09:32:11 │ LOGIN   │ admin   │ Successful...│   ║
║  │  2 │  102 │ 2026-01-15 09:35:22 │ CREATE  │ admin   │ Created book │   ║
║  │  3 │  103 │ 2026-01-15 10:01:05 │ UPDATE  │ mgr1    │ Updated stk  │   ║
║  │  4 │  104 │ 2026-01-15 10:45:30 │ DELETE  │ admin   │ Deleted cat  │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  [Prev] Page 1 of 5 [Next]                                                 ║
║                                                                            ║
║  ⚠️ [DEBUG SQL: SELECT * FROM audit_log WHERE ...]  ← 本番環境で問題         ║
║                                                                            ║
║  « Home                                                                    ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## フィルターフォーム - 入力項目

| # | 項目名 | name | 型 | 説明 |
|---|--------|------|----|------|
| 1 | From | `fromDate` | テキスト | 開始日時 |
| 2 | To | `toDate` | テキスト | 終了日時 |
| 3 | Action Type | `actionType` | セレクト | `(空)=All` / `LOGIN` / `LOGOUT` / `CREATE` / `UPDATE` / `DELETE` / `VIEW` |

---

## 結果テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| # | 行番号 | |
| ID | `log.id` | |
| Date/Time | `log.logDate` | `yyyy-MM-dd HH:mm:ss` |
| Action | `log.actionType` | |
| User | `log.username` | |
| Detail | `log.detail` | 省略表示 |

データソース: `session["auditLogs"]`  
ページサイズ: **固定 25件** (`pageSize=25`)

---

## ページネーション

```
[« Prev]  Page {current} of {total}  [Next »]
```

---

## セキュリティ上の問題点 ⚠️

| 問題 | 詳細 |
|------|------|
| JSP内でJDBC直接SQL実行 | DAO/Serviceレイヤを経由せずJSP内で直接DBアクセス |
| SQLインジェクション脆弱性 | フィルター条件が文字列連結でSQL組み立て |
| デバッグSQL表示 | 実行したSQLクエリが画面上に表示される本番不適切コード |

---

## ボタン・リンク

| 名前 | アクション |
|------|----------|
| Search | フォーム送信 (フィルター適用) |
| Clear | フィルタークリア |
| Prev / Next | ページネーション |
| « Home | `/home.do` |

---

## 画面遷移

```
[Search]  >>> 同画面 (フィルタ適用後)
[Clear]   >>> 同画面 (フィルタリセット)
« Home    >>> /home.do              (05. ダッシュボード)
権限不足   >>> /unauthorized.do     (04. 権限エラー)
未ログイン  >>> /login.do           (01. ログイン)
```
