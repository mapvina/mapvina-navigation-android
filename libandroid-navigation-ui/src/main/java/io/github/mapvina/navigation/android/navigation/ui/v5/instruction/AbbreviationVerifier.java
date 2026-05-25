package io.github.mapvina.navigation.android.navigation.ui.v5.instruction;

import android.text.TextUtils;

import io.github.mapvina.navigation.core.models.BannerComponents;

class AbbreviationVerifier implements NodeVerifier {
  @Override
  public boolean isNodeType(BannerComponents bannerComponents) {
    return hasAbbreviation(bannerComponents);
  }

  private boolean hasAbbreviation(BannerComponents components) {
    return !TextUtils.isEmpty(components.getAbbreviation());
  }
}
