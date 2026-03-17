# 調査メモ: レガシーアプリ向け Agentic Workflow

## このリポジトリをどう位置づけるか

このリポジトリは題材であって、最終目的ではない。

目的は「この repo を変換すること」ではなく、「レガシーアプリのモダナイゼーションタスクを repo 非依存で実行できる Agentic Workflow を設計すること」。

その上で、この repo は次の意味で良いケーススタディになっている。

- `build.xml` による Java 5 + Ant の古いビルド
- `src/main/webapp/WEB-INF/web.xml` と `src/main/webapp/WEB-INF/struts-config.xml` による Struts 1 系の構成
- `src/main/resources/hibernate.cfg.xml` と `*.hbm.xml` による Hibernate 3 系の XML マッピング
- JSP ベースの画面が一定数そろっている

つまり、汎用化に必要な「繰り返し現れるレガシーパターン」と、「repo ごとの泥臭さ」の両方が含まれている。

## この repo から得られる、workflow 設計上の示唆

### 1. 最初に必要なのは変換能力ではなく intake 能力

この repo を少し見るだけでも、実行前に把握すべきことが多い。

- Java 1.5 前提
- Ant ビルド
- `lib/HIBERNATE_LIBRARIES.md` はあるが JAR は同梱されていない
- 現環境では `ant clean build` が `ant: command not found` で失敗

示唆:

- モダナイゼーション workflow には、コード変換の前に必ず `environment readiness gate` が必要
- 「解析可能か」「ビルド可能か」「依存関係がそろっているか」を先に判定しないと危ない

### 2. config inventory は必須成果物

この repo にはハードコードされた値が多い。

`src/main/webapp/WEB-INF/web.xml`

- `/home/dev1/uploads`
- `/opt/bookstore/tmp`
- `/home/dev1/bookstore/config/app.properties`

`src/main/resources/hibernate.cfg.xml`

- JDBC URL
- DB ユーザー/パスワード
- 相反する pool 設定
- `hbm2ddl.auto=update`

示唆:

- 汎用 workflow は「設定が外出しされている」前提を置けない
- intake の成果物として `config inventory` を標準化すべき

### 3. live config と dead/ambiguous config を分けて扱う必要がある

`web.xml` と `struts-config.xml` には、コメントアウトされた設定、ロールバック痕跡、重複した意図、途中で止まった移行が多い。

例:

- 複数の encoding filter 定義
- 無効化された auth/session/security 設定
- 旧 URL マッピングの残骸
- 互換のため残された form-bean 定義
- migration note つきの forward / action 設定

示唆:

- config をそのまま唯一の真実として扱うのは危険
- workflow には少なくとも以下の分類が必要
  - live
  - dead
  - ambiguous

### 4. characterization は optional ではない

- `src/test/**/*.java` にはテストがない
- 一方で JSP 画面は比較的追いやすい構造になっている

示唆:

- モダナイゼーション前に、E2E や black-box regression の足場を作る工程が必要
- 特に UI を持つレガシー Web アプリでは、Playwright ベースの characterization は再利用性が高そう

### 5. 汎用化の単位は「変換」より「抽出すべき事実」

この repo にはわかりやすい層構造がある。

- `action/`
- `form/`
- `model/` + `.hbm.xml`
- `dao/`, `dao/impl/`
- `WEB-INF/jsp/bookstore/` 配下の画面

示唆:

- 汎用 workflow は「何を抽出すべきか」を標準化しやすい
- ただし変換ロジック自体は stack-aware であるべき

## 外部リサーチから見える共通パターン

最近の agentic modernization の論調を見ると、共通して次が強い。

1. 1 つの万能 agent より、役割分担した複数 agent のほうが現実的
2. discovery, dependency/risk mapping, test generation, transformation, validation を分ける
3. 高リスクな設計判断や大きな変換には人間の承認を残す
4. 成功した playbook を蓄積し、次の repo で再利用する

参考:

- IBM: https://www.ibm.com/think/insights/reimagining-application-modernization-migration-agentic
- Microsoft Azure blog: https://devblogs.microsoft.com/all-things-azure/the-realities-of-application-modernization-with-agentic-ai-early-2026/
- Capgemini: https://www.capgemini.com/insights/expert-perspectives/legacy-applications-revived-by-agentic-ai/
- AWS partner example: https://forgeahead.io/how-aws-partners-are-using-agentic-ai-to-accelerate-migration-and-legacy-modernization/
- LegacyLeap overview: https://www.legacyleap.ai/blog/agentic-ai-application-modernization/

## 現時点の研究仮説

再利用単位は「1 個の modernization skill」ではない可能性が高い。

むしろ本命は次の組み合わせ。

- 汎用の intake + governance workflow
- intake 結果をもとに選択/生成される repo 固有 skill pack

つまり最初に設計すべきものは、広い変換能力そのものではなく、以下を判断できる intake モデルかもしれない。

- その repo の AsIs stack は何か
- リスクはどこにあるか
- characterization はどの方式が向いているか
- 許可される変換パスは何か
- どこで自動化を止めて人に返すべきか

## Harness Engineering 記事から得られる指針

対象記事:

- https://nyosegawa.github.io/posts/harness-engineering-best-practices-2026/

あわせて確認した主な参照先:

- Mitchell Hashimoto: https://mitchellh.com/writing/my-ai-adoption-journey
- Martin Fowler / Birgitta Böckeler: https://martinfowler.com/articles/exploring-gen-ai/harness-engineering.html
- HumanLayer: https://www.humanlayer.dev/blog/writing-a-good-claude-md
- Factory.ai: https://factory.ai/news/using-linters-to-direct-agents
- Claude Code Hooks: https://code.claude.com/docs/en/hooks-guide
- ADR: https://adr.github.io/
- ast-grep: https://ast-grep.github.io/
- Lefthook + Claude Code: https://liambx.com/blog/ai-agent-lint-enforcement-lefthook-claude-code
- Context rot 解説: https://www.morphllm.com/context-rot

注記:

- OpenAI の Harness Engineering 本文は 403 で直接取得できなかったため、逆瀬川記事と Fowler 記事経由で補完した

### 1. 一番大事なのは「賢い agent」より「失敗しにくいハーネス」

記事全体を通じて一貫しているのは、モデル単体の性能よりも、周辺の仕組みの方が品質を左右するという点。

今回のテーマに引き直すと、repo 非依存の modernization workflow を作るうえで本当に再利用したいのは「変換プロンプト」ではなく、次のようなハーネス側の仕組み。

- intake のやり方
- context の絞り方
- テスト/リント/検証の自動フィードバック
- アーキテクチャ制約の強制
- 完了判定の gate

これは、こちらが考えている `現行分析 -> E2E 化 -> ToBe 提案 -> 実装` の流れとも整合する。

### 2. beta は狭くした方がよい

Fowler の整理でも、AI の信頼性を上げるには solution space を広げるのではなく制約する必要があるとされている。

この観点では、今の方針:

- 汎用性は欲しいが、beta は精度優先
- AsIs repo はローカルで起動可能であることを前提にする
- ベータでは AsIs と同等挙動の ToBe を最優先にする
- 機能追加や機能改善はスコープ外

はかなり筋が良い。

現時点の研究観点からは、beta では次のようにさらに絞るのが良さそう。

- AsIs は server-rendered Web アプリに限定
- UI をブラウザ経由で観測可能であることを必須にする
- ToBe も 1 つか 2 つの target architecture に固定する
- 「性能改善」は明示的に非目標にする

### 3. characterization は中核機能であり、周辺機能ではない

Mitchell Hashimoto の「agent に検証手段を与えると、自分でかなり直せる」という観点と、逆瀬川記事の「テストはドキュメントより腐敗に強い」という観点は、今回の構想に強く効く。

つまり beta では、実装スキルより先に次の 2 つを強くすべき。

- 現行分析スキル
- 現行分析結果を使って characterization E2E を書くスキル

理由:

- モダナイゼーションの価値は「新しく作ること」より「壊さずに置き換えること」にある
- 同等挙動がマストなら、E2E は単なる QA ではなく仕様抽出装置になる
- repo 非依存化の観点でも、変換ロジックより characterization の方が再利用しやすい

### 4. docs より executable artifact を優先すべき

逆瀬川記事では、腐敗しやすい説明文書よりも、実行可能な artifact を repo に置くことが強く推奨されている。

今回の workflow に引き直すと、残す価値が高いものは次。

- テスト
- lint / structural rule
- schema / type / config inventory
- ADR

一方で、AsIs の構造説明を長文 prose で大量に残すのは危険。

したがって `docs/exploration/research.md` や `docs/exploration/idea.md` に書く内容も、最終的には次へ落とし込めるものに寄せるべき。

- workflow の gate 定義
- intake で抽出すべき項目
- skill ごとの入出力契約
- 実行可能な rule / test への変換方針

### 5. AGENTS.md / CLAUDE.md は短く、普遍的に

HumanLayer の主張で重要なのは、常時読み込まれる指示ファイルは短く、普遍的であるべきという点。

今回に当てはめると、repo 非依存 workflow の root 指示には以下だけを置くのがよさそう。

- 目的
- 絶対に守る quality bar
- 進め方の骨格
- どの補助ドキュメントを必要に応じて読むべきか

逆に、stack ごとの細かい変換ルールまで root の指示に埋め込むと、context rot を起こしやすい。

つまり、workflow 設計としては

- root の普遍ルール
- stack 別の補助ルール
- repo intake 後に読む repo 固有メモ

の progressive disclosure 構造がよい。

### 6. hooks と deterministic tool を使って「毎回守らせる」

逆瀬川記事と Claude Code Hooks ドキュメントが一致しているポイントは、

- prompt で「実行してね」と頼む
- 実際に hook で必ず実行する

の間には大きな差がある、ということ。

今回の workflow に必要そうな hook/gate パターン:

- PreToolUse
  - 破壊的コマンドの禁止
  - 設定ファイルや guardrail の保護
- PostToolUse
  - 編集後の軽量チェック
  - 失敗内容を additionalContext として返す
- Stop / TaskCompleted
  - 完了宣言時の E2E / build / lint 実行
  - 通らなければ完了させない

beta の quality を考えると、「実装スキル」より「完了させない仕組み」の方が重要かもしれない。

### 7. custom lint / structural rule は modernization にも効く

Factory.ai 記事の価値は、lint を style check ではなく executable spec と捉えている点にある。

これは modernization にそのまま使える。

例えば migration rule として次のようなものが考えられる。

- 旧 API / 旧 framework 呼び出しの禁止
- 旧ディレクトリ構造への新規追加禁止
- 新規コードは target architecture の layer にのみ配置
- input validation, auth, logging の必須化
- 生成コードにありがちな anti-pattern の禁止

特に重要なのは、lint message 自体に修正方針を書くという考え方。

これは `実装スキル` の品質を上げるための、repo 非依存な再利用ポイントになる。

### 8. 「古いやり方を禁止する lint」は migration engine になる

Factory.ai の指摘どおり、lint は modernization の実行エンジンにもなる。

つまり、単に新コードの質を守るだけでなく

- old way を検出する
- new way を要求する
- autofix / codemod / agent 実装につなぐ
- regress を防ぐ

というサイクルを回せる。

この観点は、`バージョンアップは確実にやる` という要件と相性が良い。

例えば beta では

- deprecated API の禁止
- 脆弱な実装パターンの禁止
- target stack での標準実装への置換

を migration rule として定義する形が考えられる。

### 9. context rot を前提に、セッションを短く・狭くするべき

context rot の知見は、今回かなり重要。

特に coding agent においては、

- 検索結果が増えるほど性能が落ちる
- 長いタスクほど成功率が落ちる
- 35 分付近で顕著に悪化する

とされている。

これを受けると、beta の workflow は「大きな近代化を一気にやる」のではなく、明示的に小さく分割された feature 単位で進めるべき。

これはあなたが書いていた

- E2E は機能単位で実行
- 実装も機能単位で実行

という考えと一致する。

### 10. ADR は ToBe 決定の保全に向いている

ADR の価値は、なぜその ToBe を選んだかを不変の形で残せることにある。

今回で言えば、ToBe architecture を決める skill は、単に提案文を出すだけでなく、最終的には次を残せるとよい。

- なぜその ToBe を選んだか
- 何を捨てたか
- どんな trade-off があるか
- どの migration slice から始めるか

この決定履歴は、後続の実装 skill にとって重要な context になる。

## beta 版 workflow に落としたときの提案

あなたの現時点の考えに対して、かなり賛成。

とくに良いと感じる点:

- beta では汎用性より精度を優先してよい
- AsIs がローカルで動く前提を置く
- E2E を characterization の中核に置く
- ToBe は提案型でもよい
- 実装は機能単位で進める
- まずは同等挙動の ToBe を作ることをゴールにする
- 機能追加/改善をスコープ外に置く

現時点の提案を、さらに workflow として明文化すると次の 4 skill が核になる。

### 1. 現行分析 skill

入力:

- repo
- 実行手順
- 起動済み AsIs 環境

出力:

- stack fingerprint
- route / screen / backend entrypoint / data flow の対応表
- config inventory
- risk register
- characterization 対象候補

### 2. characterization E2E skill

入力:

- 現行分析結果
- 稼働中 AsIs

出力:

- 機能単位の E2E
- 期待挙動メモ
- 後続 migration の acceptance criteria

### 3. ToBe 提案 skill

入力:

- 現行分析結果
- 非機能要件
- 対応可能な target architecture の候補

出力:

- ToBe 候補
- 推奨案
- ADR 形式の決定メモ
- migration slice 案

### 4. 機能単位 migration 実装 skill

入力:

- characterization E2E
- ToBe 方針
- 対象機能 slice

出力:

- 実装差分
- 必要な rule / test の追加
- 検証結果

## beta 版のスコープ案

最初の beta は、かなり絞ってよい。

### In scope

- 既存機能の同等挙動再現
- バージョンアップ
- 明確なセキュリティ issue の解消
- 変換後の build / test / E2E が通る状態

### Out of scope

- 機能追加
- UX 改善
- 性能改善を主目的にした最適化
- 大規模なドメイン再設計

### 条件付きで入れてよいもの

- 低リスクな性能改善
- セキュリティ issue 解消に伴う最小限の構造改善

## 現時点の結論

repo 非依存の modernization workflow を本当に作りたいなら、beta で狙うべきは

- どんな repo でも変換できること

ではなく、

- 対象を絞った repo に対して
- 現行分析と characterization を確実に行い
- ToBe を制約付きで決め
- 機能単位で同等挙動の migration を進められること

だと思う。

つまり、beta で一番価値が高いのは `万能な変換 skill` ではなく、

- characterization 中心の workflow
- 厳しい gate
- 制約された target architecture
- 変換を支える executable rule

のセットである。

## 設計監査: いまの文書の矛盾と不足

ここまでの `docs/exploration/idea.md` と `docs/exploration/research.md` は方向性としては良いが、まだ実行可能な仕様にはなっていない。

### 1. いちばん大きい矛盾

目標としては `repo 非依存の modernization workflow` を置いている一方で、設計は

- stack 固有 skill pack
- AsIs / ToBe 制約
- repo ごとの characterization

に強く依存している。

したがって、ここでいう `repo 非依存` は「同じ orchestration と検証ハーネスを再利用する」という意味に限定して定義し直す必要がある。

つまり、再利用対象は

- orchestration
- eval harness
- quality gate
- skill contracts

であり、変換ロジックそのものは stack-aware / repo-aware である、と明文化すべき。

### 2. beta の形がまだ決まっていない

この節は当時の論点整理であり、**現在は解消済み**。

現時点の決定:

- beta は固定された FROM / TO に対する modernization workflow
- 単一 skill ではなく、複数 core skill + orchestration + quality gate
- `skill-creator` については、詳細統合ではなく `eval harness ownership` を先に決める

したがって、ここでの論点は
`beta の形の再検討` ではなく、`workflow をどう deterministic に測定するか`
へ移っている。

### 3. skill の I/O contract がない

4 つの core skill は整理できているが、入出力スキーマがない。

たとえば未定義なのは次。

- repo source は local path か git URL か
- 起動済み AsIs 環境の表現は何か
- stack fingerprint の JSON schema は何か
- route / screen / config inventory の schema は何か
- ToBe 提案の出力は自然言語だけか、ADR JSON も持つのか

このままだと grader も後続 skill も作れない。

### 4. 「同等挙動」の定義がない

beta の中心要件は `AsIs と同等挙動の ToBe` だが、同等の意味がまだ曖昧。

最低でも次は定義が必要。

- HTTP status
- 画面遷移
- 主要 DOM 要素
- セッション挙動
- DB side effect
- 外部 I/O
- エラーメッセージ

何を `must match` とし、何を `allowed difference` とするかが要る。

### 5. quality gate がまだ aspiration 止まり

quality gate の方向性は良いが、pass/fail 条件が未定義。

各 gate について少なくとも

- checker
- 入力
- pass 条件
- fail 時の挙動
- timeout

を決める必要がある。

### 6. characterization oracle 自体の品質条件がない

characterization E2E を oracle として重視している一方で、

- 何本あれば最低限か
- どの程度 flaky なら失格か
- assertion は誰が書くか
- oracle 自体を誰が検証するか

が決まっていない。

これはかなり重要で、oracle が弱いと migration skill 全体が崩れる。

### 7. migration unit の定義がない

`機能単位` で進める方針は妥当だが、

- feature の粒度
- 依存関係
- slice の順序
- 1 slice あたりの変更上限

がないので、エージェントが恣意的に広げてしまう危険がある。

現在はこの論点を、より具体的な
`migration unit / strangler seam / change budget`
の定義問題として扱う。

## 追加リサーチから得た、仕様化に使える具体策

### 1. Mechanical Orchard の closed-loop validation はかなり参考になる

https://www.mechanical-orchard.com/insights/verify-then-trust-a-closed-loop-method-for-ai-powered-legacy-modernization

使えそうな考え方:

- まず replication を優先する
- 本番または実運用相当の入出力を capture する
- それを replay 可能な packet にする
- 生成コードを packet に対して回し、差分を feedback として次の試行に戻す
- 全 packet が green になるまで convergence させる

今回の beta 向けには、これを簡略化して

- UI flow packet
- HTTP request/response packet
- DB side effect packet

のような形で migration unit ごとに持つのがよさそう。

### 2. Strangler Fig は ToBe rollout の default 候補

https://learn.microsoft.com/en-us/azure/architecture/patterns/strangler-fig

ToBe の rollout 戦略としては、少なくとも beta では big bang よりこちらが自然。

使えるポイント:

- façade / proxy で切り替える
- feature ごとに徐々に新実装へ寄せる
- 旧/新の並行稼働を前提に比較しやすい
- shadow write / dual read 的な考え方を DB にも適用できる

つまり ToBe skill は、単に target architecture を提案するだけでなく、
`どの seam で strangler するか` まで出せると価値が高い。

### 3. eval は task / trial / grader / transcript / outcome で分けるべき

https://www.anthropic.com/engineering/demystifying-evals-for-ai-agents

この整理は今回ほぼそのまま使える。

- task
  - 1 つの migration unit migration
- trial
  - その skill 実行の 1 回
- grader
  - code-based / model-based / human
- transcript
  - skill 実行履歴
- outcome
  - 実際に生成された code, test, runtime state

また regression eval と capability eval を分けるべきという指摘も重要。

今回なら

- capability eval
  - まだ苦手な migration task を登る
- regression eval
  - 一度通った migration unit を継続的に守る

に分けるべき。

### 4. NIST 的には、accuracy 以外の観点も整理しておくべき

https://www.nist.gov/ai-test-evaluation-validation-and-verification-tevv

AI evaluation の観点として

- accuracy
- reliability
- robustness
- security
- explainability / transparency

などがある。

今回の beta では全部を深追いしないにせよ、少なくとも

- behavioral accuracy
- reliability / flakiness
- security
- traceability

は指標に入れるべき。

## ここから仕様化するための優先論点

次に決めるべき論点を優先順に並べる。

1. behavior equivalence spec
2. intermediate artifact / evidence schema
3. 4 core skill の I/O schema
4. quality gate spec
5. characterization oracle の品質条件
6. migration unit / strangler seam definition
7. repo profile schema
8. evaluation program spec
9. eval harness ownership
10. failure taxonomy

## 現時点の率直な評価

あなたの見立てどおり、まだ実行可能な状態までは遠い。

ただし、方向性が悪いというより

- scope の固定
- schema の定義
- gate の定義
- eval harness への写像

がまだ足りていない、という状態に近い。

なので次のフェーズでは、発散的なアイデア出しより
`仕様化` に寄せたドキュメントを増やすべき。
