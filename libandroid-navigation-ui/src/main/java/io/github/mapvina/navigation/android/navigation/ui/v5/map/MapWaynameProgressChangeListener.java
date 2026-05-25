package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import static io.github.mapvina.navigation.android.navigation.ui.v5.GeoJsonExtKt.toMapVinaPoints;

import io.github.mapvina.navigation.core.location.Location;

import io.github.mapvina.navigation.core.routeprogress.ProgressChangeListener;
import io.github.mapvina.navigation.core.routeprogress.RouteProgress;

class MapWaynameProgressChangeListener implements ProgressChangeListener {

  private final MapWayName mapWayName;

  MapWaynameProgressChangeListener(MapWayName mapWayName) {
    this.mapWayName = mapWayName;
  }

  @Override
  public void onProgressChange(Location location, RouteProgress routeProgress) {
    mapWayName.updateProgress(location, toMapVinaPoints(routeProgress.getCurrentStepPoints()));
  }
}
