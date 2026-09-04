package org.forfossify.theming.models

enum class ThemeColorRole {
    PRIMARY,
    ACCENT,
    BACKGROUND,
    TEXT,
    APP_ICON,
}

data class ThemeSettings(
    val primaryUsesSystem: Boolean = true,
    val primaryColor: Int = 0xFF234B60.toInt(),
    val accentUsesSystem: Boolean = true,
    val accentColor: Int = 0xFFADC8DA.toInt(),
    val backgroundUsesSystem: Boolean = false,
    val backgroundColor: Int = 0xFF0E0E0F.toInt(),
    val textUsesSystem: Boolean = true,
    val textColor: Int = 0xFFE5E5E7.toInt(),
    val appIconUsesSystem: Boolean = false,
    val appIconColor: Int = 0xFF000000.toInt(),
    val liveSyncEnabled: Boolean = true,
) {
    fun usesSystem(role: ThemeColorRole): Boolean = when (role) {
        ThemeColorRole.PRIMARY -> primaryUsesSystem
        ThemeColorRole.ACCENT -> accentUsesSystem
        ThemeColorRole.BACKGROUND -> backgroundUsesSystem
        ThemeColorRole.TEXT -> textUsesSystem
        ThemeColorRole.APP_ICON -> appIconUsesSystem
    }

    fun customColor(role: ThemeColorRole): Int = when (role) {
        ThemeColorRole.PRIMARY -> primaryColor
        ThemeColorRole.ACCENT -> accentColor
        ThemeColorRole.BACKGROUND -> backgroundColor
        ThemeColorRole.TEXT -> textColor
        ThemeColorRole.APP_ICON -> appIconColor
    }

    fun withSystem(role: ThemeColorRole, enabled: Boolean): ThemeSettings = when (role) {
        ThemeColorRole.PRIMARY -> copy(primaryUsesSystem = enabled)
        ThemeColorRole.ACCENT -> copy(accentUsesSystem = enabled)
        ThemeColorRole.BACKGROUND -> copy(backgroundUsesSystem = enabled)
        ThemeColorRole.TEXT -> copy(textUsesSystem = enabled)
        ThemeColorRole.APP_ICON -> copy(appIconUsesSystem = enabled)
    }

    fun withCustomColor(role: ThemeColorRole, color: Int): ThemeSettings = when (role) {
        ThemeColorRole.PRIMARY -> copy(primaryColor = color)
        ThemeColorRole.ACCENT -> copy(accentColor = color)
        ThemeColorRole.BACKGROUND -> copy(backgroundColor = color)
        ThemeColorRole.TEXT -> copy(textColor = color)
        ThemeColorRole.APP_ICON -> copy(appIconColor = color)
    }
}

data class ThemeProfile(
    val name: String,
    val settings: ThemeSettings,
)
