# Deterministic E2E Strategy

## 状態

この文書は **レビュー用ドラフト**。

重要仕様のため、内容確認後に確定扱いにする。

## 目的

characterization E2E を
「エージェントがその場でブラウザを触って雰囲気で確認する手段」ではなく、
**再実行可能で決定論的な検証資産**
として定義する。

## 採用する考え方

逆瀬川記事の E2E 戦略から、beta で特に採用する原則は次。

1. 構造化テキスト出力を優先する
2. E2E は生成と実行を分離する
3. エージェント自身を CI の判定器にしない
4. build -> run -> verify -> fix の closed loop を回せる形にする
5. accessibility tree を UI 検証の主インターフェースにする

## 基本方針

### 1. 生成と実行を分離する

- エージェントは E2E を `生成` してよい
- ただし合否は、生成済みの Playwright テストや補助 checker を `決定論的に実行` して判定する
- セッション中の対話的ブラウザ操作は探索と prototyping に使ってよいが、最終判定の source of truth にはしない

### 2. accessibility tree を主、スクリーンショットを従にする

主判定は次を優先する。

- role
- accessible name
- label
- state
- actionability
- visible text の意味

スクリーンショットは次に限定して使う。

- レイアウト崩れ
- 重なり
- 画像 / canvas / chart の視覚異常
- accessibility tree に乗らない視覚要素

### 3. migration unit ごとに deterministic packet を持つ

各 migration unit について、少なくとも次を artifact として持つ。

- UI flow packet
- HTTP packet
- DB side-effect packet
- session / auth packet

E2E はこれらの packet を replay / assert できる形で保存する。

## 決定論化ルール

### 1. Selector rule

- `getByRole` / `getByLabel` / `getByText` を優先する
- 構造依存の CSS selector は原則禁止
- 必要な場合のみ `data-testid` を許容する

### 2. Wait rule

- 固定 sleep を原則禁止
- locator assertion や network assertion による待機を使う
- `networkidle` 依存の雑な完了判定は避ける

### 3. Network rule

- 外部サービス呼び出しは mock / stub / replay を優先する
- 比較対象にしたい API は request / response を capture する
- third-party の実サービス応答を E2E 成否に直接ぶら下げない

### 4. Time rule

- 現在時刻依存の挙動は固定 clock を使う
- 期限、日付表示、タイムアウト、営業時間判定などは frozen time で検証する

### 5. Randomness rule

- 乱数依存の UI / ID / fixture は seed 固定または stub 化する
- 実行ごとに結果が変わるテストデータ生成を禁止する

### 6. Data rule

- テスト前に既知状態を seed する
- テスト後に state leak を残さない
- 並列実行時も衝突しない fixture を使う

### 7. Session rule

- test ごとに独立 context を使う
- login は helper または storage state で安定化する
- 前テストの cookie / local storage を前提にしない

### 8. Retry rule

- retry は flaky test を隠すためでなく診断用に使う
- retry 成功でも flaky として記録する
- retry 前提で green を合格扱いしない

### 9. Failure evidence rule

- 失敗時は trace / screenshot / accessibility snapshot / console / network log を残す
- 人間レビューは evidence を見て allowed difference か regression かを判定する

## beta における推奨運用

### 探索フェーズ

- 対話的ブラウザ操作を使って route / screen / form / risk を発見する
- ここでは Playwright CLI 的な操作や browser automation を使ってよい

### 生成フェーズ

- migration unit ごとに characterization E2E を Playwright テストとして生成する
- assertion は accessibility-first で記述する

### 実行フェーズ

- CI またはローカル同等環境で headless 実行する
- 合否は test result と packet comparator で判定する

## beta で明示的に避けるもの

- エージェントとの対話結果だけで「テストできた」とみなすこと
- screenshot の目視比較を主判定にすること
- 本番外部サービスに依存した不安定な E2E
- sleep ベースの同期
- 共有アカウント / 共有 state に依存したテスト

## behavior equivalence との関係

behavior equivalence は「何が一致すべきか」を決める。

この文書は「それをどう決定論的に観測するか」を決める。

したがって、後続仕様では次の分離を維持する。

- `behavior-equivalence.md`
  - comparison target
- `e2e-determinism.md`
  - observation method
- `artifact-schema.md`
  - saved evidence format
- `characterization-oracle.md`
  - oracle quality threshold

## References

- Reverse Segawa, "Harness Engineering Best Practices 2026", section 5
- Anthropic, "Effective harnesses for long-running agents"
- Playwright docs: locators, network mocking, auth/storage state, retries, trace viewer
