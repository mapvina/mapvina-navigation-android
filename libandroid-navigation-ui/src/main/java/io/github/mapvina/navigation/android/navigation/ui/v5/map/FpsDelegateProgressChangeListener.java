package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import io.github.mapvina.navigation.core.location.Location;

import io.github.mapvina.navigation.core.routeprogress.ProgressChangeListener;
import io.github.mapvina.navigation.core.routeprogress.RouteProgress;

class FpsDelegateProgressChangeListener implements ProgressChangeListener {

  private final MapFpsDelegate fpsDelegate;

  FpsDelegateProgressChangeListener(MapFpsDelegate fpsDelegate) {
    this.fpsDelegate = fpsDelegate;
  }

  @Override
  public void onProgressChange(Location location, RouteProgress routeProgress) {
    fpsDelegate.adjustFpsFor(routeProgress);
  }
}
