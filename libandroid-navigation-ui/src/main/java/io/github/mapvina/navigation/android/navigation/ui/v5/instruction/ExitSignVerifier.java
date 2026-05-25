package io.github.mapvina.navigation.android.navigation.ui.v5.instruction;

import io.github.mapvina.navigation.core.models.BannerComponents;

class ExitSignVerifier implements NodeVerifier {

  @Override
  public boolean isNodeType(BannerComponents bannerComponents) {
    return bannerComponents.getType().equals("exit") || bannerComponents.getType().getText().equals("exit-number");
  }
}
