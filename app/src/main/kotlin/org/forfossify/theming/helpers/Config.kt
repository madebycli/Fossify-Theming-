package org.forfossify.theming.helpers

import android.content.Context
import org.fossify.commons.helpers.BaseConfig
import org.forfossify.theming.models.ThemeProfile
import org.forfossify.theming.models.ThemeSettings
import org.json.JSONArray
import org.json.JSONObject

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)

        private const val PRIMARY_SYSTEM = "hybrid_primary_system"
        private const val PRIMARY_COLOR = "hybrid_primary_color"
        private const val ACCENT_SYSTEM = "hybrid_accent_system"
        private const val ACCENT_COLOR = "hybrid_accent_color"
        private const val BACKGROUND_SYSTEM = "hybrid_background_system"
        private const val BACKGROUND_COLOR = "hybrid_background_color"
        private const val TEXT_SYSTEM = "hybrid_text_system"
        private const val TEXT_COLOR = "hybrid_text_color"
        private const val APP_ICON_SYSTEM = "hybrid_app_icon_system"
        private const val APP_ICON_COLOR = "hybrid_app_icon_color"
        private const val LIVE_SYNC = "hybrid_live_sync"
        private const val PROFILES = "hybrid_profiles"
    }

    fun getThemeSettings(): ThemeSettings {
        val defaults = ThemeSettings()
        return ThemeSettings(
            primaryUsesSystem = prefs.getBoolean(PRIMARY_SYSTEM, defaults.primaryUsesSystem),
            primaryColor = prefs.getInt(PRIMARY_COLOR, defaults.primaryColor),
            accentUsesSystem = prefs.getBoolean(ACCENT_SYSTEM, defaults.accentUsesSystem),
            accentColor = prefs.getInt(ACCENT_COLOR, defaults.accentColor),
            backgroundUsesSystem = prefs.getBoolean(BACKGROUND_SYSTEM, defaults.backgroundUsesSystem),
            backgroundColor = prefs.getInt(BACKGROUND_COLOR, defaults.backgroundColor),
            textUsesSystem = prefs.getBoolean(TEXT_SYSTEM, defaults.textUsesSystem),
            textColor = prefs.getInt(TEXT_COLOR, defaults.textColor),
            appIconUsesSystem = prefs.getBoolean(APP_ICON_SYSTEM, defaults.appIconUsesSystem),
            appIconColor = prefs.getInt(APP_ICON_COLOR, defaults.appIconColor),
            liveSyncEnabled = prefs.getBoolean(LIVE_SYNC, defaults.liveSyncEnabled),
        )
    }

    fun saveThemeSettings(settings: ThemeSettings) {
        prefs.edit()
            .putBoolean(PRIMARY_SYSTEM, settings.primaryUsesSystem)
            .putInt(PRIMARY_COLOR, settings.primaryColor)
            .putBoolean(ACCENT_SYSTEM, settings.accentUsesSystem)
            .putInt(ACCENT_COLOR, settings.accentColor)
            .putBoolean(BACKGROUND_SYSTEM, settings.backgroundUsesSystem)
            .putInt(BACKGROUND_COLOR, settings.backgroundColor)
            .putBoolean(TEXT_SYSTEM, settings.textUsesSystem)
            .putInt(TEXT_COLOR, settings.textColor)
            .putBoolean(APP_ICON_SYSTEM, settings.appIconUsesSystem)
            .putInt(APP_ICON_COLOR, settings.appIconColor)
            .putBoolean(LIVE_SYNC, settings.liveSyncEnabled)
            .apply()
    }

    fun getThemeProfiles(): List<ThemeProfile> {
        val raw = prefs.getString(PROFILES, "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                repeat(array.length()) { index ->
                    val profile = array.getJSONObject(index)
                    add(
                        ThemeProfile(
                            name = profile.getString("name"),
                            settings = profile.getJSONObject("settings").toThemeSettings(),
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveThemeProfile(name: String, settings: ThemeSettings) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return

        val updated = getThemeProfiles()
            .filterNot { it.name.equals(cleanName, ignoreCase = true) }
            .plus(ThemeProfile(cleanName, settings))
            .sortedBy { it.name.lowercase() }

        saveProfiles(updated)
    }

    fun deleteThemeProfile(name: String) {
        saveProfiles(getThemeProfiles().filterNot { it.name == name })
    }

    private fun saveProfiles(profiles: List<ThemeProfile>) {
        val array = JSONArray()
        profiles.forEach { profile ->
            array.put(
                JSONObject()
                    .put("name", profile.name)
                    .put("settings", profile.settings.toJson())
            )
        }
        prefs.edit().putString(PROFILES, array.toString()).apply()
    }

    private fun ThemeSettings.toJson() = JSONObject()
        .put("primaryUsesSystem", primaryUsesSystem)
        .put("primaryColor", primaryColor)
        .put("accentUsesSystem", accentUsesSystem)
        .put("accentColor", accentColor)
        .put("backgroundUsesSystem", backgroundUsesSystem)
        .put("backgroundColor", backgroundColor)
        .put("textUsesSystem", textUsesSystem)
        .put("textColor", textColor)
        .put("appIconUsesSystem", appIconUsesSystem)
        .put("appIconColor", appIconColor)
        .put("liveSyncEnabled", liveSyncEnabled)

    private fun JSONObject.toThemeSettings(): ThemeSettings {
        val defaults = ThemeSettings()
        return ThemeSettings(
            primaryUsesSystem = optBoolean("primaryUsesSystem", defaults.primaryUsesSystem),
            primaryColor = optInt("primaryColor", defaults.primaryColor),
            accentUsesSystem = optBoolean("accentUsesSystem", defaults.accentUsesSystem),
            accentColor = optInt("accentColor", defaults.accentColor),
            backgroundUsesSystem = optBoolean("backgroundUsesSystem", defaults.backgroundUsesSystem),
            backgroundColor = optInt("backgroundColor", defaults.backgroundColor),
            textUsesSystem = optBoolean("textUsesSystem", defaults.textUsesSystem),
            textColor = optInt("textColor", defaults.textColor),
            appIconUsesSystem = optBoolean("appIconUsesSystem", defaults.appIconUsesSystem),
            appIconColor = optInt("appIconColor", defaults.appIconColor),
            liveSyncEnabled = optBoolean("liveSyncEnabled", defaults.liveSyncEnabled),
        )
    }
}
