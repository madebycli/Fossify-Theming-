# Modified Fossify Thank You

This repository is a modified version of Fossify Thank You.

## Main modifications

- Reworked the app into **Fossify Theming**.
- Added hybrid Material You + custom per-color overrides.
- Added a dark-mode-only custom background, default `#0E0E0F`.
- Added configurable system/custom app-icon color, default black.
- Added persistent Live Sync for wallpaper and UI-mode changes.
- Added local theme profiles.
- Removed Thank-You-specific promotional content from the home screen.
- Kept the upstream package ID and provider authority for compatibility with existing Fossify apps.
- Adapted global-theme provider permission handling for independently signed builds and added provider-side package validation.

## License

The upstream project is licensed under GNU GPL v3. This modified version remains under GNU GPL v3. See `LICENSE`.

The package ID `org.fossify.thankyou` is intentionally retained because current Fossify Commons code explicitly detects that package when enabling global theming. As a result, this build is a drop-in replacement and cannot be installed next to the official Thank You app.
