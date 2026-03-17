# Artifact Schema

## 状態

この文書は **レビュー用ドラフト**。

重要仕様のため、内容確認後に確定扱いにする。

ただし次の方向性は確認済み:

- `共通 envelope + 種別 packet` を採用する
- packet は `標準セット` を採用する
- `must capture` は migration unit 依存で宣言する
- 判断軸は `再現性` と `実現可能性` を優先し、agent の検証ループに十分な厳しさを持たせる
- 主判定は `機械的に決定できる canonical fields` に寄せ、raw evidence は診断に使う

## 目的

artifact schema は、modernization workflow の各 skill が
「何を観測し」「何を保存し」「何を比較と判定に渡すか」
を揃えるための共通契約である。

beta では、artifact schema を次の接続点として使う。

- current-state analysis
- characterization E2E
- comparator / grader
- quality gate
- human review

この文書がないと、各 skill が独自形式の出力を返し、
deterministic な検証ループを組みにくい。

## 設計原則

artifact schema は次を満たすべきとする。

1. machine-judgeable である
2. local / CI の両方で再現可能である
3. 深い侵襲的 instrumentation をデフォルトで要求しない
4. partial evidence を明示的に表現できる
5. `capture failure` と `behavior failure` を分離できる

追加原則:

6. `機械的に決定できるものはすべて機械的に決定する`
7. raw artifact は原則として主判定に使わず、canonical fields の補助証拠として使う

## 全体像

beta の artifact schema は、1 つの巨大な JSON に何でも押し込む形ではなく、
次の 2 層で構成する。

1. 共通 envelope
   - 実行条件と比較条件を揃えるメタ情報
2. 種別 packet
   - UI, HTTP, DB, session/auth, external I/O, comparison などの証拠本体

この構成を採る理由は次。

- run 条件の差と behavior 差を分離しやすい
- packet の追加や無効化を unit ごとに扱いやすい
- legacy repo ごとの差に耐えつつ、grader 側の実装を共通化しやすい

## 採用方針

### 1. Packet-first で設計する

artifact schema の詳細化は、envelope の厚さより先に
`各 packet の主判定面をどう定義するか`
から決める。

理由:

- quality gate の deterministic 性は packet の canonicalization に依存する
- envelope は再現条件の保持に重要だが、比較器の実装を直接決めるのは packet 側である
- 先に packet を固めると、envelope に本当に必要な項目も逆算しやすい

### 2. Envelope の厚さ

`中くらい` を採用する。

理由:

- `薄い` envelope では再現条件が不足しやすい
- `厚い` envelope では beta の運用負荷が大きすぎる
- `中くらい` なら再現性と実現可能性のバランスがよい

想定する必須項目の種類:

- run identity
- migration unit identity
- environment summary
- actor / auth context
- dataset / seed / clock

ここまでに確認できた方針:

- `tool / runtime version summary` は必須にする
- `actor / auth context` は常に必須にする
- `dataset / seed / clock` は常に必須にする

`詳細まで必須` にはしない。

理由は、再現性の手掛かりは確保したいが、
beta では full dependency snapshot や host 詳細の常時採取までは要求しないため。

現時点の envelope 必須フィールド案:

- `run.id`
- `run.started_at`
- `repo.ref`
- `unit.id`
- `unit.kind`
- `env.summary`
- `env.tool_versions`
- `actor.kind`
- `auth.context`
- `data.dataset_id`
- `data.seed`
- `data.clock_mode`

上の項目名はまだ提案であり、粒度は後続レビューで確定する。

### 3. Packet 種別

beta の `標準セット` は次。

- `ui-flow packet`
- `http packet`
- `db-side-effect packet`
- `session-auth packet`
- `external-io packet`
- `comparison / verdict packet`

この標準セットは、fat controller や責務混在がある legacy を前提に、
UI だけでは見えない side effect も比較対象に含めるための最低線とする。

### 4. Must capture policy

`unit 依存` を採用する。

つまり、すべての migration unit で全 packet を必須化するのではなく、
各 unit ごとに「どの packet を必須証拠とするか」を宣言する。

これにより次を両立する。

- 厳しさの維持
- 不要な計測の削減
- legacy repo ごとの差への耐性

## Packet の役割

### UI flow packet

- 画面導線
- 操作ステップ
- accessibility-first assertion
- 必要に応じた screenshot / DOM / accessibility snapshot

beta では、`ui-flow packet` は
**主判定用の canonical fields** と
**診断用の raw evidence refs**
を分けて持つ方針にする。

理由:

- 機械比較できる部分は最大化したい
- ただし DOM 全文や accessibility tree 全文を主判定にすると
  非本質差分による誤検知が増えやすい
- そのため、主判定は正規化済みの比較項目に寄せ、
  生の snapshot や trace は診断と human review に回す

`ui-flow packet` の目的は次。

1. ユーザーがたどる主要導線を再現可能に記録する
2. UI 上の主要観測結果を機械比較可能にする
3. failure 時に追加証拠へ辿れるようにする

現時点の必須候補は次。

- `flow.id`
- `flow.entry_route`
- `flow.steps`
- `flow.final_ui_state`
- `flow.navigation_result`
- `flow.assertion_summary`
- `flow.evidence_refs`

`flow.steps` の候補:

- `step.index`
- `step.kind`
- `step.target`
- `step.locator_strategy`
- `step.navigation_hint`
- `step.expected_ui_semantics`
- `step.observed_ui_semantics`
- `step.result`

`observed_ui_semantics` では次を canonical 化して扱う案を優先する。

- role
- accessible name
- label
- state
- actionability
- visible text summary

raw evidence は主に次を参照で持つ想定:

- screenshot
- accessibility snapshot
- DOM snapshot
- Playwright trace

この packet では、
raw artifact 自体を常に機械比較対象にはしない。

raw artifact は次の場合の補助証拠として使う。

- canonical fields だけでは差分理由が分からないとき
- layout / overlap / hidden rendering など視覚問題を疑うとき
- human review が必要な allowed difference 境界の確認

navigation の持ち方は次を採用する。

- step ごとには `lightweight navigation hint` を持つ
- flow 全体では `final navigation result` を持つ

理由:

- 途中 step での遷移失敗や想定外遷移を追いやすい
- ただし各 step に完全な navigation artifact を持たせると重すぎる
- 最終的な比較判定は flow 全体の到達結果で行いやすい

ここまでに合意した主判定方針は次。

- `ui-flow packet` の主判定は canonical fields のみで行う
- raw evidence は fail / undetermined 時の診断と human review に限定して使う
- `flow.steps` は `操作 + expected semantics + observed semantics` を持つ
- `observed_ui_semantics` は accessibility-first で canonical 化する

主判定の canonical fields 候補:

- `flow.id`
- `flow.entry_route`
- `flow.steps`
- `flow.navigation_result`
- `flow.final_ui_state`
- `flow.assertion_summary`

`observed_ui_semantics` の canonical 要素:

- `role`
- `accessible_name`
- `label`
- `state`
- `actionability`
- `visible_text_summary`

### HTTP packet

- request / response の要約
- status / redirect / payload 意味
- UI だけでは分かりにくい API 差分の補助証拠

beta では、`http packet` も
**主判定用 canonical summary** と
**診断用 raw request / response refs**
を分けて持つ。

理由:

- raw HTTP 全文比較は header 順、trace id、内部 API 分割などの非本質差分に弱い
- modernization 後は URL 構成や redirect 実装が変わっても、業務意味は維持されうる
- 比較器は HTTP interaction の `業務意味` を機械比較できるべきである

ここまでに合意した主判定方針は次。

- `http packet` の主判定は canonical summary のみで行う
- request は `method + route semantics + input summary` を比較する
- response は `status semantics + redirect target semantics + payload semantics / error semantics` を比較する
- raw request / response は fail / undetermined 時の診断に限定して使う

主判定の canonical fields 候補:

- `http.id`
- `http.interaction_kind`
- `http.request.method`
- `http.request.route_semantics`
- `http.request.input_summary`
- `http.response.status_semantics`
- `http.response.redirect_target_semantics`
- `http.response.payload_semantics`
- `http.response.error_semantics`
- `http.assertion_summary`

### DB side-effect packet

- 既存データ契約に対する主要 side effect の change summary
- operation type
- row count
- key field / business-critical field 単位の整合確認

DB については、beta で次の前提を置く。

- DB schema と業務データ契約は原則維持する
- ToBe runtime で必要な DB access layer の近代化は許可する
- ただし変更は schema 非破壊かつ migration unit の change budget 内に限る
- DB server/version 互換性の根本解決、schema migration、大量データ移行は beta の自動化対象外とし escalation する

そのため `db-side-effect packet` の目的は、
schema migration の追跡ではなく
**既存データ契約に対する副作用が同等か**
を比較することに置く。

ここまでに合意した主判定方針は次。

- `db-side-effect packet` は全 migration unit で一律必須にはしない
- `unit manifest.required_packets` に `db-side-effect` がある場合のみ capture 必須とする
- 主判定は `change summary` で行い、row 全文比較はしない
- `change summary` では `key fields + business-critical fields` を比較する
- 監査列、timestamp、内部 ID は原則 `allowed difference` とする
- raw rows / dump は fail / undetermined 時の診断に限定して使う

主判定の canonical fields 候補:

- `db.id`
- `db.interaction_kind`
- `db.target_group`
- `db.change_summary[]`
- `db.duplicate_side_effect_check`
- `db.assertion_summary`

`db.change_summary[]` の候補:

- `table_semantics`
- `physical_table_name`
- `operation_type`
- `row_count_delta`
- `key_field_alignment`
- `business_field_summary`
- `integrity_result`

### Session-auth packet

- login state
- role / privilege context
- cookie / storage / session mutation の要約

beta では、`session-auth packet` も
**主判定用 auth semantics** と
**診断用 raw auth evidence refs**
を分けて持つ。

理由:

- 比較したいのは cookie 名、session ID、token 形式そのものではなく、
  認証状態、権限制御、到達可否、logout、失効挙動の業務意味である
- ToBe では session mechanism が Spring Security などに置き換わる可能性が高い
- raw cookie / storage / session state を主判定に使うと implementation lock-in が強すぎる

ここまでに合意した主判定方針は次。

- `session-auth packet` の主判定は auth semantics のみで行う
- `session-auth packet` は全 migration unit で一律必須にはしない
- `unit manifest` に `auth precondition` または protected behavior が宣言された場合のみ required とする
- cookie 名、session ID、token 形式は主判定に使わない
- CSRF 相当や session regeneration の観測結果は packet に持てるようにする
- raw auth evidence は fail / undetermined 時の診断に限定して使う

主判定の canonical fields 候補:

- `auth.id`
- `auth.actor_kind`
- `auth.authentication_state`
- `auth.effective_privileges`
- `auth.protected_resource_access`
- `auth.login_result`
- `auth.logout_result`
- `auth.session_continuity`
- `auth.session_expiry_behavior`
- `auth.csrf_semantics`
- `auth.assertion_summary`

raw auth evidence refs の候補:

- cookie / storage snapshot ref
- redirect chain ref
- auth challenge response ref
- relevant headers ref

### External-io packet

- mail / file / queue / webhook / third-party API などの外部接続証拠
- 実呼び出しまたは stub / mock / replay の条件

beta では、`external-io packet` も
**主判定用 external semantics** と
**診断用 raw external evidence refs**
を分けて持つ。

理由:

- 比較したいのは payload 全文や delivery metadata の完全一致ではなく、
  何の外部連携が、どの条件で、どんな業務意味で行われたかである
- modernization 後は integration 実装、署名、header、内部 retry 制御が変わりうる
- raw payload を主判定にすると non-essential diff で壊れやすい

ここまでに合意した主判定方針は次。

- `external-io packet` の主判定は external semantics のみで行う
- `external-io packet` は全 migration unit で一律必須にはしない
- `unit manifest.critical_side_effects` に外部連携が宣言された場合のみ required とする
- payload は基本 `payload semantics summary` で比較する
- `real / stub / mock / replay` を canonical field に持つ
- retry / delivery の詳細は `failure_handling_semantics` と `idempotency_semantics` に要約して比較する
- raw external evidence は fail / undetermined 時の診断に限定して使う

主判定の canonical fields 候補:

- `external.id`
- `external.integration_kind`
- `external.operation_semantics`
- `external.target_semantics`
- `external.delivery_mode`
- `external.call_presence`
- `external.payload_semantics`
- `external.failure_handling_semantics`
- `external.idempotency_semantics`
- `external.assertion_summary`

raw external evidence refs の候補:

- raw request/body ref
- generated file ref
- outbound message ref
- mock server capture ref
- delivery log ref

### Comparison / verdict packet

- must match
- allowed difference
- undetermined
- final judgment
- reasoning / evidence refs

beta では、`comparison / verdict packet` は
**生差分の倉庫** ではなく、
**各 packet の比較結果を rule-based に集約して migration unit 判定を出す packet**
として定義する。

理由:

- quality gate と grader は narrative ではなく deterministic な構造化判定を必要とする
- packet ごとの差分だけを並べても、最終的な合否条件が曖昧になる
- 複数 repo の検証ループで一貫した判定を出すには rule-based aggregation が必要である

ここまでに合意した主判定方針は次。

- `comparison / verdict packet` は `rule evaluation` を中心に持つ
- `packet_results` と `rule_results` を分けて持つ
- `final_judgment` は `pass / fail / partial / undetermined` の 4 値を保持する
- ただし beta の合格条件では `pass` のみ許容する
- `fail / partial / undetermined` はすべて gate 上は不合格とする
- `final_judgment` は rule-based aggregation で決定する
- `human_review_required` は機械判定不能な `allowed difference` 境界に限定して立てる
- `final_reason_codes` は failure taxonomy と接続できる構造化コードで持つ

主判定の canonical fields 候補:

- `comparison.id`
- `comparison.unit_id`
- `comparison.packet_results[]`
- `comparison.rule_results[]`
- `comparison.coverage_summary`
- `comparison.capture_failures[]`
- `comparison.behavior_failures[]`
- `comparison.allowed_differences[]`
- `comparison.undetermined_items[]`
- `comparison.final_judgment`
- `comparison.final_reason_codes`
- `comparison.human_review_required`
- `comparison.evidence_refs`

推奨する rule-based aggregation の基本順序:

1. `must_not_change` 違反が 1 つでもあれば `fail`
2. required packet の `capture failure` があれば `undetermined`
3. 主要 `must_match` 違反があれば `fail`
4. 周辺 assertion の不足または一部未確定があれば `partial`
5. すべて満たした場合のみ `pass`

この 4 値は状態表現として保持するが、
beta の quality gate と evaluation では
`pass` だけを成功として扱う。

## Artifact schema が解決したい問題

この schema で特に解決したいのは次。

1. E2E の結果を比較器がそのまま消費できること
2. `再現できない失敗` と `実際の挙動差分` を分けられること
3. `何が未判定か` を機械的に残せること
4. human review を evidence 付きで限定的に使えること

## 次に決めること

artifact schema の次の詳細論点は次。

1. 各 packet の必須フィールドと optional フィールド
2. packet 間の参照方法
3. capture failure / behavior failure / comparison failure の区別方法
4. `unit manifest` の必須項目

## Unit manifest の扱い

`unit 依存 must capture` は、envelope に埋め込まず
`unit manifest` として分離する方針を採る。

理由:

- envelope を実行結果の記録に専念させられる
- `期待された capture` と `実際に得られた evidence` を比較しやすい
- quality gate 側で暗黙ルールを持つより、判定根拠を明文化しやすい

この manifest には少なくとも次が必要になる想定:

- unit id
- unit kind
- required packets
- optional packets
- critical side effects
- auth precondition
- deterministic controls required

ここまでに合意した追加ルール:

- `required packets` は quality gate の hard requirement と直結させる
- `required packet` が不足した場合は `behavior failure` ではなく `capture failure` として扱う
- `critical side effects` に DB が宣言されている場合のみ DB 比較を hard gate に含める

## 関連文書

- `docs/spec-drafts/behavior-equivalence.md`
- `docs/spec-drafts/e2e-determinism.md`
