# Fossify Theming

A focused Android theming controller for Fossify apps.

The project started as a GPLv3 fork of Fossify Thank You, but the app itself is now intentionally limited to theming: Material You system colors, per-color overrides, profiles, live sync, app compatibility status and theme export.

## What it does

In dark mode, every supported color role can independently use either the current Android Material You system color or a custom value:

- Primary
- Accent
- Background, default `#0E0E0F`
- Text
- App icon, default black

Light mode stays on the normal system Material You theme. Profiles can be saved locally and Live Sync refreshes exported colors after wallpaper or UI-mode changes.

## Builds

There are only two distribution flavors:

- `standalone`: application ID `org.forfossify.theming`, provider `org.forfossify.theming.provider`. This is the clean long-term app identity and can coexist with official Fossify Thank You.
- `compat`: application ID `org.fossify.thankyou`, provider `org.fossify.android.provider`. This exists only so unmodified stock Fossify apps can consume the theme today.

The Kotlin/Compose source namespace is `org.forfossify.theming`. The legacy `org.fossify.thankyou` identifier remains only where the stock-compatibility contract explicitly requires it.

The optimized temporary test build is `compatPerformance`. It is R8/minified, resource-shrunk, non-debuggable and currently signed only with the Android debug key. The real release key will be configured later.

### Stock compatibility note

Stock Fossify Commons checks the legacy Thank You package/provider and the global-theme permission. Because the compatibility build uses a locally defined normal permission, Fossify apps that were installed before Fossify Theming may need to be reinstalled once. The main screen shows which installed apps are ready and which need that reinstall.

## Upstream policy

This repository does **not** automatically merge Fossify Thank You upstream changes.

Upstream changes are intentionally reviewed and brought downstream only when they are relevant to the theming project, for example:

- Fossify global-theme/provider contract changes
- Material You / theme handling changes
- Fossify Commons compatibility changes that affect theming
- Android build/API/security fixes required by this app

Donation, purchase/unlock, store metadata, Thank You UI and unrelated Fossify maintenance changes are not synced. See `UPSTREAM.md`.

## CI

The single project workflow builds:

```bash
./gradlew assembleStandaloneDebug assembleCompatDebug assembleCompatPerformance
```

Successful `main` builds also refresh the temporary APK in `performance/`.

## Privacy

- No account
- No cloud backend
- No wallpaper image upload
- No network connection required for theming
- Profiles and overrides stay on-device

## License and upstream

This project is a modified work based on Fossify Thank You and remains licensed under GNU GPL v3. See `LICENSE` and `MODIFICATIONS.md`.

Upstream origin: `FossifyOrg/Thank-You`
