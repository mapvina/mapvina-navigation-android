package com.mapvina.navigation.android.navigation.ui.v5.map;

import android.graphics.PointF;

import com.mapvina.geojson.Feature;
import com.mapvina.android.maps.MapVinaMap;

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
