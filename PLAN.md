# Fossify Theming - PLAN

## Ziel

`Fossify Theming` ist eine fokussierte Theme-Zentrale für Fossify-Apps. Das Projekt enthält keine Thank-You-Produktlogik mehr, sondern nur noch Theme-Konfiguration, Material-You-Systemfarben, individuelle Overrides, Profile, Live-Sync, App-Kompatibilitätsstatus und die Übergabe des Themes an Fossify-Apps.

## Anforderungen

- Standalone Application-ID: `org.forfossify.theming`.
- Standalone Provider: `org.forfossify.theming.provider`.
- Stock-Kompatibilitätsmodus für unveränderte Fossify-Apps.
- Dark Mode: Primary, Accent, Background, Text und App Icon jeweils `System` oder `Custom`.
- Standard-Dark-Background: `#0E0E0F`.
- Standard-App-Icon: Schwarz.
- Light Mode bleibt normales Material You.
- Profile lokal speichern/laden/löschen.
- Live-Sync für Wallpaper- und Hell/Dunkel-Änderungen.
- Auf Main sichtbar machen, welche Fossify-Apps Theming-Zugriff haben.
- Keine Donation-, Billing-, Unlock- oder Thank-You-Promo-Funktionen.
- Keine automatische Übernahme beliebiger Upstream-Änderungen.

## Architektur (gewählter Ansatz)

```text
Android Material You / Wallpaper
          |
          v
ThemeSyncManager + ThemeSyncService
          |
          +--> Systemfarben lesen
          +--> lokale Overrides anwenden
          +--> Profile / Live-Sync
          |
          v
Theme Provider
   |                     |
   | standalone          | compat
   v                     v
org.forfossify...   org.fossify.android.provider
                         |
                         v
                 Stock Fossify Apps
```

### Builds

- `standaloneDebug`: saubere eigenständige App.
- `compatDebug`: Testbuild für Stock-Fossify-Kompatibilität.
- `compatPerformance`: R8/minified, resource-shrunk, non-debuggable, temporär mit Android-Debug-Key signiert.

## Dateistruktur

```text
app/src/main/
├── AndroidManifest.xml
├── kotlin/org/fossify/thankyou/
│   ├── activities/
│   ├── contentproviders/
│   ├── extensions/
│   ├── helpers/
│   ├── models/
│   ├── receivers/
│   ├── services/
│   └── ui/
└── res/

app/src/compat/AndroidManifest.xml
.github/workflows/theming-build.yml
performance/
UPSTREAM.md
```

Der Kotlin-Source-Namespace ist derzeit noch der historische Upstream-Namespace. Er enthält keine Thank-You-Produktlogik mehr. Eine spätere reine Namespace-Migration ist möglich, ist aber nicht nötig, um die Android-Produktidentität oder das Theming-Verhalten zu bestimmen.

## Umsetzungsschritte

1. Repository auf `main` als einzige Arbeitsbasis konsolidieren.
2. Alte Fossify/Thank-You-Release-, Store- und Übersetzungsautomatisierung entfernen.
3. Buildvarianten auf `standalone` und `compat` reduzieren.
4. Theme-Sync-Service und Boot-Receiver in das gemeinsame Main-Manifest verschieben.
5. Unbenutzte About-/SimpleActivity-/Legacy-Helfer entfernen.
6. Theme-Dashboard, Profile, Live-Sync und Kompatibilitätsstatus beibehalten.
7. CI nur noch für Fossify-Theming-Builds verwenden.
8. Temporäre Performance-APK weiterhin automatisch aus `main` erzeugen.
9. Upstream-Änderungen nur nach Review und nur bei Theming-Relevanz übernehmen.
10. Später optional Commons-Fork/Integration ausbauen, um die Standalone-ID ohne Compat-Package in allen Ziel-Apps zu unterstützen.

## Offene Fragen/Unklarheiten

- UNKLAR: Wann der historische Kotlin-Source-Namespace ebenfalls auf `org.forfossify.theming` migriert werden soll.
- UNKLAR: Welche Fossify-Apps später zuerst gegen einen eigenen Commons-Fork gebaut werden sollen.
- UNKLAR: Wann der echte Release-Key eingerichtet wird.
