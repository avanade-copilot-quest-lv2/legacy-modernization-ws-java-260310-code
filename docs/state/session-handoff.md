# セッションハンドオフ

## 目的

このリポジトリを題材にしつつ、最終的には **repo 非依存のレガシーモダナイゼーション Agentic Workflow** を設計する。

ただし `repo 非依存` は「同じ変換ロジックを全 repo に使う」という意味ではなく、主に次の再利用を指す。

- orchestration
- eval harness
- quality gate
- skill contracts

## 現在の認識

- beta は汎用性より精度優先でよい
- AsIs repo はローカルで起動可能であることを前提にする
- 最優先は `AsIs と同等挙動の ToBe`
- 機能追加・機能改善は beta のスコープ外
- 性能改善は原則スコープ外
- セキュリティ改善はスコープに含めたい
- `skill-creator` を meta-harness として使う前提が有力
- repo selection policy は `Fixed cohort + Rolling expansion` を採用する
- 初期コホートは `1 canonical + 2 near-neighbor + 1 adversarial` を基本にする
- beta deliverable は、複数 skill を orchestration と gate で接続した 1 本の modernization workflow とする
- support matrix は `Java legacy server-rendered web -> Spring Boot + React` に固定する
- 移行戦略は Strangler Fig、実行単位は screen / route 単位を基本にする
- artifact schema の主判定は `機械的に決定できる canonical fields` に寄せ、raw evidence は診断に回す
- artifact schema では `ui-flow` と `http` は canonical summary を主判定面とする
- artifact schema では `session-auth` は auth semantics を主判定面とし、raw auth evidence は診断に回す
- artifact schema では `external-io` は external semantics を主判定面とし、required 条件は unit manifest の critical side effects で決める
- artifact schema では `comparison / verdict` は rule-based aggregation を持ち、beta の合格判定は `pass` のみを許容する
- beta の DB 方針は `schema 維持 + access layer modernization` とし、schema migration は原則スコープ外とする

## ここまでに整理できたこと

1. core skill 候補
   - 現行分析 skill
   - characterization E2E skill
   - ToBe 提案 skill
   - 機能単位 migration 実装 skill

2. 重要な検証信号
   - characterization E2E
   - build / compile / typecheck
   - lint / structural rule
   - behavior diff
   - security gate

3. `skill-creator` から借りたいもの
   - with-skill vs baseline 比較
   - assertions
   - grader / comparator / analyzer
   - benchmark
   - human review loop

## 方針レビュー結果

ここまでの固定事項を踏まえると、beta の残タスクは「未決の大論点」より
**workflow を deterministic に実行・評価するための schema / gate / artifact 定義**
に寄ってきた。

その結果、次の見直しが必要。

- 旧 `feature slice definition` だけでは不十分
  - 既に `screen / route` を実行単位に寄せる方針は決まっている
  - いま必要なのは、fat controller や責務混在を前提にした
    `migration unit / strangler seam / change budget` の定義
- `intermediate artifact schema` が明示的に必要
  - behavior equivalence, quality gate, eval がすべて中間成果物を消費するため
  - docs より executable artifact を優先する方針とも一致する
- `repo profile schema` が必要
  - repo selection policy と intake / gating / eval を同じフォーマットでつなぐため
- `skill-creator integration` は 2 つに分けて扱うべき
  - beta で必要なのは `eval harness ownership` の決定
  - つまり、検証ループのうち何を自前で持ち、何を `skill-creator` に委譲するかを決めること
  - 一方で `skill-creator` の詳細な組み込み実装は post-beta でもよい
- `behavior equivalence` のドラフトを `docs/spec-drafts/behavior-equivalence.md` に追加した
  - 構造一致ではなく観測可能挙動を比較する
  - `must match / allowed difference / must not change` を分ける
  - migration unit ごとに `pass / fail / partial / undetermined` で判定する
- `deterministic E2E strategy` のドラフトを `docs/spec-drafts/e2e-determinism.md` に追加した
  - accessibility-first
  - 生成と実行の分離
  - network / time / randomness / data / session の固定
  - retry は診断用であり、合格の言い訳に使わない
- `artifact schema` のドラフトを `docs/spec-drafts/artifact-schema.md` に追加した
  - `共通 envelope + 種別 packet` を採用する
  - packet は `標準セット`
  - `must capture` は migration unit 依存
  - 判断軸は `再現性` と `実現可能性`
  - 主判定は `機械的に決定できる canonical fields` を優先する
  - `ui-flow` は accessibility-first canonical fields を主判定に使い、raw evidence は診断に回す
  - `http` は request/response の canonical summary を主判定に使い、raw HTTP は診断に回す
  - `db-side-effect` は `unit manifest` で required な場合のみ必須とし、既存データ契約への side effect を change summary で比較する
  - `session-auth` は auth semantics を主判定に使い、required かどうかは unit manifest の auth precondition / protected behavior で決める
  - `external-io` は external semantics を主判定に使い、payload は意味要約で比較し、stub / mock / replay も canonical field として扱う
  - `comparison / verdict` は packet / rule 結果を集約して 4 値を保持するが、beta gate では `pass` 以外をすべて不合格にする

## いま不足している仕様

1. intermediate artifact / evidence schema の詳細化
2. migration unit / strangler seam definition
3. quality gate spec
4. behavior equivalence spec のレビューと確定
5. deterministic E2E strategy のレビューと確定
6. core skill の I/O schema
7. characterization oracle の品質条件
8. repo profile schema
9. evaluation program spec
10. failure taxonomy
11. eval harness ownership decision

## 次にやること

優先順:

1. `intermediate artifact / evidence schema` を詳細化する
2. `migration unit / strangler seam definition` を作る
3. `quality gate spec` を作る
4. `behavior equivalence` ドラフトをレビューして確定する
5. `deterministic E2E strategy` ドラフトをレビューして確定する
6. `4 core skill` の I/O schema を作る
7. `characterization oracle` の品質条件を作る
8. `repo profile schema` を作る
9. `evaluation program spec` を作る
10. `failure taxonomy` を作る
11. `eval harness ownership` を決める

## いま不要になった、または後ろ倒しでよいタスク

- `deliverable を広く取るか狭く取るか` の再検討
- `ToBe を 1 つに固定するか` の再検討
- `React / Vue / Thymeleaf の再比較`
- `Java に固定するか .NET も入れるか` の再検討
- `skill-creator integration` の詳細実装

最後の項目は不要になったわけではないが、beta blocking ではなく
**ownership decision 後の post-beta task**
として扱う。

## Go / No-Go レビューで使う最終プロンプト

以下を、その時点の artifacts を添えてレビューに使う。

```text
あなたはレガシーモダナイゼーション workflow の厳格なレビュアーです。

以下の資料を読み、beta 実行可否を Go / No-Go / Conditional Go で判定してください。

レビュー対象:
- handoff 文書
- session-state.json
- docs/exploration/idea.md
- docs/exploration/research.md
- docs/decisions/repo-selection-policy.md
- docs/decisions/beta-deliverable.md
- docs/decisions/support-matrix.md
- docs/spec-drafts/behavior-equivalence.md
- docs/spec-drafts/e2e-determinism.md
- docs/spec-drafts/artifact-schema.md
- 仕様書一式
- skill 定義
- eval 定義
- benchmark 結果

判定観点:
1. beta deliverable は明確に定義されているか
2. 対応 AsIs / ToBe 範囲は明確か
3. 4 core skill の I/O contract は十分か
4. behavior equivalence の定義は grader が使える粒度か
5. quality gate は deterministic に実装可能か
6. characterization oracle の品質条件は十分か
7. migration unit / strangler seam の定義は実行可能か
8. repo selection policy は過学習を避けられるか
9. evaluation program は capability eval / regression eval を分けているか
10. eval harness ownership と skill-creator 委譲方針は具体的か

出力形式:
- 判定: Go / Conditional Go / No-Go
- 総評: 5行以内
- 致命的な欠陥: 箇条書き
- 条件付きで進められる点: 箇条書き
- 次に必ず埋めるべき不足: 優先順で箇条書き
- 判定理由: 各観点ごとに短く
```

## 運用ルール

- 重要な判断が固まったら、このファイルの「現在の認識」と `session-state.json` の両方を更新する
- review prompt は必要に応じて増強するが、判定観点の粒度は保つ
- SQL の todos はセッション内運用用、永続状態は `session-state.json` を正とする
