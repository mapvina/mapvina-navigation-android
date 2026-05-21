package com.mapvina.navigation.android.navigation.ui.v5.notification.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.mapvina.navigation.android.navigation.ui.v5.notification.MapVinaNavigationNotification
import com.mapvina.navigation.android.navigation.ui.v5.notification.NavigationNotification
import com.mapvina.navigation.core.navigation.MapVinaNavigation


class NavigationNotificationServiceConnection(
    private val mapVinaNavigation: MapVinaNavigation,
    private val navigationNotification: NavigationNotification,
) : ServiceConnection {

    constructor(
        context: Context,
        mapVinaNavigation: MapVinaNavigation,
    ) : this(mapVinaNavigation, MapVinaNavigationNotification(context, mapVinaNavigation))

    private var serviceBinder: NavigationNotificationService.LocalBinder? = null

    fun start(context: Context) {
        val intent = Intent(context, NavigationNotificationService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun stop(context: Context) {
        context.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        (service as NavigationNotificationService.LocalBinder).also { serviceBinder ->
            this.serviceBinder = serviceBinder

            serviceBinder.service.navigationNotification = navigationNotification
            mapVinaNavigation.addNavigationEventListener(serviceBinder.service)
            mapVinaNavigation.addProgressChangeListener(serviceBinder.service)
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        serviceBinder?.let { serviceBinder ->
            mapVinaNavigation.removeNavigationEventListener(serviceBinder.service)
            mapVinaNavigation.removeProgressChangeListener(serviceBinder.service)
        }

        serviceBinder = null
    }
}