# Performance APK

Dieser Ordner ist für temporäre, optimierte Test-Builds von Fossify Theming gedacht.

## Aktueller Test-Build

`Fossify-Theming-compat-performance-temporary-signed.apk`

Build-Typ: `compatPerformance`

- basiert auf dem Stock-Fossify-Kompatibilitätsmodus (`org.fossify.thankyou` + `org.fossify.android.provider`)
- R8/Minify aktiviert
- Resource Shrinking aktiviert
- nicht debuggable
- aktuell nur mit dem temporären Android-Debug-Key signiert
- **nicht als Release-Signatur verwenden**

Der echte Release-Key wird später separat eingerichtet. Bis dahin sind diese APKs ausschließlich Test-/Performance-Builds.

Die CI erzeugt den Build mit:

```bash
./gradlew assembleCompatPerformance
```

Bei erfolgreichen Push-Builds auf `main` wird die temporäre Performance-APK automatisch in diesen Ordner kopiert. GitHub Actions veröffentlicht denselben Build zusätzlich als Artefakt `fossify-theming-performance-apk`.
