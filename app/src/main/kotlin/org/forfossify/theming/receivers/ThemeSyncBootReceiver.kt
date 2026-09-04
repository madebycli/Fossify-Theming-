package org.forfossify.theming.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.forfossify.theming.helpers.Config
import org.forfossify.theming.helpers.ThemeSyncManager
import org.forfossify.theming.services.ThemeSyncService

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
