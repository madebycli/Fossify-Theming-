# Fossify Theming

A GPLv3 fork of Fossify Thank You focused on one job: controlling the colors of the installed Fossify app suite from one place.

## Hybrid Material You theming

Fossify Theming combines Android's wallpaper-generated Material You colors with per-color overrides.

In dark mode, each supported Fossify color can independently use either:

- **System**: follow the current Material You / wallpaper color
- **Custom**: keep a color selected by you

The initial profile uses:

- Primary: System
- Accent: System
- Background: Custom `#0E0E0F`
- Text: System
- App icon: Custom black

Light mode intentionally remains the normal system Material You theme.

## Live Sync

Live Sync runs as an Android foreground service and re-applies the theme when the wallpaper or light/dark configuration changes. The service can be turned off from the main dashboard.

## Profiles

Any hybrid color setup can be saved as a named local profile, loaded again later, or deleted.

## Compatibility

The package ID intentionally remains `org.fossify.thankyou` and the provider authority remains `org.fossify.android.provider` because current stock Fossify apps explicitly look for those identifiers when enabling global theming.

That makes this app a **drop-in replacement** for the official Fossify Thank You app. You cannot keep both installed at the same time.

The independently signed fork adapts the global settings provider so existing Fossify apps can read the shared theme without requiring Fossify's private signing certificate. Provider access is still limited to Fossify package names.

## App icon colors

Fossify apps ship a fixed set of launcher icon color aliases. System/custom icon colors are therefore mapped to the closest available Fossify icon color instead of generating arbitrary new launcher assets at runtime.

## Privacy

- No account
- No cloud backend
- No wallpaper image upload
- No internet connection is required for theming
- Profiles and overrides stay on-device

## License and upstream

This is a modified version of Fossify Thank You and remains licensed under the GNU General Public License v3.0. See `LICENSE` and `MODIFICATIONS.md`.

Upstream project: FossifyOrg/Thank-You
