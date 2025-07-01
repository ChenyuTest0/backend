<!-- cSpell:ignore sysname Adop PSWAGGER appprops sonarlint Kroki -->

# core-server

## Actsドキュメント

[プロジェクトポータル](https://adp001011.acnshared.com/nexus/repository/specification/acts-portal-site/adoc/html5/ja/project-site/index.html)から参照する。

- Actsの概要については[ACTSスタートアップガイド](https://adp001011.acnshared.com/nexus/repository/specification/acts-dev-guide-adoc/dev/adoc/html5/ja/dev-guide/basic/acts-startup-guide/index.html)を参照
- Actsでの開発をチュートリアル形式で体験するには[トレーニングDX開発ガイド](https://adp001011.acnshared.com/nexus/repository/specification/acts-training-webapi/dev/adoc/html5/ja/training-guide/index.html)を参照

## Project への fork

Git のログは成果物として納品対象になる可能性がある。

shared の Git 履歴については対象外とするためプロジェクト取り込み時は初期化して削除する。

- コミットメッセージにforkを行ったバージョンを記載しておくとバージョンアップ時の参考になる

```bash
rm -rf .git
git init
git add .
git commit -m 'Acts v1.0.0からfork'
```

`gradle.properties` を編集します。

| プロパティ名         | 説明                            |
| -------------------- | ------------------------------- |
| sysname              | リポジトリ名                    |
| sysGroup             | プロジェクトパッケージ名        |
| sysVersion           | 次期リリースバージョン          |
| acn.project.packages | プロジェクトパッケージ名        |
| btsType              | プロジェクトで使用しているBTS名 |

`nexus` から始まっているプロパティ名は以下に変更する。

- Adopの設定値に依存するので問題がある場合はDevOpsCOEに問い合わせる

```properties
nexusHost=https://[プロジェクトADOPのホスト名].acnshared.com
nexusRepositoryPath=nexus/repository
# ライブラリダウンロード用(shared/acts/project - release/snapshotが混ざっている)
nexusMvnPublic=maven-public
# ライブラリダウンロード用(shared/acts/project)
nexusMvnPublicReleases=maven-proxy-shared
nexusMvnPublicSnapshots=acts-snapshots
# ライブラリアップロード用(project)
nexusMvnReleases=maven-releases
nexusMvnSnapshots=maven-snapshots
nexusRawId=specification
nexusArtifactsRawId=archive-artifacts
```

`src/main/java/com/skeleton` `src/test/groovy/com/skeleton` を必要に応じてプロジェクトのパッケージ名に変更する。

- ソースコードを配置したら `package-info.java.template` を `package-info.java` にリネームする
  - Sonarの警告対応でファイル名を変更している

`settings.gradle` の `gradle.properties` を `sysname` と同じにする。

`scanBasePackages` を検索してプロジェクトパッケージ名を追加する。

`src/main/resources/appprops/application.env.yml` を開く。

- `skeleton` で検索してプロジェクトのパッケージ名に変更する。
- DB設定を変更する。

以下のパッケージ名も変更する。

- config/sonar-project.properties
- config/checkstyle-suppressions.xml

`src/docs/asciidoc` にある `specification` 以外のskeleton用ドキュメントを削除する。

- `specification` にはプロジェクト用の詳細設計書を配置する。

[トレーニングDX](https://adp001011.acnshared.com/gitlab/jp_adep_base/acts-training/acts-sample-webapi/-/tree/main/src/docs/openapi/yaml?ref_type=heads)を参照してプロジェクト用にOpenAPIを作成する。

`.vscode/settings.json` の `Asciidoc kroki(PlantUML変換)` 設定を自プロジェクトADOPに変更する。

- 詳細は[AsciiDoc2.0利用ガイド](https://adp001011.acnshared.com/nexus/repository/specification/acts-dev-guide-adoc/dev/adoc/html5/ja/dev-guide/basic/asciidoc2.0-guide/index.html)を参照

```json
  "asciidoc.extensions.enableKroki": true,
  "asciidoc.preview.asciidoctorAttributes": {
    "kroki-server-url": "https://adp001011.acnshared.com.acnshared.com/kroki"
  }
```

Gitlabなどにリポジトリを作成して追加する。

```bash
git remote add origin https://xxxxxxx.acnshared.com/gitlab/xxxxxxxxx.git
```

初期化する。

```bash
./gradlew
```

リポジトリ追加をしていないと以下のエラーが発生する。

```bash
> Task :versionJson FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':versionJson'.
> Cannot invoke method indexOf() on null object
```

VSCodeのコマンドパレットから `Java: Clean Java Language Server Workspace` から再起動する。

プロジェクトをビルドする。

```bash
./gradlew build
```

ビルドが正常に終了したらcommitしてpushする。

DevOpsCOEにpipeline作成を依頼する。

ビルドされSonar解析が完了したら `.vscode/settings.json` のSonar設定を変更する。

- 変更方法については [SonarQubeガイド](https://adp001011.acnshared.com/nexus/repository/specification/acts-dev-guide-adoc/dev/adoc/html5/ja/dev-guide/lint/sonar/index.html)を参照する。

```json
  "sonarlint.connectedMode.project": {
    "connectionId": "ACTS",
    "projectKey": "acts-sample-batch:main"
  },
  "sonarlint.analyzerProperties": {
    "sonar.java.source": "17"
  },
```

## Java21対応

Java21でビルドする場合は `build.gradle` を変更する。

```groovy
java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
  toolchain {
    // `./gradlew -q javaToolchains` を実行すると認識されているJDK一覧が確認できる
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.AMAZON
  }
}
[中略]
bootRun {
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.AMAZON
  }
```

`.java-version` を `21.0` に変更する。

`config/sonar-project.properties` のJavaバージョンを変更する。

```text
# リポジトリ用のJDKバージョン
sonar.java.source=21
```

`.vscode/settings.json` のSonar Javaバージョンを変更する。

```json
  "sonarlint.analyzerProperties": {
    "sonar.java.source": "21"
  },
```

OSのJavaが21なら `.vscode/settings.json` のコメントを解除する。

- この設定自体はWindows版VSCodeのコンソールで使用されるPowerShellの設定となる

<!-- cSpell:disable -->
```json
// Java21の際にPSの文字化けを解消する設定
"terminal.integrated.defaultProfile.windows": "PowerShell",
"terminal.integrated.profiles.windows": {
  "PowerShell": {
    "source": "PowerShell",
    "args": [
      // 変更される場合があるので中略 - 内容はsettings.jsonファイルを参照
    ]
  }
}
```
<!-- cSpell:enable -->

Java21から文字コードがUEF-8に統一されている。

- コメントを解除しない場合、コンソール出力の日本語が文字化けする

Java17以前では、デフォルトの文字セットはJavaランタイムの起動時に決定されていたが変更された。

- macOSではUTF-8が指定されるのに対し、日本語WindowsではShift-JIS(より正確はMS932)で指定されていた
  - このため文字セット指定がない場合、これらの文字セットが利用されていた
- 標準Java APIのデフォルト文字セットがUTF-8に変更された
  - これにより文字セット指定がない場合、コンソールやファイルがUTF-8として扱われるようになった

WindowsでOS Javaとして11/17を使用している場合はコメント解除は不要。

- [Gradleのtoolchain](https://docs.gradle.org/current/userguide/toolchains.html)によりGradleを使用したビルドではJava21が使用される
  - OSにJava21がインストールされていない場合、 `[ユーザホーム]/.gradle` 配下に自動でダウンロードされる
- VSCodeではVSCode自体の設定とGradle設定によりJavaバージョンが決定される
- MacやLinuxの場合、 [jEnv](https://github.com/jenv/jenv)により `.java-version` のバージョンが使用される

## serialVersionUIDが未定義の場合の警告

``Serializable``インタフェースを実装しているクラスは``serialVersionUID``フィールドを持っていないと警告が出る。

```bash
> Task :compileJava
acts-api/src/main/java/com/skeleton/entity/TestEntity.java:5: 警告:[serial] 直列化可能なクラスTestEntityには、serialVersionUIDが定義されていません
public class TestEntity implements Serializable {
       ^
警告1個
```

構造のバージョンを表すフィールドだが、ACTSでは利用しないため警告はコンパイラレベルで抑制している。

プロジェクトにて `serialVersionUID` の警告を出力したい場合は `build.gradle` の `-serial` を除去すれば警告されるようになる。

<!-- cSpell:disable -->
```groovy
tasks.withType(JavaCompile) {
  options.compilerArgs << '-Xlint:all,-serial'
}
```
<!-- cSpell:enable -->

## インハウスリポジトリへのアクセス設定

共通ライブラリをインハウスリポジトリ(Nexus)から取得する必要がある。

インハウスリポジトリには ID/Password の認証が必要になる。

ユーザホームの `.gradle` フォルダに `gradle.properties` を作成して以下の内容を設定する。

```text
nexusUsername=[自分のID]
nexusPassword=[パスワード]
```

認証情報が不正な場合以下のようなエラーが発生する。

```bash
Could not resolve all files for configuration ':compileClasspath'.
> Could not resolve com.accenture.acts:acts-core-server-lib:1.0.4.  Required by:
      project :
   > Could not resolve com.accenture.acts:acts-core-server-lib:1.0.4.
      > Could not get resource 'https://adp001012.acnshared.com/nexus/content/repositories/releases/com/accenture/acts/acts-core-server-lib/1.0.4/acts-core-server-lib-1.0.4.pom'.
         > Could not GET 'https://adp001012.acnshared.com/nexus/content/repositories/releases/com/accenture/acts/acts-core-server-lib/1.0.4/acts-core-server-lib-1.0.4.pom'. Received status code 403 from server: Forbidden
```

- `401` エラーの場合は ID/パスワードが間違っている。
- `403` エラーの場合は認証は OK なのですがインハウスリポジトリへのアクセス権限が無いのでプロジェクト管理者に問い合わせる。

## Gradle コマンド

[asciidoctor](https://asciidoctor.org/)や[graphviz](https://graphviz.org/)がインストールされていてドキュメント生成が可能な環境なら以下のコマンドでドキュメントが生成される。

`build/docs/html5/` 配下に HTML が生成される。

```bash
gradlew clean javadoc generateSwaggerUI generateReDoc redpen asciidoc
```

依存ライブラリのアップデート状況や脆弱性を出力する場合は以下を実行する。

```bash
gradlew clean dependencyUpdates dependencyCheckAnalyze
```

| コマンド               | 説明                                                                                                                           | 備考                                                                               |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------- |
| javadoc                | Javadoc を `build/docs/javadoc` に生成する                                                                                     |                                                                                    |
| generateSwaggerUI      | `build/swagger-yaml/swagger.yaml` から [Swagger UI](https://swagger.io/swagger-ui/) を`build/docs/html5/openapi/ui` に生成する |                                                                                    |
| generateReDoc          | `build/swagger-yaml/swagger.yaml` から [ReDoc](https://rebilly.github.io/ReDoc/) を`build/docs/html5/openapi/re` に生成する    |                                                                                    |
| redpen                 | `src/docs/asciidoc` に[RedPen](http://redpen.cc/)による文書規約の自動精査を実行する                                            |                                                                                    |
| asciidoc               | asciidoc ファイルから HTML と PDF を `build/docs` に生成する                                                                   |                                                                                    |
| dependencyUpdates      | `build/asciidoc/dependencyUpdates/index.adoc` にアップデート可能のライブラリ一覧を生成する                                     |                                                                                    |
| dependencyCheckAnalyze | `build/docs/dependency-check/dependency-check-report.html` に依存ライブラリの脆弱性情報が出力される。                          | 初回はローカルにデータベースをキャッシュするので10分ほど(PCスペックに依存)かる。   |
| messageYaml2Csv        | `src/main/resources/messages` 配下のメッセージ Yaml を CSV に変換して `build/messages/csv` 配下に出力する。                    | `src/main/resources/messages` に配置することでメッセージリソースとして使用出来ます |
| messageCsv2Yaml        | `src/main/resources/messages` 配下のメッセージ CSV を Yaml に変換して `build/messages/yaml` 配下に出力する。                   | `src/main/resources/messages` に配置することでメッセージリソースとして使用出来ます |

## メッセージリソースについて

### CSV/Yaml について

`src/main/resources/messages.properties` の出力内容は `src/main/resources/messages` にあるリソースが元となる。

- CSV/Yaml 形式に対応していて両方のフォーマットが配置されていても問題はない。

### const class について

`src/main/resources/messages` に定義したメッセージのキーと値を const class として自動生成する。

出力先は `build.gradle` の `messageResources2Properties.genConstClass` を変更することで対応可能。

- 不要の場合は `genConstClass` を削除することで出力されなくなる。

```groovy
messageResources2Properties {
  genConstClass 'com.skeleton.messages.MessageConst'
}
```

## Docker Image

### イメージ作成

```shell
make build-local image
```

CIでは以下を実行する。

```shell
make build image
```

作成したイメージを削除するには以下を実行する。

```shell
make rmi
```

### イメージの push

イメージの push にはプライベートリポジトリが必要となる。

- Makefile のログイン部分を aws/azure などに合わせて修正する
- サンプルコードは AWS になる

```shell
make push
```

### Dockerでのローカル起動

[acts-sample-e2e-test](https://adp001011.acnshared.com/gitlab/jp_adep_base/acts-training/acts-sample-e2e-test) を参照

### 作成したイメージに bash でログイン

<!-- cSpell:disable -->
```shell
make run-bash

bash-4.2$ ls -lah
total 34M
drwxr-xr-x 1 nobody nobody 4.0K Oct  7 16:20 .
drwxr-xr-x 1 root   root   4.0K Oct  7 17:07 ..
-rw-r--r-- 1 nobody nobody  34M Oct  7 16:18 service.jar
```
<!-- cSpell:enable -->

### ECSへのデプロイ定義Json

`config/deploy` にて環境別に設定を定義可能にしている。

`./gradlew bootjar` を実行すると環境別ファイルが生成される。

- `build/env-config` に環境別ファイルが生成される
- `build/env-archive` に環境別ファイルをzip化した成果物が生成される

## 複数DBの利用

複数のDBに接続する機能を有している。デフォルトでは単一DBを利用するようになっているので、利用したい場合は以下の手順で機能を有効化する。

①`src/main/resources/appprops/application.env.yml`

<!-- cSpell:disable -->
`spring.datasource`の設定を削除し、`datasource.read-write`と`datasource.read-only`の設定を有効化する。

```yaml
# 以下の各種設定を削除
spring:
  datasource:
    dbcp2:
      ...

# 以下を有効化し、各種設定を行う。
datasource:
  read-write:
    ...

  read-only:
    ...
```
<!-- cSpell:enable -->

②`com.accenture.acts.config.TransactionConfig.txAdvice`

`requiredTx.setReadOnly`を`true`に設定する

```java
public TransactionInterceptor txAdvice(DataSource dataSource) {
    var requiredTx = new RuleBasedTransactionAttribute();

    // 以下をtrueに変更する
    requiredTx.setReadOnly(true);

    List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
    rollbackRules.add(new RollbackRuleAttribute(Exception.class));
    rollbackRules.add(new NoRollbackRuleAttribute(NoRollbackBusinessFailureException.class));
    requiredTx.setRollbackRules(rollbackRules);
    requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    Map<String, TransactionAttribute> txMap = new HashMap<>();
    txMap.put("*", requiredTx);
    var source = new NameMatchTransactionAttributeSource();
    source.setNameMap(txMap);
    var txManager = new DataSourceTransactionManager(dataSource);
    return new TransactionInterceptor((TransactionManager) txManager, source);
}
```

## SNAPSHOTバージョンを含む正規リリース防止対策

ADOPのリリースJobもしくは手動リリース手順にてコマンドを実行すると依存関係に `SNAPSHOT` バージョンを含んでいる場合、エラーとして検出され誤リリースを防ぐことが可能。

- SNAPSHOTバージョンは開発中の不安定バージョンなので正規リリースには含まれてはいけない。

1. Acts coreのバグ対応・新規機能を試すため正規リリース前の `SNAPSHOT` を一時的に使用するように依存関係を修正してCommit/Push
   - もしくはプロジェクトとしてcore-lib(common-lib)のような共通ライブラリをSNAPSHOTで運用している場合
2. `SNAPSHOT` の依存を含んだままリリースJobを実行
3. リリースJobでエラーとして検出され `SNAPSHOT` を使用したままのリリースを防ぐことが可能

```bash
./gradlew checkSnapshotDependencies

* What went wrong:
Execution failed for task ':checkSnapshotDependencies'.
> Release build contains snapshot dependencies:
  acts-core-server-lib-java17-0.9.0-SNAPSHOT.jar

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 6s
```
