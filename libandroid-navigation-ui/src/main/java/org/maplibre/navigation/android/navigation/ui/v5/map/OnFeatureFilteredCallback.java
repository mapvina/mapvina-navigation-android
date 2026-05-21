package com.mapvina.navigation.android.navigation.ui.v5.map;

import androidx.annotation.NonNull;

import com.mapvina.geojson.Feature;

interface OnFeatureFilteredCallback {
  void onFeatureFiltered(@NonNull Feature feature);
}
