# Fossify Theming

A standalone GPLv3 Android app for controlling a shared Fossify color profile.

Fossify Theming started from Fossify Thank You, but the product itself is now focused only on theming. It has its own Android application ID and can be installed next to the official Fossify Thank You app.

## Package identity

- Application ID: `org.forfossify.theming`
- Theme provider: `content://org.forfossify.theming.provider/settings`
- Visible app name: `Fossify Theming`

The app no longer acts as a purchase/unlock or donation companion.

## Hybrid Material You theming

Fossify Theming combines Android's wallpaper-generated Material You colors with per-color overrides.

In dark mode, each supported color can independently use either:

- **System**: follow the current Material You / wallpaper color
- **Custom**: keep a color selected by you

The default profile uses:

- Primary: System
- Accent: System
- Background: Custom `#0E0E0F`
- Text: System
- App icon: Custom black

Light mode intentionally remains the normal system Material You theme.

## Live Sync

Live Sync runs as an Android foreground service and refreshes the exported theme when the wallpaper or light/dark configuration changes. The service can be turned off from the dashboard.

## Profiles

Any hybrid color setup can be saved as a named local profile, loaded again later, or deleted.

## Fossify integration

The standalone app exports its theme through its own provider and sends the Fossify global-config update broadcast.

**Important:** current stock Fossify Commons explicitly detects `org.fossify.thankyou` and its original provider. Therefore unmodified Fossify release APKs do not automatically consume the new standalone provider yet. The intended integration is to point a Fossify Commons fork at `org.forfossify.theming.provider`, then build the target Fossify apps with that Commons version.

This tradeoff is intentional because it lets Fossify Theming coexist with the official Thank You app and keeps the theming project independent from Thank You.

## Privacy

- No account
- No cloud backend
- No wallpaper image upload
- No internet connection is required for theming
- Profiles and overrides stay on-device

## License and upstream

This project is a modified work based on Fossify Thank You and remains licensed under the GNU General Public License v3.0. See `LICENSE` and `MODIFICATIONS.md`.

Upstream project: FossifyOrg/Thank-You
