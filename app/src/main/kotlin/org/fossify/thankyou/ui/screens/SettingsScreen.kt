package org.fossify.thankyou.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.fossify.commons.R
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleColumnScaffold
import org.fossify.commons.compose.settings.SettingsGroup
import org.fossify.commons.compose.settings.SettingsPreferenceComponent
import org.fossify.commons.compose.settings.SettingsSwitchComponent
import org.fossify.commons.compose.settings.SettingsTitleTextComponent
import org.fossify.commons.compose.theme.AppThemeSurface
import org.fossify.commons.helpers.isTiramisuPlus

@Composable
internal fun SettingsScreen(
    displayLanguage: String,
    isUseEnglishEnabled: Boolean,
    isUseEnglishChecked: Boolean,
    onUseEnglishPress: (Boolean) -> Unit,
    onSetupLanguagePress: () -> Unit,
    goBack: () -> Unit,
) {
    SimpleColumnScaffold(title = stringResource(id = R.string.settings), goBack = goBack) {
        SettingsGroup(title = {
            SettingsTitleTextComponent(text = stringResource(id = R.string.general_settings))
        }) {
            if (isUseEnglishEnabled) {
                SettingsSwitchComponent(
                    label = stringResource(id = R.string.use_english_language),
                    initialValue = isUseEnglishChecked,
                    onChange = onUseEnglishPress,
                    showCheckmark = false,
                )
            }
            if (isTiramisuPlus()) {
                SettingsPreferenceComponent(
                    label = stringResource(id = R.string.language),
                    value = displayLanguage,
                    doOnPreferenceClick = onSetupLanguagePress,
                )
            }
        }
    }
}

@Composable
@MyDevices
private fun SettingsScreenPreview() {
    AppThemeSurface {
        SettingsScreen(
            displayLanguage = "English",
            isUseEnglishEnabled = false,
            isUseEnglishChecked = false,
            onUseEnglishPress = {},
            onSetupLanguagePress = {},
            goBack = {},
        )
    }
}
