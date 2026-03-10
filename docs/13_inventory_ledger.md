# 13. 在庫台帳画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/inventory/ledger.jsp`  
**URL**: `/inventory/ledger.do?method=ledger&bookId={id}`  
**必要権限**: ログイン済み  
**Actionクラス**: `com.example.bookstore.action.InventoryAction`

---

## 画面レイアウト

```
╔═══════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]            ║
║                                            User: {username} ({role})[Logout]  ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                               ║
║  Stock Ledger                                                                 ║
║  [エラーメッセージ (条件付き)]                                                  ║
║                                                                               ║
║  Book: {title} | ISBN: {isbn} | Current Stock: {qtyInStock}                   ║
║                                                                               ║
║  ┌──────────────────────────────────────────────────────────────────────┐    ║
║  │  Ledger Summary  (JSP内計算)                                          │    ║
║  │ ────────────────────────────────────────────────────────────────     │    ║
║  │  Total Transactions     :   {txnCount}                               │    ║
║  │  Units Added (+)        :  +{totalAdded}    (緑)                     │    ║
║  │  Units Removed (-)      :  -{totalRemoved}  (赤)                     │    ║
║  │  Net Change             :  {netChange}                               │    ║
║  │  Avg Change/Transaction :  {avg}                                     │    ║
║  │  Date Range             :  {earliestDate} — {latestDate}             │    ║
║  │  Types                  :  RECEIVE: 5, SALE: 12, ADJUST: 3, ...     │    ║
║  └──────────────────────────────────────────────────────────────────────┘    ║
║                                                                               ║
║  ┌──────────────────────────────────────────────────────────────────────┐    ║
║  │ # │ Date/Time           │ Type    │ Chg  │ After│ User  │Reason│Notes│Ref│Run║
║  ├───┼─────────────────────┼─────────┼──────┼──────┼───────┼──────┼─────┼───┼───║
║  │ 1 │ 2026-01-15 09:00:00 │ RECEIVE │  +50 │   95 │ admin │RECV  │     │PO1│ +50║
║  │ 2 │ 2026-01-16 14:30:00 │ SALE    │   -2 │   93 │clerk1 │SALE  │     │OR5│ +48║
║  │ 3 │ 2026-01-20 10:15:00 │ ADJUST  │   -3 │   90 │ mgr1  │DAMAGE│傷有 │   │ +45║
║  │...│                     │         │      │      │       │      │     │   │    ║
║  └──────────────────────────────────────────────────────────────────────┘    ║
║                                                                               ║
║  [在庫詳細に戻る]  [在庫一覧に戻る]                                             ║
║                                                                               ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                           ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

---

## サマリーセクション - 表示項目

| 項目名 | 計算方法 | 備考 |
|--------|---------|------|
| Total Transactions | `transactions.size()` | JSP内で計算 |
| Units Added (+) | qtyChange ≥ 0 の合計 | 緑色 |
| Units Removed (-) | qtyChange < 0 の合計の絶対値 | 赤色 |
| Net Change | Units Added - Units Removed | |
| Avg Change/Transaction | netChange ÷ txnCount | |
| Date Range | earliest 〜 latest `crtDt` | |
| Types | 取引タイプ別カウント | HashMap集計 |

---

## 台帳テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| # | 行番号 | offset + i + 1 |
| Date/Time | `txn.crtDt` | yyyy-MM-dd HH:mm:ss 形式 |
| Type | `txn.txnType` | |
| Change | `txn.qtyChange` | +は緑・-は赤 |
| After | `txn.qtyAfter` | 取引後在庫数 |
| User | `txn.userId` | |
| Reason | `txn.reason` | |
| Notes | `txn.notes` | |
| Ref | 参照番号 | |
| Running +/- | 累計増減 | JSP内計算 |

データソース: `session["transactions"]`

---

## ボタン・リンク

| 名前 | 遷移先 |
|------|--------|
| 在庫詳細に戻る | `/inventory/detail.do?method=detail&bookId={id}` |
| 在庫一覧に戻る | `/inventory/list.do?method=list` |

---

## 画面遷移

```
[在庫詳細に戻る] >>> /inventory/detail.do   (11. 在庫詳細)
[在庫一覧に戻る] >>> /inventory/list.do     (10. 在庫一覧)
未ログイン        >>> /login.do             (01. ログイン)
```
