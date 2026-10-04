# mapvina-navigation-android: verification 04/10/2026

**Status: SOURCE BUILD BLOCKED.**

Maven navigation-core/core-android/ui-android latest metadata `1.0.0`.

Source baseline: `3de1f3d846efbcfbe17116ae4c5a2e1a4942e443` (`origin/main` plus the sample/doc changes in this PR).

Audit only: no SDK API/version changes, tags, publishing or main merge.

## Navigation source và package public

README sử dụng coordinates public `1.0.0`, không phải upstream pre-release
`5.0.0-pre10`. Có metadata/POM không chứng minh ứng dụng điều hướng chạy được.

Build từ origin/main `3de1f3d` với Maven local bị loại bằng Gradle init script:

```bash
export AUDIT="$(pwd)/docs/verification/2026-10-04/evidence"
./gradlew -I "$AUDIT/public-repositories.gradle" :libandroid-navigation-ui:assembleDebug --console=plain
```

BLOCKED tại `:mapvina-navigation-core:compileDebugKotlinAndroid`:
`MapVinaLocationEngine.kt` không resolve các imports Android/location engine.
Log kèm là lượt chạy public-only, không dùng namespace được cài riêng ở ~/.m2.
Không sửa source engine hoặc dependency SDK trong PR docs.
Android navigation runtime/turn-by-turn: NOT TESTED. Sample iOS còn Pod MapVina,
nhưng trunk trả 404 nên không mô tả nó như cách cài public đã hoạt động.

Checkout gốc có metadata rebase đang dở. Audit dùng checkout riêng từ origin/main,
không abort/chỉnh rebase của người dùng và không merge nhánh migration.

## Evidence and acceptance boundary

- [navigation-android.log](evidence/navigation-android.log)

[Cross-repository artifact and runtime audit](https://github.com/mapvina/mapvina-document-android/blob/codex/docs-verification-20261004/docs/verification/2026-10-04/README.md).

PASS applies only to the named checks. BLOCKED is a reproduced failure; NOT TESTED is not a pass.

## Separate follow-up

[Reproduced blocker issue](https://github.com/mapvina/mapvina-navigation-android/issues/11). No SDK fix or migration merge in this PR.
