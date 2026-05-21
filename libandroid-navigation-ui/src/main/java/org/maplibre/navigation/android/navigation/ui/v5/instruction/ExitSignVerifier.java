package com.mapvina.navigation.android.navigation.ui.v5.instruction;

import com.mapvina.navigation.core.models.BannerComponents;

class ExitSignVerifier implements NodeVerifier {

  @Override
  public boolean isNodeType(BannerComponents bannerComponents) {
    return bannerComponents.getType().equals("exit") || bannerComponents.getType().getText().equals("exit-number");
  }
}
