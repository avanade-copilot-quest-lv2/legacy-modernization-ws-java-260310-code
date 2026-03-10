# 18. 日別売上レポート画面

**対応JSP**: `src/main/webapp/WEB-INF/jsp/bookstore/reports/daily-sales.jsp`  
**URL**: `/reports/dailySales.do?method=dailySales`  
**必要権限**: MANAGER / ADMIN  
**Actionクラス**: `com.example.bookstore.action.ReportAction`

---

## 画面レイアウト

```
╔════════════════════════════════════════════════════════════════════════════╗
║  Bookstore System  [Home][Books][Sales][Inventory][Reports][Admin]         ║
║                                           User: {username} ({role})[Logout] ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Daily Sales Report                                                        ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  From: [2026-01-01    ]   To: [2026-01-31    ]   [ View Report ]  │   ║
║  │         (yyyy-MM-dd)             (yyyy-MM-dd)                     │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  ── 結果表示エリア (送信後) ─────────────────────────────────────────────   ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐   ║
║  │  Report Period: 2026-01-01 to 2026-01-31              [Export CSV] │   ║
║  │                                                                    │   ║
║  │  # │ Date       │ Orders │ Items Sold │ Gross Sales │  Tax │  Net  │   ║
║  │ ───┼────────────┼────────┼────────────┼─────────────┼──────┼────── │   ║
║  │  1 │ 2026-01-01 │      3 │         12 │   $1,200.00 │ $... │ $...  │   ║
║  │  2 │ 2026-01-02 │      5 │         18 │   $1,800.00 │ $... │ $...  │   ║
║  │  3 │ 2026-01-03 │      2 │          7 │     $700.00 │ $... │ $...  │   ║
║  │ ───┼────────────┼────────┼────────────┼─────────────┼──────┼────── │   ║
║  │    │ TOTAL      │     10 │         37 │   $3,700.00 │ $... │ $...  │   ║
║  └────────────────────────────────────────────────────────────────────┘   ║
║                                                                            ║
║  « Back to Reports                                                         ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  © Bookstore System                                                        ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## フィルターフォーム - 入力項目

| # | 項目名 | name | 型 | バリデーション |
|---|--------|------|----|--------------|
| 1 | From (開始日) | `startDate` | テキスト | 書式: `yyyy-MM-dd`、必須 |
| 2 | To (終了日) | `endDate` | テキスト | 書式: `yyyy-MM-dd`、必須 |

---

## 結果テーブル列

| 列名 | データ | 備考 |
|------|--------|------|
| # | 行番号 | |
| Date | `row.saleDate` | `yyyy-MM-dd` |
| Orders | `row.orderCount` | 右揃え |
| Items Sold | `row.itemCount` | 右揃え |
| Gross Sales | `row.grossSales` | `$` 付き |
| Tax | `row.tax` | `$` 付き |
| Net Sales | `row.netSales` | `$` 付き、等幅フォント |

最下行: **TOTAL** (合計行、太字・背景色変更)

データソース: `session["dailySalesData"]` / `request["reportData"]`

---

## ボタン・リンク

| 名前 | アクション |
|------|----------|
| View Report | フォーム送信 (POST) |
| Export CSV | `/reports/dailySalesCsv.do` (CSVダウンロード) |
| « Back to Reports | `/reports/menu.do?method=reportsMenu` |

---

## 画面遷移

```
[View Report]       >>> 同画面 (フィルタ適用後)
[Export CSV]        >>> CSVファイルダウンロード
« Back to Reports   >>> /reports/menu.do  (17. レポートメニュー)
権限不足             >>> /unauthorized.do  (04. 権限エラー)
未ログイン            >>> /login.do        (01. ログイン)
```
