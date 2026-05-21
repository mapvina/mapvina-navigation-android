package com.mapvina.navigation.android.navigation.ui.v5.map;

import com.mapvina.navigation.core.location.Location;

import com.mapvina.navigation.core.routeprogress.ProgressChangeListener;
import com.mapvina.navigation.core.routeprogress.RouteProgress;

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
