package org.forfossify.theming.ui.theme

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import org.forfossify.theming.helpers.ThemeSyncManager
import org.forfossify.theming.models.ThemeColorRole
import org.forfossify.theming.models.ThemeSettings

/**
 * App-local theme for Fossify Theming.
 *
 * We intentionally do not use Fossify Commons' AppTheme/AppThemeSurface here. Those wrappers
 * include Fossify's anti-repackaging dialog for package names outside org.fossify.*, while this
 * project is a legitimate GPL fork with the standalone package org.forfossify.theming.
 *
 * The same hybrid rules that are exported to compatible Fossify apps are also rendered here, so
 * the dashboard is an immediate live preview of the current configuration.
 */
@Composable
fun ThemingThemeSurface(
    settings: ThemeSettings,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkMode = isSystemInDarkTheme()

    val baseScheme = when {
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && darkMode ->
            dynamicDarkColorScheme(context)

        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ->
            dynamicLightColorScheme(context)

        darkMode -> darkColorScheme()
        else -> lightColorScheme()
    }

    // Light mode intentionally remains untouched Material You.
    val colorScheme = if (!darkMode) {
        baseScheme
    } else {
        val systemColors = ThemeSyncManager.getSystemColors(context)
        val primary = resolve(settings, ThemeColorRole.PRIMARY, systemColors)
        val accent = resolve(settings, ThemeColorRole.ACCENT, systemColors)
        val background = resolve(settings, ThemeColorRole.BACKGROUND, systemColors)
        val text = resolve(settings, ThemeColorRole.TEXT, systemColors)

        baseScheme.copy(
            primary = Color(primary),
            onPrimary = contrastColor(primary),
            secondary = Color(accent),
            onSecondary = contrastColor(accent),
            background = Color(background),
            onBackground = Color(text),
            surface = Color(background),
            onSurface = Color(text),
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground,
        ) {
            content()
        }
    }
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

private fun contrastColor(argb: Int): Color {
    val opaque = AndroidColor.rgb(
        AndroidColor.red(argb),
        AndroidColor.green(argb),
        AndroidColor.blue(argb),
    )
    return if (ColorUtils.calculateLuminance(opaque) > 0.5) Color.Black else Color.White
}
