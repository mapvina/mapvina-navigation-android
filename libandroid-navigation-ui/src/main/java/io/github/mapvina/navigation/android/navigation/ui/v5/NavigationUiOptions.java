package io.github.mapvina.navigation.android.navigation.ui.v5;

import androidx.annotation.Nullable;

import io.github.mapvina.navigation.core.models.DirectionsRoute;

public abstract class NavigationUiOptions {

  public abstract DirectionsRoute directionsRoute();

  @Nullable
  public abstract Integer lightThemeResId();

  @Nullable
  public abstract Integer darkThemeResId();

  public abstract boolean shouldSimulateRoute();

  public abstract boolean waynameChipEnabled();
}
