package io.github.mapvina.navigation.android.navigation.ui.v5.instruction;

import io.github.mapvina.navigation.core.models.BannerComponents;

class TextVerifier implements NodeVerifier {
  @Override
  public boolean isNodeType(BannerComponents bannerComponents) {
    return bannerComponents.getText() != null && !bannerComponents.getText().isEmpty();
  }
}
