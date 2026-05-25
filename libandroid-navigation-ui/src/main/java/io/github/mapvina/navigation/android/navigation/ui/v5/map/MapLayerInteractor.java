package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import android.graphics.Color;

import io.github.mapvina.android.maps.MapVinaMap;
import io.github.mapvina.android.style.layers.Layer;
import io.github.mapvina.android.style.layers.LineLayer;
import io.github.mapvina.android.style.layers.SymbolLayer;

import java.util.List;

import static io.github.mapvina.android.style.layers.Property.NONE;
import static io.github.mapvina.android.style.layers.Property.VISIBLE;
import static io.github.mapvina.android.style.layers.PropertyFactory.lineColor;
import static io.github.mapvina.android.style.layers.PropertyFactory.lineWidth;
import static io.github.mapvina.android.style.layers.PropertyFactory.visibility;
import static io.github.mapvina.navigation.android.navigation.ui.v5.map.NavigationMapVinaMap.STREETS_LAYER_ID;

class MapLayerInteractor {

  private static final float DEFAULT_WIDTH = 20f;
  private static final int LAST_INDEX = 0;

  private final MapVinaMap mapVinaMap;

  MapLayerInteractor(MapVinaMap mapVinaMap) {
    this.mapVinaMap = mapVinaMap;
  }

  void updateLayerVisibility(boolean isVisible, String layerIdentifier) {
    // TODO add sourceIdentifier logic when https://github.com/mapbox/mapbox-gl-native/issues/12691 lands
    List<Layer> layers = mapVinaMap.getStyle().getLayers();
    updateLayerWithVisibility(layerIdentifier, layers, isVisible);
  }

  boolean isLayerVisible(String layerIdentifier) {
    // TODO add sourceIdentifier logic when https://github.com/mapbox/mapbox-gl-native/issues/12691 lands
    List<Layer> layers = mapVinaMap.getStyle().getLayers();
    return findLayerVisibility(layerIdentifier, layers);
  }

  void addStreetsLayer(String sourceId, String sourceLayer) {
    LineLayer streetsLayer = new LineLayer(STREETS_LAYER_ID, sourceId)
      .withProperties(
        lineWidth(DEFAULT_WIDTH),
        lineColor(Color.WHITE)
      )
      .withSourceLayer(sourceLayer);
    mapVinaMap.getStyle().addLayerAt(streetsLayer, LAST_INDEX);
  }

  private void updateLayerWithVisibility(String layerIdentifier, List<Layer> layers, boolean isVisible) {
    for (Layer layer : layers) {
      if (isValid(layer)) {
        String sourceLayerId = retrieveSourceLayerId(layer);
        if (sourceLayerId.equals(layerIdentifier)) {
          layer.setProperties(visibility(isVisible ? VISIBLE : NONE));
        }
      }
    }
  }

  private boolean findLayerVisibility(String layerIdentifier, List<Layer> layers) {
    for (Layer layer : layers) {
      if (isValid(layer)) {
        String sourceLayerId = retrieveSourceLayerId(layer);
        if (sourceLayerId.equals(layerIdentifier)) {
          return layer.getVisibility().value.equals(VISIBLE);
        }
      }
    }
    return false;
  }

  private boolean isValid(Layer layer) {
    return layer instanceof LineLayer || layer instanceof SymbolLayer;
  }

  private String retrieveSourceLayerId(Layer layer) {
    String sourceIdentifier;
    if (layer instanceof LineLayer) {
      sourceIdentifier = ((LineLayer) layer).getSourceLayer();
    } else {
      sourceIdentifier = ((SymbolLayer) layer).getSourceLayer();
    }
    return sourceIdentifier;
  }
}
