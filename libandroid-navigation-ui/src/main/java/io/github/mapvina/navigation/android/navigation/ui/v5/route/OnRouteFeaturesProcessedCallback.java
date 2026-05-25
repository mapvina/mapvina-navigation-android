package io.github.mapvina.navigation.android.navigation.ui.v5.route;

import io.github.mapvina.navigation.core.models.DirectionsRoute;
import io.github.mapvina.geojson.FeatureCollection;
import io.github.mapvina.geojson.LineString;

import java.util.HashMap;
import java.util.List;

interface OnRouteFeaturesProcessedCallback {
  void onRouteFeaturesProcessed(List<FeatureCollection> routeFeatureCollections,
                                HashMap<LineString, DirectionsRoute> routeLineStrings);
}
