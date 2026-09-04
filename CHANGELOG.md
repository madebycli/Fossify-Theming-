# Changelog

## Unreleased

### Changed
- Repository reduced to the theming product and its required compatibility code.
- Build flavors simplified to `standalone` and `compat`.
- Main CI now builds only Fossify Theming artifacts.
- Upstream sync changed to a manual, theming-only policy.

### Removed
- Unused Thank You About helper/activity integration.
- Unused legacy activity/constants.
- Fossify organization release/store/translation maintenance workflows.
- Automatic Commons update workflow.
- Upstream Fastlane/Gem release tooling and stale CODEOWNERS/contribution metadata.

## 0.1.0 - 2026-09-03

### Added
- Hybrid Material You system/custom color controls.
- Dark-mode background default `#0E0E0F`.
- System/custom Primary, Accent, Background, Text and App Icon roles.
- Local profiles and Live Sync.
- Stock Fossify compatibility mode and per-app compatibility diagnostics.
- Optimized temporarily signed `compatPerformance` test APK.
