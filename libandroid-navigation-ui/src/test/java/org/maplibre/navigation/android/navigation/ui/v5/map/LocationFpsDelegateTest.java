package com.mapvina.navigation.android.navigation.ui.v5.map;

import com.mapvina.android.camera.CameraPosition;
import com.mapvina.android.location.LocationComponent;
import com.mapvina.android.maps.MapVinaMap;

import org.junit.Test;
import com.mapvina.navigation.android.navigation.ui.v5.map.LocationFpsDelegate;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationFpsDelegateTest {

  @Test
  public void onCameraIdle_newFpsIsSetZoom16() {
    double zoom = 16d;
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    when(mapVinaMap.getCameraPosition()).thenReturn(buildCameraPosition(zoom));
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.onCameraIdle();

    verify(locationComponent).setMaxAnimationFps(eq(25));
  }

  @Test
  public void onCameraIdle_newFpsIsSetZoom14() {
    double zoom = 14d;
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    when(mapVinaMap.getCameraPosition()).thenReturn(buildCameraPosition(zoom));
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.onCameraIdle();

    verify(locationComponent).setMaxAnimationFps(eq(15));
  }

  @Test
  public void onCameraIdle_newFpsIsSetZoom10() {
    double zoom = 10d;
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    when(mapVinaMap.getCameraPosition()).thenReturn(buildCameraPosition(zoom));
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.onCameraIdle();

    verify(locationComponent).setMaxAnimationFps(eq(10));
  }

  @Test
  public void onCameraIdle_newFpsIsSet5() {
    double zoom = 5d;
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    when(mapVinaMap.getCameraPosition()).thenReturn(buildCameraPosition(zoom));
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.onCameraIdle();

    verify(locationComponent).setMaxAnimationFps(eq(5));
  }

  @Test
  public void onStart_idleListenerAdded() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.onStart();

    verify(mapVinaMap, times(2)).addOnCameraIdleListener(eq(locationFpsDelegate));
  }

  @Test
  public void onStop_idleListenerRemoved() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.onStop();

    verify(mapVinaMap).removeOnCameraIdleListener(eq(locationFpsDelegate));
  }

  @Test
  public void updateEnabled_falseResetsToMax() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.updateEnabled(false);

    verify(locationComponent).setMaxAnimationFps(eq(Integer.MAX_VALUE));
  }

  @Test
  public void isEnabled_returnsFalseWhenSet() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    LocationFpsDelegate locationFpsDelegate = new LocationFpsDelegate(mapVinaMap, locationComponent);

    locationFpsDelegate.updateEnabled(false);

    assertFalse(locationFpsDelegate.isEnabled());
  }

  private CameraPosition buildCameraPosition(double zoom) {
    return new CameraPosition.Builder().zoom(zoom).build();
  }
}