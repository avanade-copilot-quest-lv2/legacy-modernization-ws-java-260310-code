# Bookstore Sales Management System — 画面設計書 インデックス

> **システム概要**: 書店向け販売管理システム (Struts 1.3 / JSP / Hibernate)  
> **コンテキストパス**: `/legacy-app`  
> **文書作成日**: 2026-03-10

---

## 全体画面遷移図

```
                          ┌─────────────────────────────────────────────────────────────────────┐
                          │                      未ログイン状態                                   │
                          └────────────────────────────┬────────────────────────────────────────┘
                                                       │ アクセス
                                                       ▼
                                              ┌─────────────────┐
                                              │  01. ログイン    │
                                              │   /login.do      │
                                              └────────┬────────┘
                                                       │ 認証成功
                               ┌───────────────────────┼─────────────────────┐
                               │ ADMIN                  │ MANAGER/CLERK       │ USER
                               ▼                        ▼                     ▼
                    ┌──────────────────┐   ┌─────────────────────┐   ┌──────────────┐
                    │ 02. ウェルカム    │   │  02. ウェルカム      │   │ 02. ウェルカム│
                    │  /welcome.jsp    │   │   /welcome.jsp       │   │  /welcome.jsp│
                    └────────┬─────────┘   └──────────┬──────────┘   └──────┬───────┘
                             │ 自動遷移                │ 自動遷移              │
                             ▼                        ▼                     ▼
                  ┌──────────────────┐   ┌──────────────────────┐  ┌──────────────────┐
                  │ 22. 管理者         │   │   05. ダッシュボード   │  │ 05. ダッシュボード │
                  │    ダッシュボード   │   │    /home.do           │  │  /home.do         │
                  └──────────────────┘   └──────────────────────┘  └──────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                  05. ダッシュボード(/home.do)                                 │
└──────┬─────────────┬───────────────────┬──────────────────┬─────────────┬───────────────────┘
       │ 書籍         │ 販売              │ 在庫             │ 仕入先       │ レポート/管理
       ▼             ▼                   ▼                  ▼             ▼
┌──────────┐  ┌─────────────┐  ┌───────────────┐  ┌──────────────┐  ┌──────────────────┐
│ 06.書籍  │  │ 07.販売      │  │ 10.在庫一覧   │  │ 16.仕入先    │  │ 17.レポートメニュー│
│   検索   │  │   入力       │  │   /list.do    │  │   一覧       │  │  ※MANAGER以上    │
└──────────┘  └──────┬──────┘  └───────┬───────┘  └──────────────┘  └──────────────────┘
                     │                 │
          ┌──────────┼───────┐  ┌──────┼──────────┬──────────────┐
          │ カート追加│       │  │      │          │              │
          ▼          ▼       ▼  ▼      ▼          ▼              ▼
     ┌─────────┐ ┌────────┐  ┌──────┐ ┌────────┐ ┌────────┐  ┌────────┐
     │カート更新│ │08.チェック│  │11.詳細│ │12.調整 │ │13.台帳 │  │14.低在庫│
     │/cart/..│ │アウト    │  │/detail│ │/adjust │ │/ledger │  │/lowstock│
     └─────────┘ └───┬────┘  └──────┘ └────────┘ └────────┘  └────────┘
                     │
                     ▼
               ┌───────────┐
               │09.注文確認 │
               │/confirm.do│
               └───────────┘

仕入先系:
  16.仕入先一覧 ───────────────────────────────────── 15.発注書一覧
  /supplier/list.do                                  /purchaseorder/list.do

レポート系 (MANAGER/ADMIN のみ):
  17.レポートメニュー ─── 18.日別売上 ─── /reports/daily.do
                      ├── 19.書籍別売上 ── /reports/bybook.do
                      └── 20.トップ書籍 ── /reports/topbooks.do

監査ログ  (MANAGER/ADMIN のみ):
  21.監査ログ ── /audit/log.do

管理者系 (ADMIN のみ):
  22.管理者ダッシュボード ─── 23.ユーザー一覧 ─── 24.ユーザーフォーム(追加/編集)
                           └── 25.カテゴリ一覧 ─── 26.カテゴリフォーム(追加/編集)

共通エラー/権限系:
  03.エラー画面  ── /WEB-INF/jsp/bookstore/error.jsp
  04.権限エラー  ── /WEB-INF/jsp/bookstore/unauthorized.jsp
```

---

## 画面一覧

| No. | 画面名 | JSPパス | URL | 必要権限 | ドキュメント |
|-----|--------|---------|-----|----------|------------|
| 01 | ログイン | `bookstore/login.jsp` | `/login.do` | 不要 | [01_login.md](01_login.md) |
| 02 | ウェルカム | `bookstore/welcome.jsp` | `/` (ログイン後) | LOGIN | [02_welcome.md](02_welcome.md) |
| 03 | エラー | `bookstore/error.jsp` | (内部転送) | — | [03_error.md](03_error.md) |
| 04 | 権限エラー | `bookstore/unauthorized.jsp` | (内部転送) | — | [04_unauthorized.md](04_unauthorized.md) |
| 05 | ダッシュボード | `bookstore/home.jsp` | `/home.do` | LOGIN | [05_dashboard.md](05_dashboard.md) |
| 06 | 書籍検索 | `bookstore/book/search.jsp` | `/book/search.do` | LOGIN | [06_book_search.md](06_book_search.md) |
| 07 | 販売入力 | `bookstore/sales/entry.jsp` | `/sales/entry.do` | LOGIN | [07_sales_entry.md](07_sales_entry.md) |
| 08 | チェックアウト | `bookstore/sales/checkout.jsp` | `/sales/checkout.do` | LOGIN | [08_sales_checkout.md](08_sales_checkout.md) |
| 09 | 注文確認 | `bookstore/sales/confirmation.jsp` | `/sales/confirm.do` | LOGIN | [09_sales_confirmation.md](09_sales_confirmation.md) |
| 10 | 在庫一覧 | `bookstore/inventory/list.jsp` | `/inventory/list.do` | LOGIN | [10_inventory_list.md](10_inventory_list.md) |
| 11 | 在庫詳細 | `bookstore/inventory/detail.jsp` | `/inventory/detail.do` | LOGIN | [11_inventory_detail.md](11_inventory_detail.md) |
| 12 | 在庫調整 | `bookstore/inventory/adjust.jsp` | `/inventory/adjust.do` | MANAGER/ADMIN | [12_inventory_adjust.md](12_inventory_adjust.md) |
| 13 | 在庫台帳 | `bookstore/inventory/ledger.jsp` | `/inventory/ledger.do` | LOGIN | [13_inventory_ledger.md](13_inventory_ledger.md) |
| 14 | 低在庫アラート | `bookstore/inventory/lowstock.jsp` | `/inventory/lowstock.do` | LOGIN | [14_inventory_lowstock.md](14_inventory_lowstock.md) |
| 15 | 発注書一覧 | `bookstore/purchaseorder/list.jsp` | `/purchaseorder/list.do` | LOGIN | [15_purchaseorder_list.md](15_purchaseorder_list.md) |
| 16 | 仕入先一覧 | `bookstore/supplier/list.jsp` | `/supplier/list.do` | LOGIN | [16_supplier_list.md](16_supplier_list.md) |
| 17 | レポートメニュー | `bookstore/reports/menu.jsp` | `/reports/menu.do` | MANAGER/ADMIN | [17_reports_menu.md](17_reports_menu.md) |
| 18 | 日別売上レポート | `bookstore/reports/daily-sales.jsp` | `/reports/daily.do` | MANAGER/ADMIN | [18_reports_daily_sales.md](18_reports_daily_sales.md) |
| 19 | 書籍別売上レポート | `bookstore/reports/sales-by-book.jsp` | `/reports/bybook.do` | MANAGER/ADMIN | [19_reports_sales_by_book.md](19_reports_sales_by_book.md) |
| 20 | トップ書籍レポート | `bookstore/reports/top-books.jsp` | `/reports/topbooks.do` | MANAGER/ADMIN | [20_reports_top_books.md](20_reports_top_books.md) |
| 21 | 監査ログ | `bookstore/audit/log.jsp` | `/audit/log.do` | MANAGER/ADMIN | [21_audit_log.md](21_audit_log.md) |
| 22 | 管理者ダッシュボード | `bookstore/admin/home.jsp` | `/admin/home.do` | ADMIN | [22_admin_dashboard.md](22_admin_dashboard.md) |
| 23 | ユーザー管理一覧 | `bookstore/admin/user/list.jsp` | `/admin/user/list.do` | ADMIN | [23_admin_user_list.md](23_admin_user_list.md) |
| 24 | ユーザーフォーム | `bookstore/admin/user/form.jsp` | `/admin/user/form.do` | ADMIN | [24_admin_user_form.md](24_admin_user_form.md) |
| 25 | カテゴリ管理一覧 | `bookstore/admin/category/list.jsp` | `/admin/category/list.do` | ADMIN | [25_admin_category_list.md](25_admin_category_list.md) |
| 26 | カテゴリフォーム | `bookstore/admin/category/form.jsp` | `/admin/category/form.do` | ADMIN | [26_admin_category_form.md](26_admin_category_form.md) |

---

## ロール権限マトリクス

| 機能 | GUEST (未ログイン) | USER/CLERK | MANAGER | ADMIN |
|------|:---:|:---:|:---:|:---:|
| ログイン画面 | ○ | ○ | ○ | ○ |
| ダッシュボード | — | ○ | ○ | ○ |
| 書籍検索 | — | ○ | ○ | ○ |
| 販売入力/チェックアウト | — | ○ | ○ | ○ |
| 在庫一覧/詳細/台帳 | — | ○ | ○ | ○ |
| 在庫調整 | — | — | ○ | ○ |
| 発注書一覧/仕入先一覧 | — | ○ | ○ | ○ |
| レポート (全種別) | — | — | ○ | ○ |
| 監査ログ | — | — | ○ | ○ |
| ユーザー管理 | — | — | — | ○ |
| カテゴリ管理 | — | — | — | ○ |
| 管理者ダッシュボード | — | — | — | ○ |

---

## 共通レイアウト構成

```
┌──────────────────────────────────────────────────────────────┐
│  HEADER (includes/header.jsp)                                │
│  Bookstore System  [Home] [Sales] [Inventory] [Reports]      │
│                             User: {username} ({role}) [Logout]│
├──────────────────────────────────────────────────────────────┤
│  CONTENT (各画面固有)                                         │
│  [エラーメッセージ / 成功メッセージ]                           │
│  ...                                                         │
├──────────────────────────────────────────────────────────────┤
│  FOOTER (includes/footer.jsp)                                │
│  © Bookstore System                                          │
└──────────────────────────────────────────────────────────────┘
```

---

## 凡例

- `*` — 必須入力項目  
- `[btn]` — ボタン  
- `(select)` — ドロップダウン  
- `[link]` — リンク  
- `>>>` — 画面遷移先  
