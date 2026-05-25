package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import android.graphics.PointF;

import io.github.mapvina.geojson.Feature;
import io.github.mapvina.android.maps.MapVinaMap;

import java.util.List;

class WaynameFeatureFinder {

  private MapVinaMap mapVinaMap;

  WaynameFeatureFinder(MapVinaMap mapVinaMap) {
    this.mapVinaMap = mapVinaMap;
  }

  List<Feature> queryRenderedFeatures(PointF point, String[] layerIds) {
    return mapVinaMap.queryRenderedFeatures(point, layerIds);
  }
}
