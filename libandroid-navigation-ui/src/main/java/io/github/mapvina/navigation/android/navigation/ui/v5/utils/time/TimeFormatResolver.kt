package io.github.mapvina.navigation.android.navigation.ui.v5.utils.time

import io.github.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import java.util.Calendar


interface TimeFormatResolver {
    fun nextChain(chain: TimeFormatResolver?)

    fun obtainTimeFormatted(type: MapVinaNavigationOptions.TimeFormat, time: Calendar): String?
}