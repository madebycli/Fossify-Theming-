# Modified Fossify Thank You

This repository is a modified work based on Fossify Thank You, but the resulting application is a standalone theming utility.

## Main modifications

- Reworked the app into **Fossify Theming**.
- Changed the Android application ID to `org.forfossify.theming`.
- Moved the exported theme provider to `org.forfossify.theming.provider`.
- Removed the original Thank You purchase/donation role and related promotional UI/settings.
- Added hybrid Material You + custom per-color overrides.
- Added a dark-mode-only custom background, default `#0E0E0F`.
- Added configurable system/custom app-icon color, default black.
- Added persistent Live Sync for wallpaper and UI-mode changes.
- Added local theme profiles.
- Kept the installed Fossify app list as a theming-target overview.
- Removed Thank You from the theming-target list so the official app can coexist independently.

## Compatibility

The standalone package can be installed next to official Fossify Thank You.

Current stock Fossify Commons hardcodes the official Thank You package/provider for global theming. Therefore stock Fossify APKs need a Commons integration change before they can consume `org.forfossify.theming.provider`.

## License

The upstream project is licensed under GNU GPL v3. This modified version remains under GNU GPL v3. See `LICENSE`.
