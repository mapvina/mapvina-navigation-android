package com.mapvina.navigation.android.example;

import android.app.Application;

import com.mapvina.android.BuildConfig;
import com.mapvina.android.MapVina;

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
