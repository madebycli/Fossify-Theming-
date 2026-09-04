package org.forfossify.theming.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fossify.commons.activities.BaseComposeActivity
import org.fossify.commons.compose.extensions.enableEdgeToEdgeSimple
import org.fossify.commons.helpers.isTiramisuPlus
import org.forfossify.theming.extensions.config
import org.forfossify.theming.extensions.launchChangeAppLanguageIntent
import org.forfossify.theming.ui.screens.SettingsScreen
import org.forfossify.theming.ui.theme.ThemingThemeSurface
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : BaseComposeActivity() {
    private val preferences by lazy { config }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            ThemingThemeSurface(settings = preferences.getThemeSettings()) {
                val wasUseEnglishToggledFlow by preferences.wasUseEnglishToggledFlow
                    .collectAsStateWithLifecycle(preferences.wasUseEnglishToggled)
                val useEnglishFlow by preferences.useEnglishFlow
                    .collectAsStateWithLifecycle(preferences.useEnglish)
                val displayLanguage = remember { Locale.getDefault().displayLanguage }
                val isUseEnglishEnabled by remember(wasUseEnglishToggledFlow) {
                    derivedStateOf {
                        (wasUseEnglishToggledFlow || Locale.getDefault().language != "en") &&
                            !isTiramisuPlus()
                    }
                }

                SettingsScreen(
                    displayLanguage = displayLanguage,
                    isUseEnglishEnabled = isUseEnglishEnabled,
                    isUseEnglishChecked = useEnglishFlow,
                    onUseEnglishPress = { isChecked ->
                        preferences.useEnglish = isChecked
                        exitProcess(0)
                    },
                    onSetupLanguagePress = ::launchChangeAppLanguageIntent,
                    goBack = ::finish,
                )
            }
        }
    }
}
