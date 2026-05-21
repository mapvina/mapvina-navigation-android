package com.mapvina.navigation.android.navigation.ui.v5.map;

import static com.mapvina.navigation.android.navigation.ui.v5.map.NavigationSymbolManager.MAPVINA_NAVIGATION_MARKER_NAME;

import android.graphics.Bitmap;

import com.mapvina.android.maps.MapView;
import com.mapvina.android.maps.MapVinaMap;

class SymbolOnStyleLoadedListener implements MapView.OnDidFinishLoadingStyleListener {

  private final MapVinaMap mapVinaMap;
  private final Bitmap markerBitmap;

  SymbolOnStyleLoadedListener(MapVinaMap mapVinaMap, Bitmap markerBitmap) {
    this.mapVinaMap = mapVinaMap;
    this.markerBitmap = markerBitmap;
  }

  @Override
  public void onDidFinishLoadingStyle() {
    mapVinaMap.getStyle().addImage(MAPVINA_NAVIGATION_MARKER_NAME, markerBitmap);
  }
}
