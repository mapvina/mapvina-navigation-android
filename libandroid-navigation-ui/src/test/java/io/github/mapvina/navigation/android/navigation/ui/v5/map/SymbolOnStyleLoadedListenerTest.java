package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import android.graphics.Bitmap;

import io.github.mapvina.android.maps.MapVinaMap;
import io.github.mapvina.android.maps.Style;

import org.junit.Test;
import io.github.mapvina.navigation.android.navigation.ui.v5.map.SymbolOnStyleLoadedListener;

import static io.github.mapvina.navigation.android.navigation.ui.v5.map.NavigationSymbolManager.MAPVINA_NAVIGATION_MARKER_NAME;
import static io.github.mapvina.navigation.android.navigation.ui.v5.map.NavigationSymbolManager.MAPVINA_NAVIGATION_MARKER_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SymbolOnStyleLoadedListenerTest {

  @Test
  public void onDidFinishLoadingStyle_markerIsAdded() {
    MapVinaMap mapVinaMap = mock(MapVinaMap.class);
    Style style = mock(Style.class);
    when(mapVinaMap.getStyle()).thenReturn(style);
    Bitmap markerBitmap = mock(Bitmap.class);
    SymbolOnStyleLoadedListener listener = new SymbolOnStyleLoadedListener(mapVinaMap, markerBitmap);

    listener.onDidFinishLoadingStyle();

    verify(style).addImage(eq(MAPVINA_NAVIGATION_MARKER_NAME), eq(markerBitmap));
  }
}