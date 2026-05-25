package io.github.mapvina.navigation.android.navigation.ui.v5;

import io.github.mapvina.navigation.core.models.DirectionsRoute;
import io.github.mapvina.spatialk.geojson.Point;


class NavigationViewRouteEngineListener implements ViewRouteListener {

  private final NavigationViewModel navigationViewModel;

  NavigationViewRouteEngineListener(NavigationViewModel navigationViewModel) {
    this.navigationViewModel = navigationViewModel;
  }

  @Override
  public void onRouteUpdate(DirectionsRoute directionsRoute) {
    navigationViewModel.updateRoute(directionsRoute);
  }

  @Override
  public void onRouteRequestError(String errorMessage) {
    if (navigationViewModel.isOffRoute()) {
      navigationViewModel.sendEventFailedReroute(errorMessage);
    }
  }

  @Override
  public void onDestinationSet(Point destination) {
    navigationViewModel.retrieveDestination().setValue(destination);
  }
}
