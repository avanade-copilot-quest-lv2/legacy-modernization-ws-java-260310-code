# アイデア: レガシーモダナイゼーション向け Agent Skills / Workflow

## まず目的をはっきりさせる

作りたいのは「この repo を変換するスキル」そのものではなく、より一般に使える

- レガシーコードベースの modernization task を実行する Agentic Workflow
- もしくは、その workflow の中で使う Agent Skills 群

この repo はその設計を考えるためのケーススタディとして使う。

## 初期方針

最初から万能な modernization agent を目指すより、層を分けた方がよさそう。

### Layer 1: 汎用 orchestration workflow

対応対象の legacy repo に対して共通に回す部分。

1. repo intake
2. stack / build / runtime / config / UI surface / persistence style の検出
3. リスク判定と automation 可否の判定
4. characterization 資産の生成
5. 変換計画の作成
6. repo 固有 skill pack の選択または生成
7. 小さな単位での変換実行
8. quality gate による検証

### Layer 2: repo 固有 skill pack

ここで repo 依存の知識を executable にする。

- `stack-intake-*`
- `characterization-e2e-*`
- `framework-transform-*`
- `validation-and-diff-review-*`

つまり、汎用化の本体は「すべてを同じ prompt で変換すること」ではなく、
「同じ手順で repo を理解し、必要な skill を組み立てること」にあるかもしれない。

## 有力な仮説: 最初は対象を絞った方が品質が出る

品質を出したいなら、AsIs と ToBe の両方をある程度固定した方がよさそう。

### 初期スコープ案

AsIs:

- Java 5-8 の server-rendered Web アプリ
- Struts 1 / classic Spring MVC / servlet + JSP
- Ant または Maven
- MySQL などの RDB

ToBe:

- Spring Boot + Vue
- あるいは先に Spring Boot monolith 化、その後にフロント分離

理由:

- Agentic modernization の失敗要因は hidden variability が大きい
- stack の揺れを減らすと、prompt, quality gate, playbook を安定化しやすい

## workflow の形のたたき台

### A. Intake / Normalize

成果物:

- stack fingerprint
- file / layer map
- risk register
- config inventory
- UI flow inventory
- 推奨 modernization path

### B. Characterization

成果物:

- E2E smoke suite
- screen inventory
- route / action / JSP mapping
- 重要な business invariant のメモ

### C. Transformation Design

成果物:

- feature 単位または bounded context 単位の slice plan
- AsIs -> ToBe の mapping rule
- automation 可能領域と human review 必須領域の切り分け

### D. Execution

成果物:

- 生成コード
- migration note
- 未解決論点

### E. Verification

成果物:

- build / test / E2E 結果
- behavior diff
- rollback / escalation 判断

## 必須に見える quality gate

1. Environment readiness gate
   - ツールチェインがある
   - 依存関係がある
   - 少なくとも解析または起動ができる

2. Characterization gate
   - 構造変更の前に executable な safety net がある

3. Scope gate
   - 変換対象の slice が明示されていて大きすぎない

4. Diff gate
   - 生成差分が設計意図と照合される

5. Validation gate
   - build / tests / E2E / regression が通る

6. Escalation gate
   - 曖昧な config, dead code, auth/security, data migration リスクでは自動化を止める

## 検証ループはどう設計するべきか

結論としては、`最終判定はテスト中心` にしつつ、途中のフィードバック信号は複数持つのがよい。

### 最終判定に向いているもの

最終的な Done 判定に使いやすいのは、やはり実行可能で pass/fail が明確なもの。

- characterization E2E
- 既存の自動テスト
- build 成功
- type check / compile 成功
- migration 後の smoke test 成功

beta の目的が `AsIs と同等挙動の ToBe` なら、最重要なのは E2E を含むテスト結果。

### テスト以外に使える検証信号

ただし skill の品質改善や自己修正ループのためには、テスト以外の signal もかなり重要。

#### 1. build / compile / typecheck

- 壊れていないことの最小確認
- 実装 skill の早いフィードバックに使える

#### 2. lint / structural rule

- target architecture に沿っているか
- 禁止 API や禁止依存を使っていないか
- 新規コードが正しい layer / path に置かれているか

#### 3. route / screen / endpoint coverage

- 現行分析で見つけた対象機能が、characterization に反映されているか
- E2E が重要画面や主要経路を取りこぼしていないか

#### 4. behavior diff

- 画面遷移
- HTTP status / response shape
- DOM の主要要素
- 重要文言
- DB 更新有無

要するに、`通った / 落ちた` だけでなく `AsIs と何が違ったか` を比較できる signal。

#### 5. security gate

- 既知の危険 API の不使用
- 認証/認可/入力検証の必須化
- dependency / SAST 系チェック

beta で性能改善は外しても、セキュリティは gate に含める価値が高い。

#### 6. config / contract consistency

- 環境変数/設定キーの整合
- 外部 I/O 契約の維持
- DB schema や API contract の破壊的変更がないか

### 使い分け

ここは分けて考えた方がよい。

- `途中の自己修正` に効くもの
  - lint
  - compile
  - typecheck
  - small smoke test
- `完了判定` に効くもの
  - characterization E2E
  - build
  - 既存 test suite
  - security gate

つまり、skill をつなげた最後に「テストが通るか」だけを見るのでは少し弱い。
その前に何度も軽い検証で戻せるようにして、最後に重いテストで締める方がよい。

## beta workflow 向けの validation 設計案

### レイヤー1: intake validation

- AsIs が起動できる
- 必要なログイン/主要画面に到達できる
- 観測対象の機能一覧が作れる

### レイヤー2: characterization validation

- 主要機能ごとに E2E がある
- 期待結果が assertions として明示されている
- flaky すぎない

### レイヤー3: implementation validation

- 変更ごとに compile / lint / unit レベルの高速検証
- migration unit ごとに smoke / integration 検証

### レイヤー4: completion validation

- 対象 slice の E2E 全通
- build 通過
- target architecture の rule 通過
- security gate 通過

## いまの仮説

skill を強くするには、`1つの巨大 skill` を賢くするより、

- skill ごとに入出力を固定する
- 各 skill に「何で検証するか」を持たせる
- 後段の skill は前段の成果物を検証にも使う

の方がよい。

たとえば:

- 現行分析 skill の出力 → characterization skill の入力兼検証対象
- characterization E2E → 実装 skill の acceptance criteria
- ToBe ADR / rule → 実装 skill の structural gate

この形にすると、repo が変わっても validation loop 自体はかなり再利用しやすい。

## `.github/skills/skill-creator` を前提にする案

これはかなり良い方向だと思う。

`skill-creator` の本質は、単に skill を書くことではなく、

- skill を draft する
- eval prompt を作る
- with-skill / baseline を並走させる
- assertions を後追いで固める
- grader で採点する
- benchmark / viewer / human feedback を回す
- 改善して次 iteration に進む

という `skill 用の検証ハーネス` を持っている点にある。

今回ほしい modernization 系 skill も、この枠組みにかなり乗せやすい。

### 使えそうな構成要素

#### 1. with-skill vs baseline 比較

`skill-creator` は各 eval について

- with_skill
- without_skill あるいは old_skill

を並行実行する。

これは modernization skill にもそのまま効く。

例:

- 現行分析 skill あり vs なしで、抽出できる route / screen / config 情報量を比較
- characterization skill あり vs なしで、E2E の網羅性や assertion の質を比較
- migration 実装 skill の新バージョン vs 旧バージョンで、同等挙動再現率を比較

#### 2. assertions を quantitative に持つ設計

`skill-creator` は、まず eval prompt を走らせ、走っている間に assertion を書く。

この考え方はとても良い。

modernization でも、まず

- この機能を移行する
- この画面を再現する
- このログインフローを ToBe に載せる

という task を実行し、その後で

- 何をもって pass とするか
- 何が missing なら fail か

を assertions に落とせる。

#### 3. grader / comparator / analyzer の三層

`skill-creator` には

- `grader.md`
- `comparator.md`
- `analyzer.md`

があり、かなり重要。

modernization に写像すると:

- grader:
  - E2E, outputs, transcript, generated files を見て assertion を採点
- comparator:
  - 2 つの migration 結果を blind に比べる
- analyzer:
  - どの制約や instruction が効いたかを事後分析する

つまり、単に pass/fail だけでなく、`なぜ勝ったか / なぜダメだったか` を skill 改善に戻せる。

#### 4. benchmark に timing / token / variance を持つ

`skill-creator` は pass_rate だけでなく、time / tokens / variance を持つ。

今回、性能改善自体は beta の主目的ではないが、skill の安定性を見るには重要。

たとえば modernization skill では:

- 同じ eval を 3 回回して再現率を見る
- E2E の pass 率の分散を見る
- skill の適用で token 消費が極端に増えていないかを見る

といった使い方ができる。

#### 5. description optimization loop

`run_eval.py` + `run_loop.py` で skill description の trigger 精度を最適化しているのも強い。

今回も、

- 現行分析 skill はどんな依頼で trigger すべきか
- characterization skill はどんな文脈で読むべきか
- migration 実装 skill はどんな slice 指示で使うべきか

を trigger eval で詰められる。

## modernization 向けにどう適用するか

### A. meta-harness として skill-creator を使う

この案が一番自然。

つまり、

- 今回作る modernization skills 自体は `.github/skills/...` に置く
- それらの品質改善には `skill-creator` を使う

という二層構造にする。

### B. eval 単位を「機能 slice」にする

`skill-creator` の eval の最小単位は、今回なら migration unit がよい。

例:

- ログイン
- 商品検索
- カート投入
- 受注確認

各 eval に対して

- AsIs の操作
- 期待される挙動
- 重要 assertion

を持たせる。

### C. baseline の定義

modernization skill では baseline を次のように分けるとよい。

- 現行分析 skill:
  - no-skill baseline
- characterization skill:
  - no-skill baseline
  - 旧バージョン skill baseline
- migration 実装 skill:
  - 旧 iteration skill baseline
  - あるいは人手で作った参照実装があればそれ

### D. grading 対象

grading は transcript だけでは弱いので、次を評価対象にする。

- 生成された E2E
- 実装差分
- build / lint / test 結果
- 実行時の DOM / network / screenshot / output file
- user_notes

これは `grader.md` の考え方とかなり相性がよい。

### E. human review を viewer に乗せる

`skill-creator` の viewer 思想も有効。

特に modernization では、

- 旧画面と新画面の比較
- E2E 実行結果
- assertion pass/fail
- コメント

を機能ごとに見られる UI があると、改善ループが回しやすい。

## 現時点の提案

私は、今回の skill 群は `skill-creator を使って作る前提` で進めるのに賛成。

具体的には:

1. `skill-creator` を meta-harness として採用する
2. 最初の modernization skill を 1 つずつ小さく作る
3. 各 skill に対して eval prompt / assertion / baseline / grading を持つ
4. 機能 slice 単位で benchmark する
5. 人間レビュー + quantitative benchmark で iteration する

この形なら、`検証ループをものすごく厳しくして、対象範囲のいろいろな repo に対応できる` という方向にかなり近づける。

## まず作るなら何か

`skill-creator` 前提なら、最初に作るべきなのは migration 実装 skill そのものより、

- 現行分析 skill
  または
- characterization E2E skill

のどちらかだと思う。

理由は、どちらも比較的 eval を作りやすく、後続 skill の検証基盤にもなるから。

## 仕様化の前に「不足点の明確化」は必要か

必要。

ただし、ここでいう不足点の明確化は、漫然と課題を並べることではなく

- 何が未定義だと build できないのか
- 何が未定義でも後回しにできるのか

を切るための作業と捉えるべき。

つまり流れとしては:

1. 不足点の棚卸し
2. その中で beta を止める blocking issue を特定
3. 先に決める仕様だけを固定
4. 残りは open question として残す

がよい。

今の段階で blocking に近いのは次。

- beta deliverable の定義
- 対応 repo 範囲
- core skill の I/O schema
- behavior equivalence spec
- quality gate spec
- eval program の設計

## skill の有効性をどう検証するか

ここは `skill 単体` と `workflow 全体` を分けて考えるべき。

### A. skill 単体の有効性

skill 単体では次を見る。

- trigger 精度
- 成果物の品質
- 再現率
- 所要時間
- token 消費
- 失敗パターン

つまり `できたか` だけでなく

- どのくらい安定してできるか
- どのくらいコストがかかるか

まで見る。

### B. workflow 全体の有効性

workflow 全体では次を見る。

- migration unit の migration 成功率
- AsIs と ToBe の同等挙動率
- 人手介入量
- 1 slice あたりの end-to-end 所要時間
- 対応できる repo クラスの広さ

こちらは skill 単体よりも、複数 skill をつないだ結果を見る。

## 検証プログラムとして何を回すべきか

ここはかなり重要で、beta 開発そのものと同じくらい設計が必要。

### 1. task / trial / suite の整理

Anthropic の eval の考え方を使い、次で分ける。

- task
  - 1 つの repo における 1 つの migration unit
- trial
  - その task を 1 回実行した結果
- suite
  - 同じ目的の task 群

今回なら suite は少なくとも 3 種類ある。

- 現行分析 suite
- characterization suite
- migration 実装 suite

### 2. capability eval と regression eval を分ける

- capability eval
  - 新しい repo / 新しい migration unit でどこまでできるかを測る
- regression eval
  - 一度通った task が今も通るかを測る

これを混ぜると判断を誤りやすい。

## 対象 repo をどう選ぶか

これも最初から重要。

### 方針

beta では「広く集める」より「代表性を持った少数」を選ぶ方がよい。

狙いたいのは統計的な網羅ではなく、`失敗しやすい差分をちゃんと含んだ評価集合`。

### まず持つべき repo セット

最低でも次の 3 種類に分けたい。

#### 1. Canonical repo

- 最も成功してほしい代表例
- たとえば今の Struts/JSP/Hibernate 系

用途:

- 最初の capability を作る
- 開発速度を上げる

#### 2. Near-neighbor repo

- 同系統だが少しだけ違う
- 例:
  - ビルドが Maven
  - JSP 構造が少し違う
  - DB アクセスの癖が違う

用途:

- repo 非依存の再利用部分がどこまで効くかを見る

#### 3. Adversarial repo

- beta の範囲内ではあるが難所が強い
- 例:
  - 設定が散らばっている
  - 画面遷移が複雑
  - テストが皆無
  - dead code が多い

用途:

- failure mode を炙り出す
- gate の有効性を見る

### repo 選定基準

repo は次の軸で表にして選ぶのがよい。

- AsIs stack
- build/run 難易度
- UI の観測しやすさ
- DB / integration 複雑度
- auth の複雑度
- migration unit の切りやすさ
- dead code / config ambiguity
- 既存テスト有無

つまり、単に「似た repo を集める」のではなく、変動要因が見えるように選ぶ。

## 何種類くらいの repo をやるべきか

beta の最初は 3〜5 repo くらいが現実的だと思う。

- 1 repo だけだと過学習する
- 10 repo 以上だと設計が固まる前に収拾がつかない

推奨イメージ:

- 1 canonical
- 2 near-neighbor
- 1-2 adversarial

## 各 repo の結果をどう吸収するか

ここを設計しないと、ただ試して終わる。

### 吸収の単位

各 trial から最低でも次を残す。

- repo profile
- task definition
- transcript
- outcome
- grading result
- timing / token
- human feedback
- failure taxonomy

### failure taxonomy を持つ

結果を改善に戻すには、失敗を分類する必要がある。

たとえば:

- trigger failure
- intake failure
- characterization failure
- oracle weakness
- architecture decision failure
- implementation failure
- validation failure
- environment/setup failure

各失敗をこの taxonomy に落とし、どの layer を改善すべきかを記録する。

### 吸収先

repo ごとの学びは、次の 4 箇所に分けて吸収する。

1. root workflow
   - 共通 gate
   - 共通 orchestration

2. stack-specific skill
   - Struts/JSP 向けの手順や rule

3. eval suite
   - 新しい failure case を regression に追加

4. repo profile / support matrix
   - どの repo class に対応できるか更新

## 改善ループの設計案

### 1. まずは少数 repo で capability を作る

- canonical repo で skill を成立させる
- near-neighbor で壊れる箇所を知る

### 2. 失敗ケースを regression 化する

- 失敗した migration unit を eval に追加
- 以後は必ず再実行する

### 3. repo ごとに直すのではなく、layer に戻す

修正方針は次を優先する。

- まず root workflow を直せるか
- 次に stack skill を直せるか
- 最後に repo-specific workaround を許すか

repo 固有対応を安易に増やすと、すぐ破綻する。

### 4. 定期的に support matrix を更新する

結果として、beta の時点でも次が見える状態にしたい。

- 何に対応できるか
- 何はまだできないか
- どこで人手が必要か
- どの失敗が多いか

## 現時点の提案

仕様化と並行して、`評価プログラムの仕様` も作るべき。

具体的には次の文書が必要そう。

1. repo profile schema
2. task / trial / suite schema
3. behavior equivalence matrix
4. failure taxonomy
5. support matrix

なお、`repo selection policy` 自体は仕様文書として固定し、
beta では `Fixed cohort + Rolling expansion` を採用する。

つまり、skill を作る仕様だけでなく
`skill をどう測って改善するかの仕様`
も同時に作る。

## 次にプロトタイプ価値が高そうなもの

### 1. Repo intake skill

目的:

- repo を読み、stack, risk, config 問題, UI 候補, modernization 推奨パスを出す

価値:

- 多くの repo に再利用できる
- 後続 skill の前提になる

### 2. JSP / Struts 向け characterization skill

目的:

- route / action / JSP の関係を抽出し、Playwright に載せやすい regression scenario を作る

価値:

- 大きな変換の前提となる safety net を作れる

### 3. Skill factory workflow

目的:

- intake の結果をもとに、repo 固有の prompt, 制約, checklist を生成する

価値:

- 汎用性と repo 依存性の橋渡しになりやすい

## 現時点の推し

もし本当にこの repo を超えて使えるものを作りたいなら、次の形がかなり筋がよさそう。

`repo intake -> characterization plan -> repo-specific skill pack`

再利用するのは「変換 prompt そのもの」ではなく、

- orchestration
- output contract
- quality gate
- playbook の蓄積

に寄せる。

## 次回の論点

この節のうち、次はすでに決着済み。

- deliverable は狭い Java legacy web 向け workflow に固定
- ToBe は Spring Boot + React に固定
- beta は「単一 skill」ではなく workflow として扱う

残る論点は次。

1. 人間レビューをどこまで必須にするか
2. eval harness ownership をどこで切るか
3. behavior / artifact / gate をどこまで code-based grader に落とすか
