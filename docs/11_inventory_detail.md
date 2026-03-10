# 11. 在庫詳細画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/inventory/detail.jsp`  
**URL**: `/inventory/detail.do?method=detail&bookId={id}`  
**必要権限**: ログイン済み  
**Actionクラス**: `com.example.bookstore.action.InventoryAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]     ║
║                                         User: {username} ({role})[Logout]║
╠════════════════════════════════════════════════════════════════════════╣
║                                                                        ║
║  Stock Detail                                                          ║
║  [エラーメッセージ (条件付き)]                                           ║
║                                                                        ║
║  ┌──────────────────────────────────────────────────────────────┐     ║
║  │  書籍情報                                                     │     ║
║  │ ──────────────────────────────────────────────────────────   │     ║
║  │  ISBN      :  9784000000001                                  │     ║
║  │  Title     :  Java プログラミング入門                         │     ║
║  │  Publisher :  技術出版社                                      │     ║
║  │  Price     :  $1,200                                         │     ║
║  │  Stock     :  45      ← (緑/橙/赤 で色分け、大きく表示)       │     ║
║  │  Status    :  ACTIVE                                         │     ║
║  │                                                              │     ║
║  │  ※ MANAGER/ADMIN のみ:                                       │     ║
║  │  [ Adjust Stock ]                                            │     ║
║  └──────────────────────────────────────────────────────────────┘     ║
║                                                                        ║
║  Recent Stock Transactions                                             ║
║                                                                        ║
║  ┌──────────────────────────────────────────────────────────────┐     ║
║  │ Date       │ Type     │ Change │ After  │ User   │ Reason    │     ║
║  ├────────────┼──────────┼────────┼────────┼────────┼──────────┤     ║
║  │ 03/01 10:20│ RECEIVE  │ +50    │  95    │ admin  │ RECEIVING │     ║
║  │ 03/02 14:30│ SALE     │  -5    │  90    │ clerk1 │ SALE      │     ║
║  │ 03/03 09:15│ ADJUST   │  -3    │  87    │ mgr1   │ DAMAGE    │     ║
║  │ ...        │          │        │        │        │           │     ║
║  └──────────────────────────────────────────────────────────────┘     ║
║                                                                        ║
║  [在庫一覧に戻る]                                                       ║
║                                                                        ║
╠════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                    ║
╚════════════════════════════════════════════════════════════════════════╝
```

---

## 書籍情報 - 表示項目

| # | 項目名 | ソース |
|---|--------|--------|
| 1 | ISBN | `book.isbn` (bean:write) |
| 2 | Title | `book.title` |
| 3 | Publisher | `book.publisher` (bean:write) |
| 4 | Price | `book.listPrice` |
| 5 | Stock | `book.qtyInStock` ※在庫カラーで色分け・大きく表示 |
| 6 | Status | `book.status` |

データソース: `session["book"]`

---

## 在庫取引履歴テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| Date | `txn.crtDt` | MM/dd HH:mm 形式 |
| Type | `txn.txnType` | |
| Change | `txn.qtyChange` | +は緑・-は赤 |
| After | `txn.qtyAfter` | 取引後残数 |
| User | `txn.userId` | |
| Reason | `txn.reason` | |

データソース: `session["transactions"]`

---

## ボタン・リンク

| 名前 | 遷移先 | 表示条件 |
|------|--------|---------|
| Adjust Stock | `/inventory/adjust.do?method=adjustStock&bookId={id}` | MANAGER/ADMIN のみ |
| 在庫一覧に戻る | `/inventory/list.do?method=list` | 全ロール |

---

## 画面遷移

```
[Adjust Stock]   >>> /inventory/adjust.do  (12. 在庫調整)  ※MANAGER/ADMIN
[在庫一覧に戻る] >>> /inventory/list.do    (10. 在庫一覧)
未ログイン        >>> /login.do             (01. ログイン)
```
