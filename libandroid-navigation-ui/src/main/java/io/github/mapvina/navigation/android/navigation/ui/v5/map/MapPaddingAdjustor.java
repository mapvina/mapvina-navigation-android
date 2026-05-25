package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import android.content.Context;
import android.content.res.Resources;

import io.github.mapvina.android.maps.MapView;
import io.github.mapvina.android.maps.MapVinaMap;
import io.github.mapvina.navigation.android.navigation.ui.v5.R;

class MapPaddingAdjustor {

  private static final int BOTTOMSHEET_PADDING_MULTIPLIER = 4;
  private static final int WAYNAME_PADDING_MULTIPLIER = 2;

  private final MapVinaMap mapVinaMap;
  private final int[] defaultPadding;
  private int[] customPadding;

  MapPaddingAdjustor(MapView mapView, MapVinaMap mapVinaMap) {
    this.mapVinaMap = mapVinaMap;
    defaultPadding = calculateDefaultPadding(mapView);
  }

  // Testing only
  MapPaddingAdjustor(MapVinaMap mapVinaMap, int[] defaultPadding) {
    this.mapVinaMap = mapVinaMap;
    this.defaultPadding = defaultPadding;
  }

  void updatePaddingWithDefault() {
    customPadding = null;
    updatePaddingWith(defaultPadding);
  }

  void adjustLocationIconWith(int[] customPadding) {
    this.customPadding = customPadding;
    updatePaddingWith(customPadding);
  }

  int[] retrieveCurrentPadding() {
    return mapVinaMap.getPadding();
  }

  boolean isUsingDefault() {
    return customPadding == null;
  }

  void updatePaddingWith(int[] padding) {
    mapVinaMap.setPadding(padding[0], padding[1], padding[2], padding[3]);
  }

  void resetPadding() {
    if (isUsingDefault()) {
      updatePaddingWithDefault();
    } else {
      adjustLocationIconWith(customPadding);
    }
  }

  private int[] calculateDefaultPadding(MapView mapView) {
    int defaultTopPadding = calculateTopPaddingWithoutWayname(mapView);
    Resources resources = mapView.getContext().getResources();
    int waynameLayoutHeight = (int) resources.getDimension(R.dimen.wayname_view_height);
    int topPadding = defaultTopPadding - (waynameLayoutHeight * WAYNAME_PADDING_MULTIPLIER);
    return new int[] {0, topPadding, 0, 0};
  }

  private int calculateTopPaddingWithoutWayname(MapView mapView) {
    Context context = mapView.getContext();
    Resources resources = context.getResources();
    int mapViewHeight = mapView.getHeight();
    int bottomSheetHeight = (int) resources.getDimension(R.dimen.summary_bottomsheet_height);
    return mapViewHeight - (bottomSheetHeight * BOTTOMSHEET_PADDING_MULTIPLIER);
  }
}
