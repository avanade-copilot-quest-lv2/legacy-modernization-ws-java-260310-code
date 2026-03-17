# Repo Selection Policy

## 目的

beta 向けの modernization workflow / skill 群を、単一 repo への過学習を避けつつ、現実的な速度で検証・改善できるようにする。

この文書では、検証対象 repo の選び方、保持の仕方、改善ループへの戻し方を定義する。

## 方針

beta では **Fixed cohort 型を基本方針** とする。

- 1 canonical repo
- 2 near-neighbor repo
- 1 adversarial repo

運用上はこれに **Rolling expansion** を重ねる。

- 固定コホートで基本 capability と gate を安定化する
- 新しい failure mode が見つかったら、必要に応じて adversarial か regression 用 repo / task を追加する

つまり、初期の評価母集団は固定し、改善ループの中で失敗ケースだけを意図的に増やす。

## なぜこの方針か

### 採用理由

1. 単一 repo だけでは過学習しやすい
2. いきなり広く取りすぎると beta の精度が出にくい
3. canonical / near-neighbor / adversarial の組み合わせだと、能力確認・一般化確認・失敗探索を分けて観測しやすい
4. skill-creator 的な検証ループとも相性がよい

### 採用しなかった案

#### Single-canonical 型

- 利点: 最速で立ち上がる
- 欠点: repo 非依存性をほぼ検証できない

#### Broad sampling 型

- 利点: 汎用性の把握には強い
- 欠点: beta の制約設計が固まる前にノイズが増えやすい

#### Rolling expansion 単独

- 利点: 実運用には近い
- 欠点: 初期の評価基準や support matrix がぶれやすい

## Repo class 定義

### 1. Canonical repo

最も成功させたい代表例。

条件:

- beta の support matrix の中心に置く stack / 構成である
- ローカル起動可能
- 主要 UI flow を観測できる
- screen / route ベースの migration unit を切り出しやすい

用途:

- 最初の capability を成立させる
- quality gate の初期設計を固める
- benchmark の基準点にする

### 2. Near-neighbor repo

canonical と同系統だが、変動要因を 1 つか 2 つ持つ repo。

例:

- build が Ant ではなく Maven
- JSP / routing / config 配置が少し異なる
- DB access の流儀が異なる
- 認証や画面遷移の複雑さが少し高い

用途:

- root workflow の再利用可能性を確認する
- stack-specific skill の境界を明確にする
- hidden variability を早期に見つける

### 3. Adversarial repo

beta の対応範囲には入るが、難所が目立つ repo。

例:

- config ambiguity が多い
- dead code / commented config が多い
- 既存テストがない
- UI flow が複雑
- 環境構築が不安定

用途:

- failure mode を炙り出す
- escalation gate が妥当かを見る
- 自動化を止めるべき条件を具体化する

## Repo 選定基準

各 repo は次の軸で profile 化して比較する。

- AsIs stack
- build / run 難易度
- UI の観測しやすさ
- DB / integration 複雑度
- auth の複雑度
- migration unit の切りやすさ
- dead code / config ambiguity
- 既存テスト有無

追加で最低限の必須条件を置く。

- AsIs repo がローカルで起動可能
- 主要画面または主要フローをブラウザや API で観測可能
- beta の FROM 範囲に入る

## 運用ルール

### 初期構成

初期の beta コホートは 4 repo を基準とする。

- 1 canonical
- 2 near-neighbor
- 1 adversarial

必要があっても、初期段階でむやみに repo 数を増やさない。

### 追加ルール

新しい repo を追加してよいのは、次のいずれかの場合だけ。

- 既存コホートでは再現できない failure family が出た
- support matrix を広げる意思決定をした
- holdout / regression の信頼性を高める必要がある

### 改善ループへの戻し方

repo ごとの学びは、原則として次の優先順で吸収する。

1. root workflow
2. stack-specific skill
3. eval suite / regression set
4. support matrix
5. 最後の手段として repo-specific workaround

repo 固有対応を安易に増やさない。

## この方針が意味すること

この policy は、「最初から広く対応する」のではなく、
**狭い support matrix に対して、測定可能で改善可能な beta を作る** ことを優先する。

したがって、次に固定すべきものは次の 2 つ。

1. beta deliverable
2. FROM / TO support matrix
