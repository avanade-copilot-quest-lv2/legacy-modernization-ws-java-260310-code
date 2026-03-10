# 15. 発注書一覧画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/purchaseorder/list.jsp`  
**URL**: `/purchaseorder/list.do?method=poList`  
**必要権限**: ログイン済み  
**Actionクラス**: `com.example.bookstore.action.PurchaseOrderAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} ({role})[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Purchase Orders                                                           ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                               ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  Status: [▼ All              ]  [ Filter ]   [ Home ]              │   ║
║  │          All                                                       │   ║
║  │          Draft                                                     │   ║
║  │          Submitted                                                 │   ║
║  │          Partially Received                                        │   ║
║  │          Received                                                  │   ║
║  │          Closed                                                    │   ║
║  │          Cancelled                                                 │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │ #  │ PO Number   │ Order Date  │ Status           │  Total │ By    │   ║
║  ├────┼─────────────┼─────────────┼──────────────────┼────────┼───────┤   ║
║  │  1 │ PO-2026-001 │ 2026-01-10  │ [DRAFT]          │ $5,400 │ admin │   ║
║  │  2 │ PO-2026-002 │ 2026-01-15  │ [SUBMITTED]      │$12,000 │ mgr1  │   ║
║  │  3 │ PO-2026-003 │ 2026-02-01  │ [RECEIVED]       │ $8,250 │ admin │   ║
║  │  4 │ PO-2026-004 │ 2026-02-20  │ [CANCELLED]      │ $3,600 │ clerk1│   ║
║  │  5 │ PO-2026-005 │ 2026-03-01  │ [PARTIALLY_RECV] │ $9,000 │ mgr1  │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║  Total: {n} purchase orders                                                ║
║                                                                            ║
║  « Home   | Suppliers                                                      ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## フィルターフォーム - 入力項目

| # | 項目名 | name | 型 | 選択肢 |
|---|--------|------|-----|--------|
| 1 | ステータス | `status` | セレクト | (空)=All / DRAFT / SUBMITTED / PARTIALLY_RECEIVED / RECEIVED / CLOSED / CANCELLED |

---

## 一覧テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| # | 行番号 | 1始まり |
| PO Number | `po.poNumber` | 太字 |
| Order Date | `po.orderDt` | |
| Status | `po.status` | ステータスバッジで色分け |
| Total | `po.total` | `$` 付き右揃え・等幅フォント |
| Created By | `po.createdBy` | |

データソース: `session["purchaseOrders"]`

---

## ステータスバッジ カラーコード

| ステータス | バッジ色 |
|----------|---------|
| DRAFT | グレー背景・グレー文字 |
| SUBMITTED | 青背景・青文字 |
| RECEIVED / PARTIALLY_RECEIVED | 緑背景・緑文字 |
| CANCELLED | 赤背景・赤文字 |

---

## ボタン・リンク

| 名前 | 遷移先 |
|------|--------|
| Filter | 同画面 (ステータスフィルタ) |
| Home | `/home.do` |
| « Home (下部) | `/home.do` |
| Suppliers | `/supplier/list.do?method=supplierList` |

---

## 画面遷移

```
[Filter]    >>> 同画面 (ステータスでフィルタリング)
[Home]      >>> /home.do              (05. ダッシュボード)
[Suppliers] >>> /supplier/list.do     (16. 仕入先一覧)
未ログイン   >>> /login.do            (01. ログイン)
```
