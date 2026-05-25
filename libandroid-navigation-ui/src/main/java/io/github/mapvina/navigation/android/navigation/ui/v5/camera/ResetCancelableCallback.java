package io.github.mapvina.navigation.android.navigation.ui.v5.camera;

import io.github.mapvina.android.maps.MapVinaMap;

class ResetCancelableCallback implements MapVinaMap.CancelableCallback {

  private final NavigationCamera camera;

  ResetCancelableCallback(NavigationCamera camera) {
    this.camera = camera;
  }

  @Override
  public void onCancel() {
    camera.updateIsResetting(false);
  }

  @Override
  public void onFinish() {
    camera.updateIsResetting(false);
  }
}