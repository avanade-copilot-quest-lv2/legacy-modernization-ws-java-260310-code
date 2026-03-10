# 12. 在庫調整画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/inventory/adjust.jsp`  
**URL**: `/inventory/adjust.do?method=adjustStock&bookId={id}`  
**必要権限**: MANAGER / ADMIN  
**Actionクラス**: `com.example.bookstore.action.InventoryAction`  
**フォームBean**: `stockAdjustmentForm` (StockAdjustmentForm)

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]     ║
║                                         User: {username} ({role})[Logout]║
╠════════════════════════════════════════════════════════════════════════╣
║                                                                        ║
║  Stock Adjustment                                                      ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                           ║
║                                                                        ║
║  Book: {title} (ISBN: {isbn}) — Current Stock: {currentStock}          ║
║                                                                        ║
║  ┌──────────────────────────────────────────────────────────┐         ║
║  │  在庫調整フォーム                                          │         ║
║  │ ──────────────────────────────────────────────────────   │         ║
║  │  Book ID * :  [______]                                   │         ║
║  │                                                          │         ║
║  │  Type    * :  [▼ -- Select --              ]             │         ║
║  │                  Increase                                │         ║
║  │                  Decrease                                │         ║
║  │                                                          │         ║
║  │  Quantity * :  [_____]  (max: {maxAdj})                  │         ║
║  │                                                          │         ║
║  │  Reason   * :  [▼ -- Select --              ]            │         ║
║  │                  Correction                              │         ║
║  │                  Damage                                  │         ║
║  │                  Theft                                   │         ║
║  │                  Loss                                    │         ║
║  │                  Found                                   │         ║
║  │                  Sample                                  │         ║
║  │                  Other                                   │         ║
║  │                                                          │         ║
║  │  Notes    :   ┌────────────────────────────────────┐    │         ║
║  │               │                                    │    │         ║
║  │               │                                    │    │         ║
║  │               └────────────────────────────────────┘    │         ║
║  │               ※ Other 選択時は必須                        │         ║
║  │                                                          │         ║
║  │                    [ キャンセル ]  [ 調整実行 ]            │         ║
║  └──────────────────────────────────────────────────────────┘         ║
║                                                                        ║
╠════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                    ║
╚════════════════════════════════════════════════════════════════════════╝
```

---

## 入力項目

| # | 項目名 | name/id | 型 | 必須 | バリデーション |
|---|--------|---------|-----|------|-------------|
| 1 | Book ID | `bookId` | hidden/テキスト | ○ | 空チェック |
| 2 | 調整タイプ | `adjType` | セレクト | ○ | 選択チェック |
| 3 | 数量 | `qty` | テキスト | ○ | 1〜9999 (最大値は `maxAdj`) |
| 4 | 理由 | `reason` | セレクト | ○ | 選択チェック |
| 5 | メモ | `notes` | テキストエリア | △ | OTHER 選択時は必須 |

---

## 調整タイプ選択肢

| value | ラベル |
|-------|--------|
| INCREASE | Increase (増加) |
| DECREASE | Decrease (減少) |

---

## 理由選択肢

| value | ラベル |
|-------|--------|
| CORRECTION | Correction (訂正) |
| DAMAGE | Damage (破損) |
| THEFT | Theft (盗難) |
| LOSS | Loss (紛失) |
| FOUND | Found (発見) |
| SAMPLE | Sample (見本) |
| OTHER | Other (その他) |

---

## バリデーション (クライアント側)

- Book ID: 空チェック
- 調整タイプ: 選択チェック
- 数量: 1〜9999
- 理由: 選択チェック
- メモ: OTHER 選択時は必須
- 実行前に確認ダイアログ表示

---

## ボタン

| 名前 | アクション |
|------|---------|
| キャンセル | 前のページに戻る（ブラウザバック） |
| 調整実行 | POST `/inventory/adjust.do?method=adjustStock` |

---

## 画面遷移

```
[調整実行] 成功 >>> /inventory/detail.do  (11. 在庫詳細) + 成功メッセージ
[調整実行] エラー >>> 同画面 + エラーメッセージ
権限不足        >>> /home.do              (05. ダッシュボード) ※自動リダイレクト
未ログイン       >>> /login.do            (01. ログイン)
```
