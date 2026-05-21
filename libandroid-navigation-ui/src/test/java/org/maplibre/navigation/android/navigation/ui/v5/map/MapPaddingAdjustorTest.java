package com.mapvina.navigation.android.navigation.ui.v5.map;

import com.mapvina.android.maps.MapVinaMap;

import org.junit.Test;
import com.mapvina.navigation.android.navigation.ui.v5.map.MapPaddingAdjustor;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MapPaddingAdjustorTest {

  @Test
  public void adjustLocationIconWith_customPaddingIsSet() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};
    int[] customPadding = {0, 0, 0, 0};
    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);

    paddingAdjustor.adjustLocationIconWith(customPadding);

    verify(mapVinaMap).setPadding(0, 0, 0, 0);
  }

  @Test
  public void isUsingDefault_falseAfterCustomPaddingIsSet() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};
    int[] customPadding = {0, 0, 0, 0};
    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);

    paddingAdjustor.adjustLocationIconWith(customPadding);

    assertFalse(paddingAdjustor.isUsingDefault());
  }

  @Test
  public void isUsingDefault_trueWithoutCustomPadding() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};

    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);

    assertTrue(paddingAdjustor.isUsingDefault());
  }

  @Test
  public void updatePaddingWithZero_updatesMapToZeroPadding() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};
    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);

    paddingAdjustor.updatePaddingWith(new int[]{0, 0, 0, 0});

    verify(mapVinaMap).setPadding(0, 0, 0, 0);
  }

  @Test
  public void updatePaddingWithZero_retainsCustomPadding() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};
    int[] customPadding = {0, 350, 0, 0};
    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);
    paddingAdjustor.adjustLocationIconWith(customPadding);
    paddingAdjustor.updatePaddingWith(new int[]{0, 0, 0, 0});

    paddingAdjustor.resetPadding();

    verify(mapVinaMap, times(2)).setPadding(0, 350, 0, 0);
  }

  @Test
  public void updatePaddingWithDefault_defaultIsRestoredAfterCustom() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};
    int[] customPadding = {0, 0, 0, 0};
    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);
    paddingAdjustor.adjustLocationIconWith(customPadding);

    paddingAdjustor.updatePaddingWithDefault();

    verify(mapVinaMap).setPadding(0, 250, 0, 0);
  }

  @Test
  public void retrieveCurrentPadding_returnsCurrentMapPadding() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    int[] defaultPadding = {0, 250, 0, 0};
    MapPaddingAdjustor paddingAdjustor = new MapPaddingAdjustor(mapVinaMap, defaultPadding);

    paddingAdjustor.retrieveCurrentPadding();

    verify(mapVinaMap).getPadding();
  }
}