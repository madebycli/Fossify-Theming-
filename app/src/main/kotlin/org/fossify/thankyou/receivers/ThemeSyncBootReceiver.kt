package org.fossify.thankyou.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.fossify.thankyou.helpers.Config
import org.fossify.thankyou.helpers.ThemeSyncManager
import org.fossify.thankyou.services.ThemeSyncService

class ThemeSyncBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (
            intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val settings = Config.newInstance(context).getThemeSettings()
            ThemeSyncManager.apply(context, settings)
            if (settings.liveSyncEnabled) {
                runCatching { ThemeSyncService.setEnabled(context, true) }
            }
        }
    }
}
