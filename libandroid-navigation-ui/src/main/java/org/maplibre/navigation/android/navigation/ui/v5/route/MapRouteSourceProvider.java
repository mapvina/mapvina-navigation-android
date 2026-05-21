package com.mapvina.navigation.android.navigation.ui.v5.route;

import com.mapvina.geojson.FeatureCollection;
import com.mapvina.android.style.sources.GeoJsonOptions;
import com.mapvina.android.style.sources.GeoJsonSource;

class MapRouteSourceProvider {

  GeoJsonSource build(String id, FeatureCollection featureCollection, GeoJsonOptions options) {
    return new GeoJsonSource(id, featureCollection, options);
  }
}
