package org.fossify.thankyou.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import org.fossify.thankyou.R
import org.fossify.thankyou.activities.MainActivity
import org.fossify.thankyou.helpers.ThemeSyncManager

class ThemeSyncService : Service() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val delayedRefresh = Runnable { ThemeSyncManager.apply(this) }

    private val wallpaperColorsListener = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        WallpaperManager.OnColorsChangedListener { _: WallpaperColors?, _: Int ->
            refreshThemeWithFollowUp()
        }
    } else {
        null
    }

    private val changesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (
                intent?.action == Intent.ACTION_WALLPAPER_CHANGED ||
                intent?.action == Intent.ACTION_CONFIGURATION_CHANGED
            ) {
                refreshThemeWithFollowUp()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        promoteToForeground()
        registerSystemChanges()
        registerWallpaperColorsListener()
        refreshThemeWithFollowUp()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        refreshThemeWithFollowUp()
        return START_STICKY
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(delayedRefresh)
        runCatching { unregisterReceiver(changesReceiver) }
        unregisterWallpaperColorsListener()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @Suppress("DEPRECATION")
    private fun registerSystemChanges() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_WALLPAPER_CHANGED)
            addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(changesReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(changesReceiver, filter)
        }
    }

    private fun registerWallpaperColorsListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            wallpaperColorsListener?.let { listener ->
                WallpaperManager.getInstance(this)
                    .addOnColorsChangedListener(listener, mainHandler)
            }
        }
    }

    private fun unregisterWallpaperColorsListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            wallpaperColorsListener?.let { listener ->
                runCatching {
                    WallpaperManager.getInstance(this).removeOnColorsChangedListener(listener)
                }
            }
        }
    }

    private fun refreshThemeWithFollowUp() {
        ThemeSyncManager.apply(this)

        // Pixel/Monet resources can update a little after the wallpaper callback. Re-read once
        // more after a short delay so the global Fossify config receives the final palette.
        mainHandler.removeCallbacks(delayedRefresh)
        mainHandler.postDelayed(delayedRefresh, PALETTE_SETTLE_DELAY_MS)
    }

    private fun promoteToForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setSmallIcon(R.mipmap.ic_launcher_grey_black)
            .setContentTitle(getString(R.string.live_sync_notification_title))
            .setContentText(getString(R.string.live_sync_notification_text))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.live_sync_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.live_sync_channel_description)
            setShowBadge(false)
        }

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "fossify_theming_live_sync"
        private const val NOTIFICATION_ID = 42017
        private const val PALETTE_SETTLE_DELAY_MS = 1500L

        fun setEnabled(context: Context, enabled: Boolean) {
            val intent = Intent(context, ThemeSyncService::class.java)
            if (enabled) {
                context.startForegroundService(intent)
            } else {
                context.stopService(intent)
            }
        }
    }
}
