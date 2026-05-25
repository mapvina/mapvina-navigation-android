package io.github.mapvina.navigation.android.navigation.ui.v5;

import io.github.mapvina.navigation.core.location.Location;
import io.github.mapvina.navigation.core.routeprogress.ProgressChangeListener;
import io.github.mapvina.navigation.core.routeprogress.RouteProgress;

class NavigationViewModelProgressChangeListener implements ProgressChangeListener {

  private final NavigationViewModel viewModel;

  NavigationViewModelProgressChangeListener(NavigationViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void onProgressChange(Location location, RouteProgress routeProgress) {
    viewModel.updateRouteProgress(routeProgress);
    viewModel.updateLocation(location);
  }
}