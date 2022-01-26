![Minecraft](https://img.shields.io/badge/minecraft-1.18.1-brightgreen)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Futen2c.github.io%2Frepo%2Fdev%2Futen2c%2Fstrobo%2Fmaven-metadata.xml)

# Strobo

内部で使ってるサーバーサイドFabricのいろいろライブラリ  
基本的に最新バージョンでしか開発したくないので最新バージョン以外はサポートする気はない

## Feature

- イベントリスナー, 多少のイベント
- BrigadierのKotlin DSL
- サーバーサイドに最適化されたアイテムの実装
- サーバーサイドに最適化されたスクリーンハンドラーの実装
- タスクスケジューラー
- ユーティリティ

## Gradle

```kotlin
repositories {
    // ...
    maven("https://uten2c.github.io/repo")
}
```

```kotlin
dependencies {
    // ...
    modImplementation("dev.uten2c:strobo:VERSION")
    // include("dev.uten2c:strobo:VERSION")
}
```

## Styling

```
./gradlew ktlintApplyToIdea addKtlintFormatGitPreCommitHook
```
