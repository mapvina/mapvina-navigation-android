package io.github.mapvina.navigation.android.navigation.ui.v5.route;

import io.github.mapvina.geojson.FeatureCollection;

import java.util.List;

interface OnPrimaryRouteUpdatedCallback {
  void onPrimaryRouteUpdated(List<FeatureCollection> updatedRouteCollections);
}
