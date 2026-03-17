# FROM / TO Support Matrix

## 目的

beta で対象とする modernization workflow の適用範囲を明確にし、
どこを仕様で固定し、どこを skill 側で吸収するかを定義する。

beta の目的は汎用性の最大化ではなく、**測定可能な品質で成功させる範囲を固定すること** にある。

## 設計原則

1. 言語とアプリ種別は強く固定する
2. 近縁な framework / build 差分は skill 側で吸収する
3. ToBe は 1 系統に固定する
4. 移行戦略は Big Bang ではなく Strangler Fig を前提にする
5. 実行単位は feature よりも screen / route 単位を優先する

## FROM: Must support

beta で必須対応とする範囲は次。

### 言語

- Java

### Java version band

- Java 5
- Java 6
- Java 7
- Java 8

考え方:

- major version 帯は仕様で固定する
- 各バージョンの細かな差分は skill 側で吸収する

### アプリ種別

- legacy web application
- server-rendered multi-page application

補足:

- JSP / taglib / form post / redirect / session ベースの画面遷移を含む
- 一部に AJAX や jQuery 的な振る舞いが含まれていてもよい
- ただし full SPA や client-heavy frontend は対象外

### AsIs framework family

- Struts 1
- Servlet + JSP
- classic Spring MVC

考え方:

- 近縁な server-rendered Java web stack に絞る
- 画面遷移、controller、form handling、session 管理の構造が近いものを対象にする

### Build family

- Ant
- Maven

### Persistence / backend shape

- RDB を使う典型的な業務 Web アプリ
- 永続化や業務ロジックが散在していてもよい

補足:

- beta は `きれいな責務分離が済んでいる legacy app` を前提にしない
- fat controller、JSP への業務ロジック混入、helper / util への責務流出、SQL や ORM 呼び出しの分散を許容する
- 重要なのは構造の美しさではなく、現行挙動を観測して migration unit を切り出せること

### Runtime assumption

- ローカルで起動可能
- 主要画面または主要 route を観測可能
- characterization 用の操作と結果確認ができる

## FROM: Maybe support

beta では必須保証しないが、近縁として将来的に取り込みやすい範囲。

- JSF
- Tiles などの周辺 view technology
- Hibernate / iBATIS / JDBC の永続化差分
- 一部の hybrid UI

扱い:

- これらは `near-neighbor` として eval に入る可能性はある
- ただし beta の成功条件には含めない

## FROM: Out of scope

beta では対象外とする範囲。

- .NET Framework
- VB6
- PHP
- Ruby on Rails
- Node.js / Express 系 legacy app
- full SPA ベースの frontend
- desktop application
- mobile application
- Java 以外の legacy language

## TO: Must support

beta の target architecture は次に固定する。

### Backend

- Spring Boot

### Frontend

- React

### Architecture shape

- FE / BE 分離を前提にした構成
- 段階移行可能な Strangler Fig 戦略
- screen / route 単位での置き換えを基本とする

### Migration shape

- 旧画面と新画面の共存を許容する
- 1 画面または 1 route を新系に切り出して移行できる
- 新画面は React、必要な backend は Spring Boot 側に切り出す

## TO: Maybe support

将来的な拡張候補。

- Spring Boot + Thymeleaf
- Spring Boot + Vue
- monolith-first の中間着地点

扱い:

- 提案として比較対象に出すことはあっても、beta の成功条件には含めない

## TO: Out of scope

beta では対象外。

- 複数 frontend framework 同時対応
- React / Vue / Thymeleaf の自動選択
- microservices 前提の大規模分割
- native mobile frontend まで含む再構成

## 仕様で固定するもの

次は workflow / skill ではなく、仕様で固定する。

- Java legacy web であること
- server-rendered MPA であること
- AsIs framework family
- Java version band
- Build family
- ToBe が Spring Boot + React であること
- Strangler Fig / screen 単位移行であること

## Skill 側で吸収するもの

次は同系統の差分として skill 側で吸収する。

- Ant と Maven の差
- Java 5 / 6 / 7 / 8 の細部差分
- config file の配置差
- route / screen / form の naming 差
- DAO / ORM 実装の流儀差
- business logic の配置揺れ
- fat controller の分離に必要な現行分析上の差異

## この matrix が意味すること

beta で作るのは、あらゆる legacy app に対する万能 workflow ではない。

作るのは、
**Java legacy server-rendered web を Spring Boot + React に段階移行するための、高精度な modernization workflow**
である。

この固定により、次の仕様を現実的に設計できる。

- behavior equivalence matrix
- screen / route 単位 migration unit
- core skill I/O schema
- deterministic quality gate
