# Behavior Equivalence Matrix

## 状態

この文書は **レビュー用ドラフト**。

重要仕様のため、内容確認後に確定扱いにする。

## 目的

beta における `AsIs と同等挙動の ToBe` の意味を、
grader / quality gate / human review が共通で使える粒度まで固定する。

この文書は、AsIs 側の実装構造ではなく
**観測可能な振る舞い** を評価対象にする。

## 基本原則

1. まず `replication` を優先する
2. 構造ではなく観測結果を比べる
3. migration unit 単位で判定する
4. `must match` / `allowed difference` / `must not change` を分ける
5. 判定不能な項目は pass 扱いにしない

## 判定単位

behavior equivalence の最小判定単位は `migration unit` とする。

beta では migration unit を、原則として次の組み合わせで扱う。

- 1 screen または 1 route
- その screen / route に紐づく主要 form submit
- その操作で発生する主要 side effect

## 証拠の原則

behavior equivalence の判定は、最低でも次のいずれかの evidence を伴う。

- characterization E2E の実行結果
- HTTP request / response capture
- DOM assertion artifact
- DB side-effect capture
- session / cookie observation
- 外部 I/O の capture または stubbed assertion

evidence がない項目は、`未判定` として扱う。

なお、characterization E2E 自体の観測方法は
`docs/spec-drafts/e2e-determinism.md`
の原則に従う。

## 判定区分

### 1. Must match

ToBe が AsIs と同等挙動とみなされるために一致が必要な項目。

### 2. Allowed difference

modernization に伴い差が生じてもよい項目。
ただし、業務上の意味や受け入れ条件を壊してはならない。

### 3. Must not change

差分が見つかった時点で fail とみなす項目。

## Matrix

| 観点 | Must match | Allowed difference | Must not change |
| --- | --- | --- | --- |
| Route availability | 主要 entry route が存在し、到達可能であること | URL の内部実装、proxy 経由、配信経路 | 主要 route が消える、到達不能になる |
| HTTP semantics | 主要操作の成功/失敗系で期待される HTTP status 群 | 3xx の細部、内部 API の細分化 | success が error になる、認可失敗が見逃される |
| Navigation / redirect | 主要操作後の遷移先、遷移成否、戻り導線 | React router 化に伴う client-side 遷移、URL パラメータの整理 | 遷移不能、別画面に飛ぶ、二重送信で壊れる |
| DOM / UI semantics | 主要見出し、主要 form 要素、主要結果表示、主要エラーメッセージの意味 | DOM 構造、CSS class、見た目、文言の軽微な非本質差 | 必須要素欠落、操作不能、成功/失敗の判別不能 |
| Form behavior | 入力受付、validation、submit 成否、再送時の挙動 | client-side validation の追加、補助 UI の改善 | 必須 validation が抜ける、送信結果が変わる |
| Session / auth | login 状態、logout、権限制御、session 継続/失効の業務意味 | session ID の形式、cookie 名、token 化の内部差 | 認証回避、権限昇格、未認証アクセス許容 |
| Business outcome | ユーザーが観測する業務結果、一覧/詳細/登録/更新/削除の意味 | 内部実装、API 分割、保存順序の一部 | 業務ルール違反、更新漏れ、重複作成 |
| DB side effect | 主要テーブル群への create/update/delete の有無と結果整合 | 監査列、内部 ID 採番、補助テーブル更新 | 意図しない更新、更新不足、重複副作用 |
| External I/O | 呼び出し有無、主要 payload 意味、失敗時ハンドリング | payload 順序、ヘッダの一部、内部リトライ | 呼び出し消失、誤送信、失敗隠蔽 |
| Error handling | 主要異常系でユーザー/運用が認識すべき失敗が表面化すること | 画面文言、エラー表示位置、trace ID 表示 | silent failure、成功に見える失敗 |
| Security invariants | 認可、入力検証、CSRF 相当、機密情報露出防止 | 実装方式、ライブラリ、middleware 構成 | 既存より弱い security posture |
| Observability / traceability | migration unit ごとに何を比較したか追跡できること | ログ形式、レポートのフォーマット | 判定根拠が残らない |

## Must match の詳細ルール

### Route / navigation

- migration unit の entry point は到達できること
- 主要導線は characterization で再現できること
- submit 後の遷移結果は業務的に同じこと

### DOM / UI

- grader は DOM 全体の一致を要求しない
- grader は `role / label / visible text / actionability` を優先する
- 重要なのは「同じ業務操作が可能であること」と「結果が読めること」

### Session / auth

- login 必須画面は login 後にだけ到達できること
- logout 後は保護画面に戻れないこと
- 権限制御の結果は AsIs と同じこと

### DB side effect

- 主対象テーブルの状態整合を重視する
- 監査列や timestamp の完全一致は要求しない
- 二重送信やリロードで副作用が増えないことを確認する

### External I/O

- beta では本番外部接続よりも capture / mock / stub を優先してよい
- ただし、呼び出しの有無と payload の意味は比較対象に含める

## Allowed difference のガードレール

Allowed difference は、次を満たす場合にのみ許容する。

1. 業務結果を変えない
2. characterization assertion を壊さない
3. security posture を弱めない
4. 運用上の観測可能性を失わない

例:

- JSP の DOM 構造が React で変わる
- redirect が SPA navigation に置き換わる
- validation が server-side only から client + server に増える
- message wording が軽微に変わる

## Fail にすべき差分

次は原則として fail。

- 主要 route がなくなる
- 主要 form が送れない
- login / authorization の意味が変わる
- 一覧件数や詳細内容の業務意味が変わる
- DB 更新が欠ける、増える、重複する
- 外部連携が消える、誤送信する
- エラーが成功に見える
- security issue を新たに導入する

## Evidence ごとの判定方針

### Characterization E2E

- 主判定ソース
- ユーザー観点の equivalence を確認する

### HTTP packet

- navigation / redirect / status / payload を補強する
- UI だけでは判定しにくい API 差分に使う

### DB packet

- side effect の有無と整合を確認する
- 主要テーブルを優先し、補助テーブルは後順位とする

### Session evidence

- auth / timeout / privilege の確認に使う

### Human review

- allowed difference と must not change の境界で使う
- 特に legacy 特有の曖昧なメッセージや複雑な帳票系画面で使う

## 判定結果

各 migration unit の判定結果は次の 4 値で持つ。

- `pass`
- `fail`
- `partial`
- `undetermined`

### pass

- must match を満たし
- must not change に抵触せず
- 未判定項目が受容範囲内である

### fail

- must not change に抵触
- または must match の重大項目を満たさない

### partial

- 主要導線は満たすが、周辺 assertion が不足または一部不一致

### undetermined

- evidence 不足で判定不能
- beta gate では原則 pass 扱いにしない

## Beta の最低合格線

1 migration unit を完了扱いにする最低条件は次。

1. 主要 characterization E2E が green
2. 主要 route / navigation が一致
3. auth / session の意味が一致
4. 主要 DB side effect が一致
5. must not change 差分が 0
6. security regression が 0

## この spec が次に要求するもの

この文書の次に必要なのは次。

1. intermediate artifact / evidence schema
2. characterization oracle requirements
3. quality gate spec
4. migration unit / strangler seam definition

## References

- Reverse Segawa, "Harness Engineering Best Practices 2026"
- Mechanical Orchard, "Verify, then trust: a closed-loop method for AI-powered legacy modernization"
