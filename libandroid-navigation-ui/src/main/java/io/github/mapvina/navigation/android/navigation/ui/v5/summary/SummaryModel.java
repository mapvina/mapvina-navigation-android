package io.github.mapvina.navigation.android.navigation.ui.v5.summary;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;

import io.github.mapvina.navigation.core.navigation.MapVinaNavigationOptions;
import io.github.mapvina.navigation.core.routeprogress.RouteProgress;
import io.github.mapvina.navigation.android.navigation.ui.v5.utils.DistanceFormatter;

import java.util.Calendar;

import static io.github.mapvina.navigation.android.navigation.ui.v5.utils.time.TimeFormatter.formatTime;
import static io.github.mapvina.navigation.android.navigation.ui.v5.utils.time.TimeFormatter.formatTimeRemaining;

public class SummaryModel {

  private final String distanceRemaining;
  private final SpannableStringBuilder timeRemaining;
  private final String arrivalTime;

  public SummaryModel(Context context, DistanceFormatter distanceFormatter, RouteProgress progress, MapVinaNavigationOptions.TimeFormat timeFormatType) {
    distanceRemaining = distanceFormatter.formatDistance(progress.getDistanceRemaining()).toString();
    double legDurationRemaining = progress.getCurrentLegProgress().getDurationRemaining();
    timeRemaining = formatTimeRemaining(context, legDurationRemaining);
    Calendar time = Calendar.getInstance();
    boolean isTwentyFourHourFormat = DateFormat.is24HourFormat(context);
    arrivalTime = formatTime(time, legDurationRemaining, timeFormatType, isTwentyFourHourFormat);
  }

  String getDistanceRemaining() {
    return distanceRemaining;
  }

  SpannableStringBuilder getTimeRemaining() {
    return timeRemaining;
  }

  String getArrivalTime() {
    return arrivalTime;
  }
}
