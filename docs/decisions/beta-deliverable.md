# Beta Deliverable

## 結論

beta deliverable は、**固定された FROM / TO 範囲に対して動作する 1 本の modernization workflow** とする。

これは「巨大な単一 skill」ではなく、**複数 skill を orchestration と quality gate で接続した workflow** である。

## Deliverable の形

beta で提供するものは次の組み合わせ。

1. root orchestration
2. 4 つの core skill
3. 中間成果物の schema
4. deterministic quality gate
5. evaluation / benchmark 定義

つまり、deliverable の本体は
**「repo を受け取り、段階的に分析・characterization・方針決定・機能単位 migration・検証まで進める実行可能 workflow」**
である。

## なぜ単一 skill ではないか

巨大な 1 skill にしない理由は次の通り。

1. failure point を切り分けにくい
2. 検証ループを skill 単位で回しにくい
3. 中間成果物を acceptance criteria として再利用しづらい
4. context rot の影響を受けやすい

beta の主目的が「同等挙動を保った modernization」を測定可能にすることである以上、
分割された skill 群を workflow として束ねる方が適している。

## Workflow を構成する core skill

### 1. current-state-analysis

役割:

- stack / config / route / screen / risk を抽出する

主な成果物:

- stack fingerprint
- config inventory
- route / screen / backend mapping
- risk register

### 2. characterization-e2e

役割:

- 現行分析を元に executable な characterization を作る

主な成果物:

- 機能単位 E2E
- 期待挙動の assertion
- 後続 migration の acceptance criteria

### 3. tobe-architecture-proposal

役割:

- 許可された TO 範囲の中で ToBe 方針を決める

主な成果物:

- 推奨 ToBe 案
- ADR
- slice migration plan

### 4. feature-slice-migration

役割:

- 対象 migration unit を段階的に置き換える

主な成果物:

- 実装差分
- 追加 rule / test
- 検証結果

## Orchestration の責務

workflow としての価値は、各 skill を並べることだけではなく、次を統制することにある。

- 実行順序
- 入出力受け渡し
- human review の差し込み点
- escalation 条件
- quality gate 実行
- benchmark / grading の記録

つまり、beta deliverable は skill 群そのものというより、
**skill 群を安全に・測定可能に実行する運用単位** である。

## Done の定義

beta deliverable が成立したとみなす条件は次。

1. 固定コホート repo に対して workflow を最後まで実行できる
2. characterization と migration の成否を段階ごとに評価できる
3. workflow 全体だけでなく skill 単位でも失敗箇所を特定できる
4. benchmark 結果を次の改善ループへ戻せる
5. FROM / TO support matrix の範囲で、同等挙動を目標とした modernization を評価できる

## この決定が意味する次の仕様

この deliverable を成立させるため、次に固定すべきものは次。

1. FROM / TO support matrix
2. behavior equivalence matrix
3. intermediate artifact / evidence schema
4. core skill の I/O schema
5. quality gate spec
6. characterization oracle requirements
7. migration unit / strangler seam definition
8. evaluation program spec
9. eval harness ownership
