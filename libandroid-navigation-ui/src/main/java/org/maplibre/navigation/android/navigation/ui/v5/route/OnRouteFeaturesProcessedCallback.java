package com.mapvina.navigation.android.navigation.ui.v5.route;

import com.mapvina.navigation.core.models.DirectionsRoute;
import com.mapvina.geojson.FeatureCollection;
import com.mapvina.geojson.LineString;

import java.util.HashMap;
import java.util.List;

interface OnRouteFeaturesProcessedCallback {
  void onRouteFeaturesProcessed(List<FeatureCollection> routeFeatureCollections,
                                HashMap<LineString, DirectionsRoute> routeLineStrings);
}
