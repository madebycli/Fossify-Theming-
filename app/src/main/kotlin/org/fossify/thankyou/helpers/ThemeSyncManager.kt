package org.fossify.thankyou.helpers

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.ui.graphics.toArgb
import org.fossify.commons.helpers.MyContentProvider.COL_ACCENT_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_APP_ICON_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_BACKGROUND_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_LAST_UPDATED_TS
import org.fossify.commons.helpers.MyContentProvider.COL_PRIMARY_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_TEXT_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_THEME_TYPE
import org.fossify.commons.helpers.MyContentProvider.GLOBAL_THEME_CUSTOM
import org.fossify.commons.helpers.MyContentProvider.GLOBAL_THEME_SYSTEM
import org.fossify.thankyou.contentproviders.MyContentProvider
import org.fossify.thankyou.models.ThemeColorRole
import org.fossify.thankyou.models.ThemeSettings

object ThemeSyncManager {
    fun apply(
        context: Context,
        settings: ThemeSettings = Config.newInstance(context).getThemeSettings(),
    ): Boolean {
        val systemColors = getSystemColors(context)
        val appIconColor = resolve(
            context = context,
            settings = settings,
            role = ThemeColorRole.APP_ICON,
            systemColors = systemColors,
        )

        val values = ContentValues().apply {
            put(COL_APP_ICON_COLOR, appIconColor)

            if (context.isSystemDarkMode()) {
                put(COL_THEME_TYPE, GLOBAL_THEME_CUSTOM)
                put(
                    COL_PRIMARY_COLOR,
                    resolve(context, settings, ThemeColorRole.PRIMARY, systemColors),
                )
                put(
                    COL_ACCENT_COLOR,
                    resolve(context, settings, ThemeColorRole.ACCENT, systemColors),
                )
                put(
                    COL_BACKGROUND_COLOR,
                    resolve(context, settings, ThemeColorRole.BACKGROUND, systemColors),
                )
                put(
                    COL_TEXT_COLOR,
                    resolve(context, settings, ThemeColorRole.TEXT, systemColors),
                )
            } else {
                put(COL_THEME_TYPE, GLOBAL_THEME_SYSTEM)
            }

            put(COL_LAST_UPDATED_TS, System.currentTimeMillis() / 1000L)
        }

        return runCatching {
            context.contentResolver.update(
                MyContentProvider.SETTINGS_URI,
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
        val primary = scheme.primary.toArgb()

        return mapOf(
            ThemeColorRole.PRIMARY to primary,
            ThemeColorRole.ACCENT to scheme.secondary.toArgb(),
            ThemeColorRole.BACKGROUND to scheme.surface.toArgb(),
            ThemeColorRole.TEXT to scheme.onSurface.toArgb(),
            ThemeColorRole.APP_ICON to nearestAppIconColor(context, primary),
        )
    }

    private fun resolve(
        context: Context,
        settings: ThemeSettings,
        role: ThemeColorRole,
        systemColors: Map<ThemeColorRole, Int>,
    ): Int {
        val color = if (settings.usesSystem(role)) {
            systemColors.getValue(role)
        } else {
            settings.customColor(role)
        }

        return if (role == ThemeColorRole.APP_ICON) {
            nearestAppIconColor(context, color)
        } else {
            color
        }
    }

    private fun nearestAppIconColor(context: Context, targetColor: Int): Int {
        val candidates = context.resources.getIntArray(org.fossify.commons.R.array.md_app_icon_colors)
        return candidates.minByOrNull { candidate ->
            colorDistanceSquared(candidate, targetColor)
        } ?: Color.BLACK
    }

    private fun colorDistanceSquared(first: Int, second: Int): Long {
        val red = Color.red(first) - Color.red(second)
        val green = Color.green(first) - Color.green(second)
        val blue = Color.blue(first) - Color.blue(second)
        return red.toLong() * red + green.toLong() * green + blue.toLong() * blue
    }

    private fun Context.isSystemDarkMode(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
    }
}
