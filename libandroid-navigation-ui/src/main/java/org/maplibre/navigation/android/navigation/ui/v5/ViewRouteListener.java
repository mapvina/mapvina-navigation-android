package com.mapvina.navigation.android.navigation.ui.v5;

import com.mapvina.navigation.core.models.DirectionsRoute;
import com.mapvina.geojson.Point;

public interface ViewRouteListener {

  void onRouteUpdate(DirectionsRoute directionsRoute);

  void onRouteRequestError(String errorMessage);

  void onDestinationSet(Point destination);
}
