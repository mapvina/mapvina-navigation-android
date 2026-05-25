package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import static io.github.mapvina.navigation.android.navigation.ui.v5.map.NavigationSymbolManager.MAPVINA_NAVIGATION_MARKER_NAME;

import android.graphics.Bitmap;

import io.github.mapvina.android.maps.MapView;
import io.github.mapvina.android.maps.MapVinaMap;

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
