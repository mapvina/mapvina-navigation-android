package io.github.mapvina.navigation.android.navigation.ui.v5.route;

import io.github.mapvina.geojson.FeatureCollection;
import io.github.mapvina.android.style.sources.GeoJsonOptions;
import io.github.mapvina.android.style.sources.GeoJsonSource;

class MapRouteSourceProvider {

  GeoJsonSource build(String id, FeatureCollection featureCollection, GeoJsonOptions options) {
    return new GeoJsonSource(id, featureCollection, options);
  }
}
