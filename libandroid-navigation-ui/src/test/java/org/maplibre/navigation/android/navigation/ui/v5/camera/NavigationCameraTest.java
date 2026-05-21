package com.mapvina.navigation.android.navigation.ui.v5.camera;

import com.mapvina.android.camera.CameraPosition;
import com.mapvina.android.camera.CameraUpdate;
import com.mapvina.android.location.LocationComponent;
import com.mapvina.android.location.OnLocationCameraTransitionListener;
import com.mapvina.android.location.modes.CameraMode;
import com.mapvina.android.maps.MapVinaMap;
import com.mapvina.navigation.android.navigation.ui.v5.BaseTest;

import com.mapvina.navigation.core.navigation.MapVinaNavigation;
import com.mapvina.navigation.core.navigation.camera.RouteInformation;
import com.mapvina.navigation.core.routeprogress.ProgressChangeListener;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NavigationCameraTest extends BaseTest {

  @Test
  public void sanity() {
    NavigationCamera camera = buildCamera();

    assertNotNull(camera);
  }

  @Test
  public void setTrackingEnabled_trackingIsEnabled() {
    LocationComponent locationComponent = mock(LocationComponent.class);
    NavigationCamera camera = buildCamera(locationComponent);

    verify(locationComponent, times(1)).setCameraMode(eq(CameraMode.TRACKING_GPS),
      any(OnLocationCameraTransitionListener.class));
    verify(locationComponent, times(0)).setCameraMode(eq(CameraMode.NONE),
      any(OnLocationCameraTransitionListener.class));

    camera.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE);
    verify(locationComponent, times(1)).setCameraMode(eq(CameraMode.NONE),
      any(OnLocationCameraTransitionListener.class));

    camera.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);
    verify(locationComponent, times(2)).setCameraMode(eq(CameraMode.TRACKING_GPS),
      any(OnLocationCameraTransitionListener.class));

    assertTrue(camera.isTrackingEnabled());
  }

  @Test
  public void setTrackingDisabled_trackingIsDisabled() {
    LocationComponent locationComponent = mock(LocationComponent.class);
    NavigationCamera camera = buildCamera(locationComponent);

    verify(locationComponent, times(1)).setCameraMode(eq(CameraMode.TRACKING_GPS),
      any(OnLocationCameraTransitionListener.class));
    verify(locationComponent, times(0)).setCameraMode(eq(CameraMode.NONE),
      any(OnLocationCameraTransitionListener.class));

    camera.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);
    verify(locationComponent, times(2)).setCameraMode(eq(CameraMode.TRACKING_GPS),
      any(OnLocationCameraTransitionListener.class));

    camera.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE);
    verify(locationComponent, times(1)).setCameraMode(eq(CameraMode.NONE),
      any(OnLocationCameraTransitionListener.class));

    assertFalse(camera.isTrackingEnabled());
  }

  @Test
  public void onResetCamera_trackingIsResumed() {
    NavigationCamera camera = buildCamera();

    camera.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE);
    camera.resetCameraPositionWith(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);

    assertTrue(camera.isTrackingEnabled());
  }

  @Test
  public void onResetCamera_dynamicCameraIsReset() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    when(mapVinaMap.getCameraPosition()).thenReturn(mock(CameraPosition.class));
    MapVinaNavigation navigation = mock(MapVinaNavigation.class);
    DynamicCamera dynamicCamera = mock(DynamicCamera.class);
    when(navigation.getCameraEngine()).thenReturn(dynamicCamera);
    RouteInformation currentRouteInformation = mock(RouteInformation.class);
    NavigationCamera camera = buildCamera(mapVinaMap, navigation, currentRouteInformation);

    camera.resetCameraPositionWith(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);

    verify(dynamicCamera).forceResetZoomLevel();
  }

  @Test
  public void onStartWithNullRoute_progressListenerIsAdded() {
    MapVinaNavigation navigation = mock(MapVinaNavigation.class);
    ProgressChangeListener listener = mock(ProgressChangeListener.class);
    NavigationCamera camera = buildCamera(navigation, listener);

    camera.start(null);

    verify(navigation, times(1)).addProgressChangeListener(listener);
  }

  @Test
  public void onResumeWithNullLocation_progressListenerIsAdded() {
    MapVinaNavigation navigation = mock(MapVinaNavigation.class);
    ProgressChangeListener listener = mock(ProgressChangeListener.class);
    NavigationCamera camera = buildCamera(navigation, listener);

    camera.resume(null);

    verify(navigation, times(1)).addProgressChangeListener(listener);
  }

  @Test
  public void update_defaultIsIgnoredWhileTracking() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    when(locationComponent.getCameraMode()).thenReturn(CameraMode.TRACKING_GPS);
    when(mapVinaMap.getLocationComponent()).thenReturn(locationComponent);
    CameraUpdate cameraUpdate = mock(CameraUpdate.class);
    MapVinaMap.CancelableCallback callback = mock(MapVinaMap.CancelableCallback.class);
    NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
    NavigationCamera camera = buildCamera(mapVinaMap);

    camera.update(navigationCameraUpdate, 300, callback);

    verify(mapVinaMap, times(0)).animateCamera(cameraUpdate);
  }

  @Test
  public void update_defaultIsAcceptedWithNoTracking() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    when(locationComponent.getCameraMode()).thenReturn(CameraMode.NONE);
    when(mapVinaMap.getLocationComponent()).thenReturn(locationComponent);
    CameraUpdate cameraUpdate = mock(CameraUpdate.class);
    MapVinaMap.CancelableCallback callback = mock(MapVinaMap.CancelableCallback.class);
    NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
    NavigationCamera camera = buildCamera(mapVinaMap);

    camera.update(navigationCameraUpdate, 300, callback);

    verify(mapVinaMap).animateCamera(eq(cameraUpdate), eq(300), eq(callback));
  }

  @Test
  public void update_overrideIsAcceptedWhileTracking() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    when(locationComponent.getCameraMode()).thenReturn(CameraMode.TRACKING_GPS);
    when(mapVinaMap.getLocationComponent()).thenReturn(locationComponent);
    CameraUpdate cameraUpdate = mock(CameraUpdate.class);
    MapVinaMap.CancelableCallback callback = mock(MapVinaMap.CancelableCallback.class);
    NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
    navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
    NavigationCamera camera = buildCamera(mapVinaMap);

    camera.update(navigationCameraUpdate, 300, callback);

    verify(mapVinaMap).animateCamera(eq(cameraUpdate), eq(300), eq(callback));
  }

  @Test
  public void update_overrideSetsLocationComponentCameraModeNone() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    LocationComponent locationComponent = mock(LocationComponent.class);
    when(locationComponent.getCameraMode()).thenReturn(CameraMode.TRACKING_GPS);
    when(mapVinaMap.getLocationComponent()).thenReturn(locationComponent);
    CameraUpdate cameraUpdate = mock(CameraUpdate.class);
    MapVinaMap.CancelableCallback callback = mock(MapVinaMap.CancelableCallback.class);
    NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
    navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
    NavigationCamera camera = buildCamera(mapVinaMap);

    camera.update(navigationCameraUpdate, 300, callback);

    verify(locationComponent).setCameraMode(eq(CameraMode.NONE));
  }

  private NavigationCamera buildCamera() {
    return new NavigationCamera(mock(MapVinaMap.class), mock(MapVinaNavigation.class), mock(LocationComponent.class));
  }

  private NavigationCamera buildCamera(MapVinaMap mapVinaMap) {
    return new NavigationCamera(mapVinaMap, mock(MapVinaNavigation.class), mock(LocationComponent.class));
  }

  private NavigationCamera buildCamera(LocationComponent locationComponent) {
    return new NavigationCamera(mock(MapVinaMap.class), mock(MapVinaNavigation.class), locationComponent);
  }

  private NavigationCamera buildCamera(MapVinaNavigation navigation, ProgressChangeListener listener) {
    return new NavigationCamera(mock(MapVinaMap.class), navigation, listener,
      mock(LocationComponent.class), mock(RouteInformation.class));
  }

  private NavigationCamera buildCamera(MapVinaMap mapVinaMap, MapVinaNavigation navigation,
                                       RouteInformation routeInformation) {
    return new NavigationCamera(mapVinaMap, navigation, mock(ProgressChangeListener.class),
      mock(LocationComponent.class), routeInformation);
  }
}
