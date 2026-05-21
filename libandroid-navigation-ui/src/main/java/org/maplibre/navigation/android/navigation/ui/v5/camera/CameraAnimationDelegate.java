package com.mapvina.navigation.android.navigation.ui.v5.camera;

import com.mapvina.android.camera.CameraUpdate;
import com.mapvina.android.location.modes.CameraMode;
import com.mapvina.android.maps.MapVinaMap;

class CameraAnimationDelegate {

  private final MapVinaMap mapVinaMap;

  CameraAnimationDelegate(MapVinaMap mapVinaMap) {
    this.mapVinaMap = mapVinaMap;
  }

  void render(NavigationCameraUpdate update, int durationMs, MapVinaMap.CancelableCallback callback) {
    CameraUpdateMode mode = update.getMode();
    CameraUpdate cameraUpdate = update.getCameraUpdate();
    if (mode == CameraUpdateMode.OVERRIDE) {
      mapVinaMap.getLocationComponent().setCameraMode(CameraMode.NONE);
      mapVinaMap.animateCamera(cameraUpdate, durationMs, callback);
    } else if (!isTracking()) {
      mapVinaMap.animateCamera(cameraUpdate, durationMs, callback);
    }
  }

  private boolean isTracking() {
    int cameraMode = mapVinaMap.getLocationComponent().getCameraMode();
    return cameraMode != CameraMode.NONE;
  }
}