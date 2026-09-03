package org.fossify.thankyou.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fossify.commons.activities.BaseComposeActivity
import org.fossify.commons.compose.extensions.enableEdgeToEdgeSimple
import org.fossify.commons.extensions.hideKeyboard
import org.fossify.commons.extensions.toast
import org.fossify.thankyou.BuildConfig
import org.fossify.thankyou.R
import org.fossify.thankyou.extensions.config
import org.fossify.thankyou.extensions.getAllFossifyApps
import org.fossify.thankyou.extensions.getFakeFossifyApps
import org.fossify.thankyou.extensions.getFossifyAppsFlow
import org.fossify.thankyou.helpers.ThemeSyncManager
import org.fossify.thankyou.models.ThemeSettings
import org.fossify.thankyou.services.ThemeSyncService
import org.fossify.thankyou.ui.screens.MainScreen
import org.fossify.thankyou.ui.theme.ThemingThemeSurface

class MainActivity : BaseComposeActivity() {
    private val preferences by lazy { config }
    private val allAppsFlow by lazy { getFossifyAppsFlow(::getAllFossifyApps) }
    private val fakeAppsFlow by lazy { getFossifyAppsFlow(::getFakeFossifyApps) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()

        val initialSettings = preferences.getThemeSettings()
        ThemeSyncManager.apply(this, initialSettings)
        if (initialSettings.liveSyncEnabled) {
            runCatching { ThemeSyncService.setEnabled(this, true) }
        }

        setContent {
            var settings by remember { mutableStateOf(preferences.getThemeSettings()) }
            var profiles by remember { mutableStateOf(preferences.getThemeProfiles()) }
            var systemColors by remember {
                mutableStateOf(ThemeSyncManager.getSystemColors(this@MainActivity))
            }

            ThemingThemeSurface(settings = settings) {
                val allApps by allAppsFlow.collectAsStateWithLifecycle(listOf())
                val fakeApps by fakeAppsFlow.collectAsStateWithLifecycle(listOf())

                fun persistAndApply(updated: ThemeSettings) {
                    val liveSyncChanged = settings.liveSyncEnabled != updated.liveSyncEnabled
                    settings = updated
                    preferences.saveThemeSettings(updated)
                    ThemeSyncManager.apply(this@MainActivity, updated)
                    systemColors = ThemeSyncManager.getSystemColors(this@MainActivity)

                    if (liveSyncChanged) {
                        runCatching {
                            ThemeSyncService.setEnabled(
                                this@MainActivity,
                                updated.liveSyncEnabled,
                            )
                        }
                    }
                }

                MainScreen(
                    allApps = allApps,
                    fakeApps = fakeApps,
                    settings = settings,
                    profiles = profiles,
                    systemColors = systemColors,
                    stockCompatibilityMode = BuildConfig.THEME_PROVIDER_AUTHORITY ==
                        STOCK_PROVIDER_AUTHORITY,
                    onSettingsChanged = ::persistAndApply,
                    onApplyNow = {
                        val applied = ThemeSyncManager.apply(this@MainActivity, settings)
                        systemColors = ThemeSyncManager.getSystemColors(this@MainActivity)
                        toast(
                            if (applied) {
                                R.string.theme_applied
                            } else {
                                R.string.theme_apply_failed
                            }
                        )
                    },
                    onSaveProfile = { name ->
                        preferences.saveThemeProfile(name, settings)
                        profiles = preferences.getThemeProfiles()
                        toast(R.string.profile_saved)
                    },
                    onLoadProfile = { profile ->
                        persistAndApply(profile.settings)
                        toast(R.string.profile_loaded)
                    },
                    onDeleteProfile = { profile ->
                        preferences.deleteThemeProfile(profile.name)
                        profiles = preferences.getThemeProfiles()
                    },
                    openSettings = ::launchSettings,
                    launchApp = ::launchApp,
                    uninstallApp = ::uninstallApp,
                )
            }
        }
    }

    private fun launchSettings() {
        hideKeyboard()
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun launchApp(packageName: String) {
        if (packageName == this.packageName) {
            toast(org.fossify.commons.R.string.hello)
        } else {
            packageManager.getLaunchIntentForPackage(packageName)?.let(::startActivity)
        }
    }

    private fun uninstallApp(packageName: String) {
        Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", packageName, null)
            @Suppress("DEPRECATION")
            startActivityForResult(this, UNINSTALL_APP_REQUEST_CODE)
        }
    }

    companion object {
        private const val UNINSTALL_APP_REQUEST_CODE = 50
        private const val STOCK_PROVIDER_AUTHORITY = "org.fossify.android.provider"
    }
}
