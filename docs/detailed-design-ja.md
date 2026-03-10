# 書店販売管理システム 詳細設計書

## 1. 文書概要
- 文書名: 書店販売管理システム 詳細設計書
- 対象システム: Legacy Bookstore Application
- 作成日: 2026-03-10
- 対象コードベース: `/app` ワークスペース
- ベース実装: Struts 1.x + Hibernate 3.6 + MySQL 5.7

本書は、現行ソースコード実装を基準にした「As-Is」詳細設計である。設計意図と実装が乖離している箇所も、現行挙動を優先して記載する。

## 2. システム構成

### 2.1 技術スタック
- 言語: Java 1.5 世代
- Webフレームワーク: Apache Struts 1.3
- ORM: Hibernate 3.6 (一部処理は JDBC 直書き)
- DB: MySQL 5.7
- ビルド: Apache Ant (`build.xml`)
- サーブレットコンテナ: Tomcat 6-8 (README記載)

### 2.2 レイヤ構成
1. Presentation層
- JSP (`src/main/webapp/WEB-INF/jsp/bookstore/...`)
- ActionForm (`src/main/java/com/example/bookstore/form/...`)
- Struts Action (`src/main/java/com/example/bookstore/action/...`)

2. Business層
- Manager (`src/main/java/com/example/bookstore/manager/...`)
- 主に `BookstoreManager`, `UserManager` が業務処理を集約

3. Data Access層
- DAO Interface/Impl (`src/main/java/com/example/bookstore/dao/...`, `.../dao/impl/...`)
- Hibernate中心だが、Action/Form/Manager内に JDBC 直実装あり

4. Data層
- MySQL スキーマ (`config/mysql/01-create-tables.sql`)
- 初期データ (`config/mysql/02-seed-data.sql`)

### 2.3 リクエスト処理基盤
- URLパターン: `*.do`, `*.action`, `/api/v1/*` (`web.xml`)
- Front Controller: `org.apache.struts.action.ActionServlet`
- 文字コードフィルタ: `CharacterEncodingFilter`
- エラー画面: `404/500/403` の error-page 定義あり

## 3. 定数・共通仕様

基準定義: `src/main/java/com/example/bookstore/constant/AppConstants.java`

### 3.1 ステータスコード
- `0`: 正常 (`STATUS_OK`)
- `1`: 警告 (`STATUS_WARN`)
- `2`: 未検出 (`STATUS_NOT_FOUND`)
- `3`: 重複 (`STATUS_DUPLICATE`)
- `4`: 権限エラー (`STATUS_UNAUTHORIZED`)
- `9`: エラー (`STATUS_ERR`)

### 3.2 主要セッションキー
- `user`, `role`, `loginTime`
- `cart`, `searchResult`, `searchCriteria`
- 画面別補助情報: `books`, `categories`, `lowStockBooks`, `transactions` など

### 3.3 在庫閾値
- 低在庫: 10 (`LOW_STOCK_THRESHOLD`)
- クリティカル: 3 (`CRITICAL_STOCK_THRESHOLD`)

## 4. 機能詳細設計

## 4.1 認証・認可

### 4.1.1 ログイン
- Action: `LoginAction.login` (`src/main/java/com/example/bookstore/action/LoginAction.java`)
- Manager: `UserManager.authenticate` (`src/main/java/com/example/bookstore/manager/UserManager.java`)
- Form: `LoginForm` (`src/main/java/com/example/bookstore/form/LoginForm.java`)
- Mapping: `/login.do` (`src/main/webapp/WEB-INF/struts-config.xml`)

処理概要:
1. `usrNm/pwd` を Form または request から取得
2. 入力必須チェック
3. 失敗回数に応じた遅延・ロック判定
4. `UserManager.authenticate` で認証
5. 認証成功時に `user/role/loginTime` 等をセッション登録
6. 監査ログ (audit_log) を JDBC で直接INSERT

戻り:
- 成功: `success` → `/home.do`
- 失敗: `failure` → ログイン画面

### 4.1.2 ログアウト
- Action: `LoginAction.logout`
- 処理: セッション破棄 + 操作ログ記録

### 4.1.3 認可
- 基本は Action 内のセッション/ロール判定
- Container security (`web.xml` の security-constraint) はコメントアウトされている

## 4.2 販売管理

対象Action: `SalesAction` (`src/main/java/com/example/bookstore/action/SalesAction.java`)

機能群:
- `entry`: 商品検索、カート表示、カテゴリ読込
- `addToCart`: カート追加
- `updateCart`: カート数量更新
- `removeFromCart`: カート明細削除
- `checkout` 系: 購入確定フロー

関連Manager:
- `BookstoreManager.searchBooks`
- `BookstoreManager.addToCart`
- `BookstoreManager.updateCartQty`
- `BookstoreManager.removeFromCart`
- `BookstoreManager.calculateTotal`

販売共通前処理:
- `SalesAction.preProcess` にて
- セッション確認
- ログイン属性確認
- レート制御の監視ログ
- CSRFプレースホルダ判定

画面遷移(主要):
- `/sales/entry.do` → `sales/entry.jsp`
- `/sales/cart/add.do` → `sales/entry.do` (redirect)
- `/sales/checkout.do` → `sales/checkout.jsp`
- `/sales/checkout/submit.do` → `sales/confirmation.jsp`

## 4.3 在庫管理

対象Action: `InventoryAction` (`src/main/java/com/example/bookstore/action/InventoryAction.java`)

機能:
- `list`: 在庫一覧・低在庫件数表示
- `detail`: 書籍詳細と在庫履歴表示
- `adjustStock`: 在庫増減登録
- `ledger`: 台帳表示
- `lowStock`: 在庫警告一覧

権限制御 (adjustStock):
- `ADMIN`: 制限なし
- `MANAGER`: 上限100、減算時条件あり
- `SUPERVISOR`: 上限25、数量条件で承認必須
- `CLERK/INTERN`: 不可

在庫調整フロー:
1. ロールごとの上限・承認要件判定
2. 入力チェック (`bookId`, `adjType`, `qty`, `reason`)
3. `BookstoreManager.adjustStock` 実行
4. 履歴再取得、警告文再計算
5. 監査ログ INSERT

## 4.4 仕入・発注管理

対象Action:
- `PurchaseOrderAction` (`src/main/java/com/example/bookstore/action/PurchaseOrderAction.java`)

主要URL:
- `/supplier/list.do`
- `/purchaseorder/list.do`

関連DAO(Manager側):
- `SupplierDAO`, `PurchaseOrderDAO`, `PurchaseOrderItemDAO`
- `ReceivingDAO`, `ReceivingItemDAO`

備考:
- 実装上、DAO経由とJDBC直書きが混在する設計である。

## 4.5 レポート

対象Action:
- `ReportAction` (`src/main/java/com/example/bookstore/action/ReportAction.java`)

主要URL:
- `/reports.do` (メニュー)
- `/reports/daily.do`
- `/reports/bybook.do`
- `/reports/topbooks.do`
- `/export/csv.do`

フォーム:
- `ReportFilterForm` (`src/main/java/com/example/bookstore/form/ReportFilterForm.java`)

抽出条件:
- 期間 (`startDt`, `endDt`)
- カテゴリ
- ソート条件
- TopN など

## 4.6 監査ログ

対象Action:
- `AuditLogAction` (`src/main/java/com/example/bookstore/action/AuditLogAction.java`)

機能:
- ログ表示
- 条件検索
- CSV出力

データソース:
- `audit_log` テーブル
- 一部処理は SQL を動的組み立てして検索

## 4.7 管理者機能

対象Action:
- `AdminAction` (`src/main/java/com/example/bookstore/action/AdminAction.java`)

主要機能:
- 管理ホーム
- ユーザ一覧/編集/有効無効
- カテゴリ一覧/編集/削除

主要URL:
- `/admin/home.do`
- `/admin/user/list.do`
- `/admin/user/form.do`
- `/admin/user/save.do`
- `/admin/category/list.do`
- `/admin/category/form.do`

## 5. Struts設定設計

設定ファイル: `src/main/webapp/WEB-INF/struts-config.xml`

### 5.1 FormBean
- `loginForm` → `LoginForm`
- `bookSearchForm` → `BookSearchForm`
- `salesForm` → `SalesForm`
- `stockAdjustmentForm` → `StockAdjustmentForm`
- `reportFilterForm` → `ReportFilterForm`
- `auditLogFilterForm` → `AuditLogFilterForm`
- `adminForm` → `AdminForm`

### 5.2 Action Mapping設計方針
- 多くのActionで `parameter="method"` を採用し、DispatchActionでメソッドディスパッチ
- Forwardは `success/login/unauthorized/error` を基本命名
- 一部で `redirect="true"` を併用

## 6. 画面-URL対応

主要対応 (抜粋):
- `/login.do` → `WEB-INF/jsp/bookstore/login.jsp`
- `/home.do` → `WEB-INF/jsp/bookstore/home.jsp`
- `/book/search.do` → `WEB-INF/jsp/bookstore/book/search.jsp`
- `/sales/entry.do` → `WEB-INF/jsp/bookstore/sales/entry.jsp`
- `/inventory/list.do` → `WEB-INF/jsp/bookstore/inventory/list.jsp`
- `/inventory/detail.do` → `WEB-INF/jsp/bookstore/inventory/detail.jsp`
- `/inventory/adjust.do` → `WEB-INF/jsp/bookstore/inventory/adjust.jsp`
- `/reports/daily.do` → `WEB-INF/jsp/bookstore/reports/daily-sales.jsp`
- `/audit/log.do` → `WEB-INF/jsp/bookstore/audit/log.jsp`
- `/admin/user/list.do` → `WEB-INF/jsp/bookstore/admin/user/list.jsp`

## 7. データ設計

主定義: `config/mysql/01-create-tables.sql`
初期データ: `config/mysql/02-seed-data.sql`

### 7.1 主テーブル
- ユーザ系: `users`
- マスタ: `categories`, `authors`, `books`, `supplier`
- 顧客系: `customer`, `address`
- 販売系: `orders`, `order_items`, `order_history`, `shopping_cart`
- 在庫系: `stock_transaction`
- 仕入系: `purchase_order` (他関連テーブル含む)
- 監査系: `audit_log`

### 7.2 データ型・命名の特徴
- 日付時刻が `VARCHAR` ベースで保持される列が多い
- 命名規則が一部不統一 (`customer` 単数, `users` 複数など)
- スキーマ定義が `config/mysql` と `src/main/resources/sql` で乖離する前提コメントあり

## 8. 非機能設計

### 8.1 セッション
- `web.xml` では `session-timeout=60` 分
- Action側でセッション属性を多数保持する設計

### 8.2 ログ
- `System.out.println`、JUL、Commons Logging が混在
- 監査ログは `audit_log` へアプリケーションから都度INSERT

### 8.3 例外制御
- Action単位で `try-catch` し、画面フォワードで継続する実装が中心
- Struts `global-exceptions` は無効化されている

### 8.4 文字コード
- `CharacterEncodingFilter` 定義あり
- UTF-8 と Shift_JIS 設定の履歴が共存

## 9. ビルド・デプロイ設計

- ビルド: `ant clean build`
- 成果物: WAR (`dist/legacy-app.war`)
- 配備先: Tomcat `webapps`
- DB初期化:
  - `config/mysql/01-create-tables.sql`
  - `config/mysql/02-seed-data.sql`

## 10. 既知課題・技術的負債 (実装ベース)

1. DAO/Hibernate と JDBC直実装の混在
- Action/Form内でDBアクセスする箇所があり、責務が分散

2. セキュリティ設定の一部無効化
- `web.xml` の security-constraint や一部フィルタがコメントアウト

3. バリデーション重複
- Form と Action の両方で重複チェック
- 同一意味の重複フィールドが複数存在

4. スキーマ不整合リスク
- SQLファイル間の差分が明示されている

5. 文字コード/エラーページ設定の履歴混在
- 旧設定が同居し、運用環境依存の挙動が出る可能性

## 11. 付録: 主要コンポーネント

### 11.1 Action
- `LoginAction`
- `HomeAction`
- `BookAction`
- `SalesAction`
- `InventoryAction`
- `PurchaseOrderAction`
- `ReportAction`
- `AuditLogAction`
- `AdminAction`

### 11.2 Manager
- `BookstoreManager`
- `UserManager`
- `SystemManager`
- `CacheManager`

### 11.3 DAO (代表)
- `BookDAO`, `OrderDAO`, `ShoppingCartDAO`, `ReportDAO`
- `SupplierDAO`, `PurchaseOrderDAO`, `ReceivingDAO`
- `AuditLogDAO`, `UserDAO`, `CustomerDAO`

---

以上。
