# Docker / Dev Container セットアップまとめ

## 概要

このプロジェクトは、レガシーな Java 5 / Struts 1.x / Hibernate 3.x アプリを Docker 上で再現して動かす構成です。

利用している主なミドルウェアは以下です。

1. Tomcat 6
2. MySQL 5.7
3. phpMyAdmin
4. Java 5 + Ant 開発コンテナ

アプリは WAR として配備され、公開 URL は `/` ではなく `/legacy-app/` です。

## コンテナ構成

構成は以下の 4 サービスです。

1. `java5-dev`
   Java 5 と Ant を持つ開発用コンテナ
2. `legacy-tomcat`
   アプリ配備先の Tomcat 6
3. `legacy-mysql`
   アプリ用データベース
4. `legacy-phpmyadmin`
   DB 確認用 UI

主な設定ファイルは以下です。

1. `.devcontainer/devcontainer.json`
2. `.devcontainer/compose.dev.yaml`
3. `.devcontainer/compose.services.yaml`
4. `build.xml`
5. `src/main/resources/hibernate.cfg.xml`

## ローカル起動手順

### 1. コンテナ起動

PowerShell で以下を実行します。

```powershell
docker compose -f .devcontainer/compose.dev.yaml -f .devcontainer/compose.services.yaml up -d
```

### 2. 起動確認

```powershell
docker ps
```

想定される起動コンテナは以下です。

1. `java5-legacy-dev`
2. `legacy-tomcat`
3. `legacy-mysql`
4. `legacy-phpmyadmin`

### 3. アクセス先

1. アプリ: `http://localhost:8080/legacy-app/`
2. phpMyAdmin: `http://localhost:8082/`
3. MySQL: `localhost:3306`

## ビルド方法

このプロジェクトはホストに Java / Ant を入れなくても、`java5-dev` コンテナ内でビルドできます。

### 1. 開発コンテナに入る

```powershell
docker exec -it java5-legacy-dev bash
```

### 2. WAR ビルド

```bash
ant war
```

### 3. フルビルド

```bash
ant clean build
```

ビルド結果は以下に生成されます。

1. `dist/legacy-app.war`

Tomcat は `dist` を `webapps` にマウントしているため、WAR が生成されると `legacy-app` として展開されます。

## 停止・初期化

### 停止

```powershell
docker compose -f .devcontainer/compose.dev.yaml -f .devcontainer/compose.services.yaml down
```

### DB ボリュームごと初期化

```powershell
docker compose -f .devcontainer/compose.dev.yaml -f .devcontainer/compose.services.yaml down -v
docker compose -f .devcontainer/compose.dev.yaml -f .devcontainer/compose.services.yaml up -d
```

## Dev Container としての使い方

VS Code では `.devcontainer/devcontainer.json` を使って Dev Container として起動できます。

手順は以下です。

1. ワークスペースを開く
2. `Reopen in Container` または `Rebuild Container` を実行する
3. コンテナ内ターミナルで `ant war` を実行する

## Codespaces での動作

今回の修正後、Codespaces でも動作する構成に調整済みです。

### 想定手順

1. 変更済みソースを GitHub に push する
2. Codespace を起動する
3. `Rebuild Container` を実行する
4. ターミナルで `ant war` を実行する
5. Forwarded Port の `8080` から `/legacy-app/` にアクセスする

### Codespaces で重要な点

1. `devcontainer.json` で `compose.dev.yaml` と `compose.services.yaml` の両方を読むこと
2. `lib` 配下の依存 JAR が Codespaces 側にもあること
3. 公開 URL は `/` ではなく `/legacy-app/` であること

## 初期ログイン情報

初期ユーザーは seed SQL に登録されています。

### 管理系アカウント

1. Username: `admin` / Password: `admin123`
2. Username: `manager` / Password: `manager1`
3. Username: `clerk` / Password: `clerk1`
4. Username: `superadmin` / Password: `god`

まずは `admin / admin123` を使うのが無難です。

## 今回の修正内容

### 1. Dev Container / Compose 構成の修正

対象:

1. `.devcontainer/devcontainer.json`
2. `.devcontainer/compose.dev.yaml`

対応内容:

1. `dockerComposeFile` を 2 ファイル構成に変更
2. `forwardPorts` を追加
3. `java5-dev` 側の `8080` 公開を削除し、Tomcat とポート競合しないように変更
4. network 定義の衝突を解消

### 2. MySQL 初期化 SQL の修正

対象:

1. `config/mysql/01-create-tables.sql`
2. `config/mysql/02-seed-data.sql`

対応内容:

1. 存在しない列を参照するテストデータ INSERT を修正
2. 存在しない補助テーブルを参照する seed INSERT を無効化
3. MySQL 初期化が最後まで通るように修正

### 3. ビルド依存 JAR の追加

対象:

1. `lib/`

追加した主なライブラリ:

1. Hibernate 系
2. Struts 系
3. Servlet / JSP API
4. MySQL Connector/J
5. Commons 系
6. Log4j

### 4. ビルドと配備確認

対応内容:

1. `ant war` により WAR の生成を確認
2. `dist/legacy-app.war` の作成を確認
3. Tomcat 上で `legacy-app` として展開されることを確認
4. `http://localhost:8080/legacy-app/` で HTTP 200 を確認

## 発表用まとめ

今回の対応により、レガシー Java 5 / Struts アプリを Docker ベースで再現し、ローカル・Dev Container・Codespaces で利用しやすい形に整理できました。

特に以下の実行阻害要因を解消しています。

1. Docker / Dev Container の Compose 構成不整合
2. MySQL 初期化 SQL の不整合
3. ビルド依存ライブラリ不足
4. WAR 未生成による Tomcat 未配備状態

結果として、アプリ起動、DB 接続、ログイン、開発コンテナ利用まで一通り再現可能な状態になっています。