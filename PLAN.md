# Fossify Theming - PLAN

## Ziel

`Fossify Theming` ist eine eigenständige Theme-App für die Fossify-Suite.

Sie ist technisch und funktional nicht mehr die Thank-You-App, sondern verwaltet ausschließlich Theme-Farben, Material-You-Systemfarben, lokale Overrides, Profile und Live-Sync.

Die App kann parallel zum offiziellen Fossify Thank You installiert werden.

## Anforderungen

- Android Application-ID: `org.forfossify.theming`.
- Sichtbarer Name: `Fossify Theming`.
- Eigener Theme-Provider: `org.forfossify.theming.provider`.
- Keine Purchase-, Donation-, Unlock- oder Thank-You-Funktionalität.
- Android 12+ nutzt die echten dynamischen Material-You-Farben.
- Android 8 bis 11 erhält einen statischen Material-3-Fallback.
- Dark Mode: Primary, Accent, Background, Text und App Icon jeweils `System` oder `Custom`.
- Standard-Dark-Background: `#0E0E0F`.
- Standard-App-Icon: Schwarz.
- Light Mode: vollständig normales Material You, keine Dark-Mode-Overrides.
- Live Sync reagiert auf Wallpaper- und Dark/Light-Änderungen.
- Profile können lokal gespeichert, geladen und gelöscht werden.
- Die installierten Fossify-Apps bleiben unter dem Theme-Dashboard als Zielübersicht sichtbar.
- Keine Netzwerkverbindung, keine Cloud-Kosten und keine Speicherung des Wallpaper-Bildes.

## Architektur (gewählter Ansatz)

### Standalone Theme Controller

Die App besitzt eine eigene Android-Identität und einen eigenen Provider.

```text
Pixel Wallpaper / Material You
        |
        v
ThemeSyncService
        |
        +--> dynamische Systemfarben lesen
        |
        +--> ThemeSettings laden
        |      System / Custom pro Farbrolle
        |
        +--> Dark Mode?
               |
               +-- Nein --> SYSTEM THEME
               |
               +-- Ja ----> Systemfarben + Custom Overrides
                              |
                              v
            org.forfossify.theming.provider
                              |
                              v
              Global-Config-Update-Broadcast
                              |
                              v
              kompatible Fossify-Builds
```

### Wichtige Kompatibilitätsentscheidung

Stock Fossify Commons erkennt aktuell explizit `org.fossify.thankyou` und den alten Provider `org.fossify.android.provider`.

Da Fossify Theming nun bewusst parallel zu Thank You installierbar ist, verwendet es diese Identitäten nicht mehr.

Folge: Unveränderte Stock-Fossify-APKs lesen den neuen Provider noch nicht. Für die finale Suite-Integration wird ein Fossify-Commons-Fork benötigt, der `org.forfossify.theming.provider` als Theme-Quelle akzeptiert. Danach müssen die gewünschten Fossify-Apps gegen diese Commons-Version gebaut werden.

Diese Architektur ist sauberer als ein Thank-You-Ersatz, weil Fossify Theming dadurch ein echtes eigenständiges Produkt bleibt.

## Dateistruktur

```text
app/src/main/kotlin/org/fossify/thankyou/
├── activities/
│   ├── MainActivity.kt
│   ├── SettingsActivity.kt
│   ├── SimpleActivity.kt
│   └── SplashActivity.kt
├── contentproviders/
│   └── MyContentProvider.kt
├── extensions/
│   ├── Context.kt
│   ├── Flow.kt
│   └── Fossify.kt
├── helpers/
│   ├── Config.kt
│   ├── ThemeSyncManager.kt
│   ├── MyContentProviderHelper.kt
│   └── FossifyPackages.kt
├── models/
│   └── ThemeModels.kt
├── receivers/
│   └── ThemeSyncBootReceiver.kt
├── services/
│   └── ThemeSyncService.kt
└── ui/screens/
    ├── MainScreen.kt
    └── SettingsScreen.kt

app/src/main/AndroidManifest.xml
app/src/{core,foss,gplay}/AndroidManifest.xml
gradle.properties
```

Hinweis: Der Kotlin-Source-Namespace stammt noch aus dem Upstream-Fork und kann später rein technisch auf `org.forfossify.theming` migriert werden. Die installierte Android Application-ID ist bereits unabhängig davon `org.forfossify.theming`.

## Umsetzungsschritte

1. Application-ID auf `org.forfossify.theming` umstellen.
2. Eigenen Provider `org.forfossify.theming.provider` verwenden.
3. Alle alten Thank-You-Permissions und Provider-Abhängigkeiten entfernen.
4. Thank-You-Promotion, Donation, Purchase/Unlock und alte Customization-Einstiege entfernen.
5. Theme-Dashboard als Hauptfunktion beibehalten.
6. Material-You-Systemfarben pro Farbrolle einzeln überschreibbar machen.
7. Default-Hintergrund `#0E0E0F` und Default-App-Icon Schwarz verwenden.
8. Light Mode unverändert systemgesteuert lassen.
9. Live-Sync-Service für Wallpaper und UI-Mode beibehalten.
10. Theme-Profile lokal speichern.
11. Fossify-App-Liste als Zielübersicht beibehalten, Thank You selbst daraus ausnehmen.
12. CI für Core/Foss-Debug-Builds grün halten.
13. Standalone-APK parallel zum offiziellen Thank You auf Pixel testen.
14. Danach `FossifyOrg/commons` forken und die Theme-Quelle auf den neuen Provider erweitern.
15. Ziel-Apps gegen den Commons-Fork bauen und visuell testen.

## Offene Fragen/Unklarheiten

- UNKLAR: Welche Fossify-Apps zuerst gegen den Commons-Fork gebaut werden sollen.
- UNKLAR: Ob der Commons-Fork ausschließlich Fossify Theming verwenden soll oder optional zwischen offiziellem Thank You und Fossify Theming wählen können soll.
- UNKLAR: Ob der interne Kotlin-Source-Namespace in derselben Änderung ebenfalls vollständig von `org.fossify.thankyou` auf `org.forfossify.theming` migriert werden soll. Das beeinflusst die installierte Package-ID nicht, wäre aber die letzte Code-Hygiene-Stufe für einen vollständig eigenständigen Fork.
- UNKLAR: OEM-spezifisches Foreground-Service-Verhalten außerhalb von Pixel-Geräten muss separat getestet werden.
