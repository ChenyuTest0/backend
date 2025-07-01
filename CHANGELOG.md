# ACTS Core Server Skeleton for Java17 Changelog

最新化しても解消されないCVE(脆弱性)については[こちら](src/docs/asciidoc/library-cve/index.adoc)を参照。

## 1.2.4

* `com.accenture.acts` パッケージ配下のソースコード修正
  * みなし日/DBヘルスチェックのリポジトリクラスの配置
    * `DeemedDateRepository` と `DatabaseHealthCheckRepository` を配置
  * `MyBatisMapperScannerConfig` 内の型宣言時にvarによる型推論を使用するように修正
  * `ErrorResponseBody`におけるjavadocのインデントを修正
* 依存関係の定義を修正
  * `additional.gradle` でも使用されるケースがあるので `groovy-all` と `acts-spock-lib` を共通化
  * `acts-core-server-lib-java17` の定義を `core.gradle` から `additional.gradle` に変更
    * プロジェクト共通ライブラリを使用するとそちらから伝播されるので `additional.gradle` で定義するよう変更
    * プロジェクト共通ライブラリを使用する場合は `additional.gradle` の `acts-core-server-lib-java17` の定義はコメントアウトする必要がある
* SNAPSHOTのライブラリを含んでいる場合、エラー検知するGradleプラグインを追加
* Updating the dependent libraries / 依存ライブラリの最新化

## 1.2.3

* v1.2.0としてリリースした `推奨されるClassPathMapperScanner(BeanDefinitionRegistry, Environment)に修正` のバグ修正
  * `ApplicationContext` 経由で `ClassPathMapperScanner` に `Environment` を引き渡すのではなく、`EnvironmentAware` を Implement して `Environment` を引き渡すように修正

## 1.2.2

* Updating the dependent libraries / 依存ライブラリの最新化
* DBのReader/Writerを分離する機能を追加
* 環境変数のログマスキング機能を追加
  * 詳細については [Acts APIガイド](https://adp001011.acnshared.com/nexus/repository/specification/acts-dev-guide-adoc/dev/adoc/html5/ja/dev-guide/api/java-acts/index.html#masking-environment-variables)を参照

## 1.2.1

* checkstyleConfig.versionErrorの設定をtrueに変更
* Updating the dependent libraries / 依存ライブラリの最新化

## v1.2.0

* `com.accenture.acts:acts-core-server-lib-java17:0.7.2` へのバージョンアップ
* 日時チェックアノテーションのメッセージ追加
* Graceful shutdownの削除とSpring用の設定を追加
* OpenAPI定義の最新化
  * VSCodeプラグインでのプレビュー対応
  * catalog.yamlの廃止
* Updating the dependent libraries / 依存ライブラリの最新化
  * mybatis-spring-boot-starterを3.0.4にアップデートしたことによりClassPathMapperScannerのClassPathMapperScanner(BeanDefinitionRegistry)が非推奨となったため、推奨されるClassPathMapperScanner(BeanDefinitionRegistry, Environment)に修正([ClassPathMapperScanner (mybatis-spring 3.0.4 API)](https://mybatis.org/spring/ja/apidocs/org/mybatis/spring/mapper/ClassPathMapperScanner.html))
    * 修正にバグがありv1.2.3にて修正

## v1.1.4

* Redpenのルール見直し(ja/en版の齟齬解消)
  * DuplicatedSection
    * en版から削除
  * FrequentSentenceStart
    * en版から削除
  * ParagraphNumber
    * en版から削除
  * Spelling
    * en版から削除
    * Cspellによる単語チェックが入っているので不用
      * 辞書の二重管理となる
  * SentenceLength
    * 120 => 200に変更
* Updating the dependent libraries / 依存ライブラリの最新化

## v1.1.3

<!-- cSpell:disable-next-line -->
* Bootrun時のJavaバージョン指定を追加
<!-- cSpell:disable-next-line -->
* com.github.hierynomus.licenseプラグインの変更
* Updating the dependent libraries / 依存ライブラリの最新化

## v1.1.2

* Java21 のとき、 Windows VSCode の Powershell での文字化けを回避する設定を `.vscode/settings.json` にコメントで追加
  * 文字化けが発生した際はコメントアウトを解除すること

## v1.1.1

* Updating the dependent libraries / 依存ライブラリの最新化

## v1.1.0

* Updating the dependent libraries / 依存ライブラリの最新化

および、Acts自動生成にあわせて構成を大幅変更。

* OpenAPIの削除
* com.accenture.skeleton.test.common.BeanValidatorの削除
* boot起動時にSpring FlameWorkで[警告ログ](https://github.com/spring-projects/spring-framework/issues/30575)が出力されるので対処
<!-- cSpell:disable-next-line -->
* Spotbugs `findsecbugs-plugin:1.13.0` で `BEAN_PROPERTY_INJECTION` が検知される問題の対応
  * 詳細については [Acts APIガイド](https://adp001011.acnshared.com/nexus/repository/specification/acts-dev-guide-adoc/dev/adoc/html5/ja/dev-guide/api/java-acts/index.html#_spotbugs%E3%81%AEbean_property_injection%E3%82%A8%E3%83%A9%E3%83%BC%E3%81%AB%E3%81%A4%E3%81%84%E3%81%A6)を参照
* Sonarqube最新版の指摘対応として Field Injection から Constructor Injection に変更
  * Springコミュニティとしても Field Injection は非推奨とされているため

## v1.0.13

* Updating the dependent libraries / 依存ライブラリの最新化
* Spring6.1以上でDI時にパラメータ名解決をする設定を追加
  * VSCodeのデバック起動失敗も `eclipse-setup:1.0.1` で対応済み

## v1.0.12

* Updating the dependent libraries / 依存ライブラリの最新化

## v1.0.11

* Updating the dependent libraries / 依存ライブラリの最新化

## v1.0.10

* Updating the dependent libraries / 依存ライブラリの最新化

## v1.0.9

* メッセージ周りの修正
  * org.hibernate.validator.constraints.NotBlank は非推奨
    * javax.validation.constraints.NotBlank を使用する必要がある
  * javax.validation.constraintsの共通メッセージを追加

## v1.0.8

* Updating the dependent libraries / 依存ライブラリの最新化

## v1.0.7

* Actsプラグインの最新化
* Gradle8.3対応
* 依存ライブラリの最新化
* Databaseのデフォルトをpostgresqlに変更

## v1.0.6

* application-env-yamlプラグインのアップデート対応
  * `gradle.properties` から設定を削除して `application.env.yml` で設定可能に変更
* actsパッケージのユニットテストの追加/対象外設定
* ユニットテスト(groovy)のlint(CodeNarc)を追加
* 依存ライブラリの最新化

## v1.0.5

* 依存ライブラリの最新化

## v1.0.4

* AsciiDoc2対応

## v1.0.3

* 依存ライブラリの最新化
* Spring のバージョンアップにより、ユニットテストのログレベルがINFOになってしまったため、明示的にDEBUGに変更する logback.xml を追加

## v1.0.2

* 依存ライブラリの最新化

## v1.0.1

* 依存ライブラリの最新化

## v1.0.0

* Spring 2.7.x から 3.0.x への移行については src/docs/asciidoc/skeleton-update-guide/index.adoc を参照してください。

## v0.1.0

* acts-core-server-skeleton-java11のfeature/spring3ブランチから派生。
  * 上記ブランチで以下を実施しSpring Boot 3.0に対応。
    * Javaのバージョンを17に変更。
    * 各ライブラリをSpring Boot 3.0に対応したバージョンに変更。
    * SpockのGroovyバージョンを4.0に変更。
    * Jakarta EE9の変更に伴いjavax.*からjakarta.*にパッケージ名を変更。
