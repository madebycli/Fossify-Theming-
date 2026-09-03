package org.fossify.thankyou.helpers

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.ui.graphics.toArgb
import org.fossify.commons.helpers.MyContentProvider
import org.fossify.commons.helpers.MyContentProvider.COL_ACCENT_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_APP_ICON_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_BACKGROUND_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_LAST_UPDATED_TS
import org.fossify.commons.helpers.MyContentProvider.COL_PRIMARY_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_TEXT_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_THEME_TYPE
import org.fossify.commons.helpers.MyContentProvider.GLOBAL_THEME_CUSTOM
import org.fossify.commons.helpers.MyContentProvider.GLOBAL_THEME_SYSTEM
import org.fossify.thankyou.models.ThemeColorRole
import org.fossify.thankyou.models.ThemeSettings

object ThemeSyncManager {
    fun apply(context: Context, settings: ThemeSettings = Config.newInstance(context).getThemeSettings()): Boolean {
        val values = ContentValues().apply {
            if (context.isSystemDarkMode()) {
                val systemColors = getSystemColors(context)
                put(COL_THEME_TYPE, GLOBAL_THEME_CUSTOM)
                put(COL_PRIMARY_COLOR, resolve(settings, ThemeColorRole.PRIMARY, systemColors))
                put(COL_ACCENT_COLOR, resolve(settings, ThemeColorRole.ACCENT, systemColors))
                put(COL_BACKGROUND_COLOR, resolve(settings, ThemeColorRole.BACKGROUND, systemColors))
                put(COL_TEXT_COLOR, resolve(settings, ThemeColorRole.TEXT, systemColors))
                put(COL_APP_ICON_COLOR, resolve(settings, ThemeColorRole.APP_ICON, systemColors))
            } else {
                // Light mode intentionally stays 100% Material You / system controlled.
                put(COL_THEME_TYPE, GLOBAL_THEME_SYSTEM)
            }

            put(COL_LAST_UPDATED_TS, System.currentTimeMillis() / 1000L)
        }

        return runCatching {
            context.contentResolver.update(
                MyContentProvider.MY_CONTENT_URI,
                values,
                null,
                null,
            ) > 0
        }.getOrDefault(false)
    }

    fun getSystemColors(context: Context): Map<ThemeColorRole, Int> {
        val scheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(context)
        } else {
            darkColorScheme()
        }

        return mapOf(
            ThemeColorRole.PRIMARY to scheme.primary.toArgb(),
            ThemeColorRole.ACCENT to scheme.secondary.toArgb(),
            ThemeColorRole.BACKGROUND to scheme.surface.toArgb(),
            ThemeColorRole.TEXT to scheme.onSurface.toArgb(),
            ThemeColorRole.APP_ICON to scheme.primary.toArgb(),
        )
    }

    private fun resolve(
        settings: ThemeSettings,
        role: ThemeColorRole,
        systemColors: Map<ThemeColorRole, Int>,
    ): Int {
        return if (settings.usesSystem(role)) {
            systemColors.getValue(role)
        } else {
            settings.customColor(role)
        }
    }

    private fun Context.isSystemDarkMode(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
    }
}
