package io.github.mapvina.navigation.android.navigation.ui.v5;

import io.github.mapvina.navigation.core.models.DirectionsRoute;
import io.github.mapvina.spatialk.geojson.Point;

public interface ViewRouteListener {

  void onRouteUpdate(DirectionsRoute directionsRoute);

  void onRouteRequestError(String errorMessage);

  void onDestinationSet(Point destination);
}
