package com.mapvina.navigation.android.navigation.ui.v5.map;

import android.graphics.PointF;

import com.mapvina.android.maps.MapVinaMap;

import org.junit.Test;
import com.mapvina.navigation.android.navigation.ui.v5.map.WaynameFeatureFinder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WaynameFeatureFinderTest {

  @Test
  public void queryRenderedFeatures_mapVinaMapIsCalled() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    WaynameFeatureFinder featureFinder = new WaynameFeatureFinder(mapVinaMap);
    PointF point = mock(PointF.class);
    String[] layerIds = {"id", "id"};

    featureFinder.queryRenderedFeatures(point, layerIds);

    verify(mapVinaMap).queryRenderedFeatures(point, layerIds);
  }
}