# 16. 仕入先一覧画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/supplier/list.jsp`  
**URL**: `/supplier/list.do?method=supplierList`  
**必要権限**: ログイン済み  
**Actionクラス**: `com.example.bookstore.action.InventoryAction` (推定)

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} ({role})[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Suppliers                                                                 ║
║  [エラーメッセージ / 成功メッセージ (条件付き)]                               ║
║                                                                            ║
║  ┌──────────────────────────────────────────────────────────────────────┐ ║
║  │ #  │ Name         │ Contact  │ Email           │Phone│ City │Stat│LT │ ║
║  ├────┼──────────────┼──────────┼─────────────────┼─────┼──────┼────┼───┤ ║
║  │  1 │ BookWorld    │ Alice M. │ a@bookworld.com │ 555 │ N.Y  │[A] │ 7 │ ║
║  │  2 │ PagesTech    │ Bob K.   │ b@pagestech.com │ 556 │ L.A. │[A] │14 │ ║
║  │  3 │ OldPress     │ Carol T. │ c@oldpress.com  │ 557 │ CHI  │[I] │30 │ ║
║  └──────────────────────────────────────────────────────────────────────┘ ║
║  Total: {n} suppliers                                                      ║
║                                                                            ║
║  « Home   | Purchase Orders                                                ║
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
| Name | `s.name` | 太字 |
| Contact | `s.contactName` | |
| Email | `s.email` | `mailto:` リンク |
| Phone | `s.phone` | |
| City | `s.city` | |
| Stat | `s.active` | ステータスバッジ |
| LT | `s.leadTimeDays` | リードタイム (日数) |

データソース: `session["supplierList"]`

---

## ステータスバッジ カラーコード

| ステータス | 表示 | バッジ色 |
|----------|------|---------|
| active=true | [A] Active | 緑背景・緑文字 (`badge-active`) |
| active=false | [I] Inactive | 赤背景・赤文字 (`badge-inactive`) |

---

## ボタン・リンク

| 名前 | 遷移先 |
|------|--------|
| « Home | `/home.do` |
| Purchase Orders | `/purchaseorder/list.do?method=poList` |

---

## 画面遷移

```
« Home           >>> /home.do               (05. ダッシュボード)
Purchase Orders  >>> /purchaseorder/list.do (15. 発注書一覧)
未ログイン        >>> /login.do             (01. ログイン)
```
