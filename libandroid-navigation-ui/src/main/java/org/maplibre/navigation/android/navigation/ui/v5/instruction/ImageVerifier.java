package com.mapvina.navigation.android.navigation.ui.v5.instruction;

import android.text.TextUtils;

import com.mapvina.navigation.core.models.BannerComponents;

class ImageVerifier implements NodeVerifier {

  @Override
  public boolean isNodeType(BannerComponents bannerComponents) {
    return hasImageUrl(bannerComponents);
  }

  boolean hasImageUrl(BannerComponents components) {
    return !TextUtils.isEmpty(components.getImageBaseUrl());
  }
}
