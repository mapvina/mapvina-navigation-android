package com.mapvina.navigation.android.navigation.ui.v5.route;

import com.mapvina.geojson.FeatureCollection;

import java.util.List;

interface OnPrimaryRouteUpdatedCallback {
  void onPrimaryRouteUpdated(List<FeatureCollection> updatedRouteCollections);
}
