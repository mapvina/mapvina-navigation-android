package io.github.mapvina.navigation.android.example;

import android.app.Application;

import io.github.mapvina.android.BuildConfig;
import io.github.mapvina.android.MapVina;

import timber.log.Timber;

public class NavigationApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    MapVina.getInstance(getApplicationContext());
  }

}
