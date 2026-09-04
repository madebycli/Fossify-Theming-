# Modified Fossify Thank You

This repository is a modified GPLv3 work based on Fossify Thank You. The resulting product is Fossify Theming, a theming-only utility.

## Main modifications

- Reworked the application into **Fossify Theming**.
- Added standalone Android identity `org.forfossify.theming` and provider `org.forfossify.theming.provider`.
- Removed purchase, donation, unlock and promotional Thank You product behavior.
- Removed unused About/legacy helper code and upstream product/release automation.
- Added hybrid Material You + custom per-color overrides.
- Added a dark-mode-only custom background, default `#0E0E0F`.
- Added configurable system/custom app-icon color, default black.
- Added persistent Live Sync for wallpaper and UI-mode changes.
- Added local theme profiles.
- Added installed-app compatibility diagnostics.
- Added a stock compatibility flavor using the legacy Fossify package/provider contract for unmodified Fossify apps.
- Reduced build variants to `standalone` and `compat`.
- Added an optimized temporary-signed `compatPerformance` test build.

## Upstream policy

The repository does not automatically merge Fossify Thank You changes. Upstream work is reviewed and imported only when relevant to theming, Fossify theme-provider compatibility, Android API/security requirements or required build compatibility. See `UPSTREAM.md`.

## Compatibility

The standalone package can coexist with official Fossify Thank You. Current stock Fossify Commons still expects the legacy Thank You package/provider contract, so `compat` exists as a transition path until target apps can consume the standalone provider directly.

## License

The upstream project is licensed under GNU GPL v3. This modified version remains under GNU GPL v3. See `LICENSE`.
