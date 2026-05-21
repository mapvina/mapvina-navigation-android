package com.mapvina.navigation.android.navigation.ui.v5.camera;

import com.mapvina.android.camera.CameraUpdate;
import com.mapvina.android.maps.MapVinaMap;

class CameraOverviewCancelableCallback implements MapVinaMap.CancelableCallback {

  private static final int OVERVIEW_UPDATE_DURATION_IN_MILLIS = 750;

  private CameraUpdate overviewUpdate;
  private MapVinaMap mapVinaMap;

  CameraOverviewCancelableCallback(CameraUpdate overviewUpdate, MapVinaMap mapVinaMap) {
    this.overviewUpdate = overviewUpdate;
    this.mapVinaMap = mapVinaMap;
  }

  @Override
  public void onCancel() {
    // No-op
  }

  @Override
  public void onFinish() {
    mapVinaMap.animateCamera(overviewUpdate, OVERVIEW_UPDATE_DURATION_IN_MILLIS);
  }
}
