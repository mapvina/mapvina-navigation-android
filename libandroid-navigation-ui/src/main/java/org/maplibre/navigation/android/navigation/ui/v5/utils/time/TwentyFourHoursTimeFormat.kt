package com.mapvina.navigation.android.navigation.ui.v5.utils.time

import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import java.util.Calendar
import java.util.Locale


open class TwentyFourHoursTimeFormat : TimeFormatResolver {
    private var chain: TimeFormatResolver? = null

    override fun nextChain(chain: TimeFormatResolver?) {
        this.chain = chain
    }

    override fun obtainTimeFormatted(
        type: MapVinaNavigationOptions.TimeFormat,
        time: Calendar
    ): String? {
        return if (type == MapVinaNavigationOptions.TimeFormat.TWENTY_FOUR_HOURS) {
            String.format(
                Locale.getDefault(),
                TWENTY_FOUR_HOURS_FORMAT,
                time,
                time
            )
        } else {
            chain?.obtainTimeFormatted(type, time)
        }
    }

    companion object {
        const val TWENTY_FOUR_HOURS_FORMAT: String = "%tk:%tM"
    }
}
