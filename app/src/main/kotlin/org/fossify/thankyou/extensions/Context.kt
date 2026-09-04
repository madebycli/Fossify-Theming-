package org.fossify.thankyou.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import org.fossify.commons.extensions.openDeviceSettings
import org.fossify.thankyou.helpers.Config

val Context.config: Config get() = Config.newInstance(applicationContext)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal fun Activity.launchChangeAppLanguageIntent() {
    try {
        Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    } catch (_: Exception) {
        openDeviceSettings()
    }
}
