# Fossify Theming - PLAN

## Ziel

`Fossify Thank You` wird zu einer zentralen Theme-App für die installierte Fossify-Suite umgebaut.

Die App nutzt weiterhin die vorhandene Fossify-Global-Theme-Infrastruktur, erweitert sie aber um einen Hybridmodus:

- Material-You-Systemfarben vom Wallpaper bleiben die Basis.
- Jede vorhandene Fossify-Farbe kann einzeln auf `System` oder `Custom` gestellt werden.
- Custom-Overrides gelten nur im Dark Mode.
- Light Mode bleibt vollständig beim normalen System-/Material-You-Theme.
- Standard-Hintergrund im Dark Mode ist `#0E0E0F`.
- Standard-App-Icon-Farbe ist Schwarz.
- Wallpaper- und UI-Mode-Änderungen werden über Live Sync automatisch verteilt.
- Konfigurationen können als Profile gespeichert werden.
- Die installierten Fossify-Apps bleiben unter dem Theme-Dashboard sichtbar.

## Anforderungen

- Android 12+ nutzt die echten dynamischen Material-You-Farben.
- Android 8 bis 11 erhält einen statischen Material-3-Fallback.
- Dark Mode: Primary, Accent, Background, Text und App Icon jeweils System oder Custom.
- Light Mode: immer `GLOBAL_THEME_SYSTEM`, keine Dark-Mode-Overrides.
- Live Sync überwacht Wallpaper- und Dark/Light-Änderungen.
- Profile können gespeichert, geladen und gelöscht werden.
- Keine Netzwerkverbindung, keine Cloud-Kosten und keine Speicherung des Wallpaper-Bildes.

## Architektur

### Gewählter Ansatz: Drop-in-Ersatz für Fossify Thank You

Die bestehenden Fossify-Apps suchen explizit nach dem Paket `org.fossify.thankyou` und nach dem Provider `org.fossify.android.provider`.

Deshalb bleibt die technische Application-ID absichtlich `org.fossify.thankyou`. Der sichtbare App-Name wird zu `Fossify Theming`.

Das bedeutet:

- Die App ersetzt das originale Thank You.
- Original Thank You und Fossify Theming können nicht gleichzeitig installiert sein.
- Dafür müssen die einzelnen Fossify-App-Repositories nicht verändert werden.

Da der Fork nicht mit Fossifys Original-Zertifikat signiert werden kann, wird die globale Permission von `signature` auf `normal` geändert. Der ContentProvider prüft zusätzlich selbst, dass nur Pakete mit `org.fossify.` zugreifen dürfen.

### Datenfluss

```text
Pixel Wallpaper / Material You
        |
        v
ThemeSyncService
        |
        +--> Systemfarben lesen
        |
        +--> ThemeSettings laden
        |      System / Custom pro Farbrolle
        |
        +--> Dark Mode?
               |
               +-- Nein --> GLOBAL_THEME_SYSTEM
               |
               +-- Ja ----> Systemfarben + Custom Overrides
                              |
                              v
                    org.fossify.android.provider
                              |
                              v
                  GLOBAL_CONFIG_UPDATED Broadcast
                              |
                              v
          Files / Gallery / Contacts / Calendar / ...
```

## Dateistruktur

```text
app/src/main/kotlin/org/fossify/thankyou/
├── activities/
│   ├── MainActivity.kt
│   └── SettingsActivity.kt
├── contentproviders/
│   └── MyContentProvider.kt
├── helpers/
│   ├── Config.kt
│   ├── ThemeSyncManager.kt
│   └── MyContentProviderHelper.kt
├── models/
│   └── ThemeModels.kt
├── receivers/
│   └── ThemeSyncBootReceiver.kt
├── services/
│   └── ThemeSyncService.kt
└── ui/screens/
    ├── MainScreen.kt
    └── SettingsScreen.kt

app/src/{core,foss,gplay}/AndroidManifest.xml
app/src/main/res/values/strings.xml
```

## Umsetzungsschritte

1. Sichtbaren App-Namen auf `Fossify Theming` ändern.
2. Application-ID `org.fossify.thankyou` für Stock-Fossify-Kompatibilität behalten.
3. Thank-You-Werbe-/Donation-UI aus dem Homescreen entfernen.
4. Theme-Dashboard an den Anfang des Homescreens setzen.
5. Datenmodell für System-/Custom-Overrides erstellen.
6. Default-Hintergrund `#0E0E0F` und Default-App-Icon Schwarz setzen.
7. Fossifys echten Global-Theme-Provider weiterverwenden.
8. Provider für unabhängig signierte Builds kompatibel machen und paketbasiert absichern.
9. Material-You-Farben lesen und Dark-Mode-Overrides auflösen.
10. Light Mode unverändert auf System lassen.
11. Live-Sync-Foreground-Service hinzufügen.
12. Wallpaper- und Configuration-Broadcasts dynamisch überwachen.
13. Live Sync nach Boot/Update wieder aktivieren.
14. Profile in lokalen SharedPreferences speichern.
15. Bestehende Fossify-App-Liste unter dem Dashboard behalten.
16. CI laufen lassen und Buildfehler beheben.
17. Auf Pixel testen: Wallpaper, Dark/Light, App-Icons und Profile.

## Offene Fragen / Unklarheiten

- UNKLAR: Ob jede Installationsquelle der vorhandenen Fossify-Apps die neu auf `normal` gesetzte Global-Settings-Permission ohne Neuinstallation sofort übernimmt. Falls eine App nicht synchronisiert, zuerst App-Neustart, danach gegebenenfalls Neuinstallation dieser Fossify-App testen.
- UNKLAR: Android/OEMs können Foreground-Service-Verhalten und Akkuoptimierung unterschiedlich behandeln. Auf Pixel mit Android 16 gezielt testen.
- UNKLAR: Die vorhandene Fossify-App-Icon-Logik kann nur die von Fossify mitgelieferten diskreten Icon-Farben aktivieren. Eine völlig beliebige Custom-Farbe kann als globale Wunschfarbe gespeichert werden, das Launcher-Icon kann aber auf vorhandene Alias-Farben begrenzt sein.
