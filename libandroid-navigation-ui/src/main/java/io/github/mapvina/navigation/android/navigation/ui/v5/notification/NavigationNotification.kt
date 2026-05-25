package io.github.mapvina.navigation.android.navigation.ui.v5.notification

import android.app.Notification
import android.content.Context
import io.github.mapvina.navigation.core.routeprogress.RouteProgress
import io.github.mapvina.navigation.core.navigation.MapVinaNavigation
import io.github.mapvina.navigation.core.navigation.MapVinaNavigationOptions

/**
 * Defines a contract in which a custom notification must adhere to when
 * given to [MapVinaNavigationOptions].
 */
interface NavigationNotification {

    /**
     * Provides a custom [Notification] to launch
     * with the [NavigationService], specifically
     * [android.app.Service.startForeground].
     *
     * @return a custom notification
     */
    fun getNotification(): Notification

    /**
     * An integer id that will be used to start this notification
     * from [NavigationService] with
     * [android.app.Service.startForeground].
     *
     * @return an int id specific to the notification
     */
    fun getNotificationId(): Int

    /**
     * If enabled, this method will be called every time a new [RouteProgress] is generated.
     *
     *
     * This method can serve as a cue to update a [Notification] with a specific notification id.
     *
     * @param routeProgress with the latest progress data
     */
    fun updateNotification(routeProgress: RouteProgress)

    /**
     * Callback for when navigation is stopped via [MapVinaNavigation.stopNavigation].
     *
     *
     * This callback may be used to clean up any listeners or receivers, preventing leaks.
     *
     * @param context to be used if needed for Android-related work
     */
    fun onNavigationStopped(context: Context)

    companion object {
        const val END_NAVIGATION_ACTION: String =
            "io.github.mapvina.navigation.android.intent.action.END_NAVIGATION"
        const val OPEN_NAVIGATION_ACTION: String =
            "io.github.mapvina.navigation.android.intent.action.OPEN_NAVIGATION"
    }
}
