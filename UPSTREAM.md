# Upstream policy

Fossify Theming is intentionally maintained as a theming-only downstream project.

## Source upstreams

- `FossifyOrg/Thank-You`: historical base of this fork.
- `FossifyOrg/commons`: shared Fossify APIs and theme behavior used by the app and target apps.

## What gets synced

Upstream work is reviewed manually and copied/cherry-picked only when it materially affects Fossify Theming:

1. Global theme provider/config contract changes.
2. Material You, dynamic color or theme-role changes.
3. App icon theming changes.
4. Android API, permission or security changes required for theme sync.
5. Build-system changes required to keep the theming app compiling against current Fossify Commons.

## What does not get synced

- Donation, billing, purchase or feature-unlock code.
- Thank You promotional UI or copy.
- Store/fastlane metadata from Thank You.
- Translation-only churn for strings that Fossify Theming does not use.
- Release automation or Fossify organization maintenance workflows that are unrelated to this project.
- Unrelated product features.

## Merge rule

There is no automatic upstream merge workflow. Relevant upstream commits should be reviewed first, then ported as a small dedicated commit or pull request so the theming-only architecture stays easy to audit.
