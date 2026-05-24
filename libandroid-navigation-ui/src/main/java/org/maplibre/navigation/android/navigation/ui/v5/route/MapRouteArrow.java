package com.mapvina.navigation.android.navigation.ui.v5.route;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;

import com.mapvina.geojson.Feature;
import com.mapvina.geojson.FeatureCollection;
import com.mapvina.geojson.LineString;
import com.mapvina.geojson.Point;
import com.mapvina.android.maps.MapView;
import com.mapvina.android.maps.MapVinaMap;
import com.mapvina.android.maps.Style;
import com.mapvina.android.style.layers.Layer;
import com.mapvina.android.style.layers.LineLayer;
import com.mapvina.android.style.layers.Property;
import com.mapvina.android.style.layers.PropertyFactory;
import com.mapvina.android.style.layers.SymbolLayer;
import com.mapvina.android.style.sources.GeoJsonOptions;
import com.mapvina.android.style.sources.GeoJsonSource;
import com.mapvina.android.utils.MathUtils;
import com.mapvina.navigation.android.navigation.ui.v5.R;
import com.mapvina.navigation.android.navigation.ui.v5.utils.MapImageUtils;
import com.mapvina.navigation.core.routeprogress.RouteProgress;
import com.mapvina.turf.TurfConstants;
import com.mapvina.turf.TurfMeasurement;
import com.mapvina.turf.TurfMisc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mapvina.android.style.expressions.Expression.color;
import static com.mapvina.android.style.expressions.Expression.get;
import static com.mapvina.android.style.expressions.Expression.interpolate;
import static com.mapvina.android.style.expressions.Expression.linear;
import static com.mapvina.android.style.expressions.Expression.step;
import static com.mapvina.android.style.expressions.Expression.stop;
import static com.mapvina.android.style.expressions.Expression.zoom;
import static com.mapvina.android.style.layers.Property.ICON_ROTATION_ALIGNMENT_MAP;
import static com.mapvina.android.style.layers.Property.NONE;
import static com.mapvina.android.style.layers.Property.VISIBLE;
import static com.mapvina.android.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapvina.android.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapvina.android.style.layers.PropertyFactory.visibility;
import static com.mapvina.navigation.android.navigation.ui.v5.GeoJsonExtKt.toJvm;
import static com.mapvina.navigation.android.navigation.ui.v5.GeoJsonExtKt.toMapVinaPoints;

class MapRouteArrow {

  @ColorInt
  private final int arrowColor;
  @ColorInt
  private final int arrowBorderColor;

  private List<String> arrowLayerIds;
  private GeoJsonSource arrowShaftGeoJsonSource;
  private GeoJsonSource arrowHeadGeoJsonSource;

  private final MapView mapView;
  private final MapVinaMap mapVinaMap;

  MapRouteArrow(MapView mapView, MapVinaMap mapVinaMap, @StyleRes int styleRes) {
    this.mapView = mapView;
    this.mapVinaMap = mapVinaMap;

    Context context = mapView.getContext();
    TypedArray typedArray = context.obtainStyledAttributes(styleRes, R.styleable.NavigationMapRoute);
    arrowColor = typedArray.getColor(R.styleable.NavigationMapRoute_upcomingManeuverArrowColor,
      ContextCompat.getColor(context, R.color.mapvina_navigation_route_upcoming_maneuver_arrow_color));
    arrowBorderColor = typedArray.getColor(R.styleable.NavigationMapRoute_upcomingManeuverArrowBorderColor,
      ContextCompat.getColor(context, R.color.mapvina_navigation_route_upcoming_maneuver_arrow_border_color));
    typedArray.recycle();

    initialize();
  }

  void addUpcomingManeuverArrow(RouteProgress routeProgress) {
    boolean invalidUpcomingStepPoints = routeProgress.getUpcomingStepPoints() == null
      || routeProgress.getUpcomingStepPoints().size() < RouteConstants.TWO_POINTS;
    boolean invalidCurrentStepPoints = routeProgress.getCurrentStepPoints().size() < RouteConstants.TWO_POINTS;
    if (invalidUpcomingStepPoints || invalidCurrentStepPoints) {
      updateVisibilityTo(false);
      return;
    }
    updateVisibilityTo(true);

    List<Point> maneuverPoints = obtainArrowPointsFrom(routeProgress);
    updateArrowShaftWith(maneuverPoints);
    updateArrowHeadWith(maneuverPoints);
  }

  void updateVisibilityTo(boolean visible) {
    Style style = mapVinaMap.getStyle();
    if (style != null) {
      for (String layerId : arrowLayerIds) {
        Layer layer = style.getLayer(layerId);
        if (layer != null) {
          String targetVisibility = visible ? VISIBLE : NONE;
          if (!targetVisibility.equals(layer.getVisibility().getValue())) {
            layer.setProperties(visibility(targetVisibility));
          }
        }
      }
    }
  }

  private List<Point> obtainArrowPointsFrom(RouteProgress routeProgress) {
    List<Point> reversedCurrent = new ArrayList<>(toMapVinaPoints(routeProgress.getCurrentStepPoints()));
    Collections.reverse(reversedCurrent);

    LineString arrowLineCurrent = LineString.fromLngLats(reversedCurrent);
    LineString arrowLineUpcoming = LineString.fromLngLats(toMapVinaPoints(routeProgress.getUpcomingStepPoints()));

    LineString arrowCurrentSliced = TurfMisc.lineSliceAlong(arrowLineCurrent, 0, RouteConstants.THIRTY, TurfConstants.UNIT_METERS);
    LineString arrowUpcomingSliced = TurfMisc.lineSliceAlong(arrowLineUpcoming, 0, RouteConstants.THIRTY, TurfConstants.UNIT_METERS);

    List<Point> reversedArrowCurrentSliced = new ArrayList<>(arrowCurrentSliced.coordinates());
    Collections.reverse(reversedArrowCurrentSliced);

    List<Point> combined = new ArrayList<>();
    combined.addAll(reversedArrowCurrentSliced);
    combined.addAll(arrowUpcomingSliced.coordinates());
    return combined;
  }

  private void updateArrowShaftWith(List<Point> points) {
    LineString shaft = LineString.fromLngLats(points);
    Feature arrowShaftGeoJsonFeature = Feature.fromGeometry(shaft);
    arrowShaftGeoJsonSource.setGeoJson(arrowShaftGeoJsonFeature);
  }

  private void updateArrowHeadWith(List<Point> points) {
    double azimuth = TurfMeasurement.bearing(points.get(points.size() - 2), points.get(points.size() - 1));
    Feature arrowHeadGeoJsonFeature = Feature.fromGeometry(points.get(points.size() - 1));
    arrowHeadGeoJsonFeature.addNumberProperty(RouteConstants.ARROW_BEARING, (float) MathUtils.wrap(azimuth, 0, RouteConstants.MAX_DEGREES));
    arrowHeadGeoJsonSource.setGeoJson(arrowHeadGeoJsonFeature);
  }

  private void initialize() {
    initializeArrowShaft();
    initializeArrowHead();

    addArrowHeadIcon();
    addArrowHeadIconCasing();

    LineLayer shaftLayer = createArrowShaftLayer();
    LineLayer shaftCasingLayer = createArrowShaftCasingLayer();
    SymbolLayer headLayer = createArrowHeadLayer();
    SymbolLayer headCasingLayer = createArrowHeadCasingLayer();

    mapVinaMap.getStyle().addLayerBelow(shaftCasingLayer, RouteConstants.LAYER_ABOVE_UPCOMING_MANEUVER_ARROW);
    mapVinaMap.getStyle().addLayerAbove(headCasingLayer, shaftCasingLayer.getId());

    mapVinaMap.getStyle().addLayerAbove(shaftLayer, headCasingLayer.getId());
    mapVinaMap.getStyle().addLayerAbove(headLayer, shaftLayer.getId());

    createArrowLayerList(shaftLayer, shaftCasingLayer, headLayer, headCasingLayer);
  }

  private void initializeArrowShaft() {
    arrowShaftGeoJsonSource = new GeoJsonSource(
      RouteConstants.ARROW_SHAFT_SOURCE_ID,
      FeatureCollection.fromFeatures(new Feature[]{}),
      new GeoJsonOptions().withMaxZoom(16)
    );
    mapVinaMap.getStyle().addSource(arrowShaftGeoJsonSource);
  }

  private void initializeArrowHead() {
    arrowHeadGeoJsonSource = new GeoJsonSource(
      RouteConstants.ARROW_HEAD_SOURCE_ID,
      FeatureCollection.fromFeatures(new Feature[]{}),
      new GeoJsonOptions().withMaxZoom(16)
    );
    mapVinaMap.getStyle().addSource(arrowHeadGeoJsonSource);
  }

  private void addArrowHeadIcon() {
    int headResId = R.drawable.ic_arrow_head;
    Drawable arrowHead = AppCompatResources.getDrawable(mapView.getContext(), headResId);
    if (arrowHead == null) {
      return;
    }
    Drawable head = DrawableCompat.wrap(arrowHead);
    DrawableCompat.setTint(head.mutate(), arrowColor);
    Bitmap icon = MapImageUtils.getBitmapFromDrawable(head);
    mapVinaMap.getStyle().addImage(RouteConstants.ARROW_HEAD_ICON, icon);
  }

  private void addArrowHeadIconCasing() {
    int casingResId = R.drawable.ic_arrow_head_casing;
    Drawable arrowHeadCasing = AppCompatResources.getDrawable(mapView.getContext(), casingResId);
    if (arrowHeadCasing == null) {
      return;
    }
    Drawable headCasing = DrawableCompat.wrap(arrowHeadCasing);
    DrawableCompat.setTint(headCasing.mutate(), arrowBorderColor);
    Bitmap icon = MapImageUtils.getBitmapFromDrawable(headCasing);
    mapVinaMap.getStyle().addImage(RouteConstants.ARROW_HEAD_ICON_CASING, icon);
  }

  private LineLayer createArrowShaftLayer() {
    LineLayer shaftLayer = (LineLayer) mapVinaMap.getStyle().getLayer(RouteConstants.ARROW_SHAFT_LINE_LAYER_ID);
    if (shaftLayer != null) {
      mapVinaMap.getStyle().removeLayer(shaftLayer);
    }
    return new LineLayer(RouteConstants.ARROW_SHAFT_LINE_LAYER_ID, RouteConstants.ARROW_SHAFT_SOURCE_ID).withProperties(
      PropertyFactory.lineColor(color(arrowColor)),
      PropertyFactory.lineWidth(
        interpolate(linear(), zoom(),
          stop(RouteConstants.MIN_ARROW_ZOOM, RouteConstants.MIN_ZOOM_ARROW_SHAFT_SCALE),
          stop(RouteConstants.MAX_ARROW_ZOOM, RouteConstants.MAX_ZOOM_ARROW_SHAFT_SCALE)
        )
      ),
      PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
      PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
      PropertyFactory.visibility(NONE),
      PropertyFactory.lineOpacity(
        step(zoom(), RouteConstants.OPAQUE,
          stop(
            RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
          )
        )
      )
    );
  }

  private LineLayer createArrowShaftCasingLayer() {
    LineLayer shaftCasingLayer = (LineLayer) mapVinaMap.getStyle().getLayer(RouteConstants.ARROW_SHAFT_CASING_LINE_LAYER_ID);
    if (shaftCasingLayer != null) {
      mapVinaMap.getStyle().removeLayer(shaftCasingLayer);
    }
    return new LineLayer(RouteConstants.ARROW_SHAFT_CASING_LINE_LAYER_ID, RouteConstants.ARROW_SHAFT_SOURCE_ID).withProperties(
      PropertyFactory.lineColor(color(arrowBorderColor)),
      PropertyFactory.lineWidth(
        interpolate(linear(), zoom(),
          stop(RouteConstants.MIN_ARROW_ZOOM, RouteConstants.MIN_ZOOM_ARROW_SHAFT_CASING_SCALE),
          stop(RouteConstants.MAX_ARROW_ZOOM, RouteConstants.MAX_ZOOM_ARROW_SHAFT_CASING_SCALE)
        )
      ),
      PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
      PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
      PropertyFactory.visibility(NONE),
      PropertyFactory.lineOpacity(
        step(zoom(), RouteConstants.OPAQUE,
          stop(
            RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
          )
        )
      )
    );
  }

  private SymbolLayer createArrowHeadLayer() {
    SymbolLayer headLayer = (SymbolLayer) mapVinaMap.getStyle().getLayer(RouteConstants.ARROW_HEAD_LAYER_ID);
    if (headLayer != null) {
      mapVinaMap.getStyle().removeLayer(headLayer);
    }
    return new SymbolLayer(RouteConstants.ARROW_HEAD_LAYER_ID, RouteConstants.ARROW_HEAD_SOURCE_ID)
      .withProperties(
        PropertyFactory.iconImage(RouteConstants.ARROW_HEAD_ICON),
        iconAllowOverlap(true),
        iconIgnorePlacement(true),
        PropertyFactory.iconSize(interpolate(linear(), zoom(),
          stop(RouteConstants.MIN_ARROW_ZOOM, RouteConstants.MIN_ZOOM_ARROW_HEAD_SCALE),
          stop(RouteConstants.MAX_ARROW_ZOOM, RouteConstants.MAX_ZOOM_ARROW_HEAD_SCALE)
          )
        ),
        PropertyFactory.iconOffset(RouteConstants.ARROW_HEAD_OFFSET),
        PropertyFactory.iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
        PropertyFactory.iconRotate(get(RouteConstants.ARROW_BEARING)),
        PropertyFactory.visibility(NONE),
        PropertyFactory.iconOpacity(
          step(zoom(), RouteConstants.OPAQUE,
            stop(
              RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
            )
          )
        )
      );
  }

  private SymbolLayer createArrowHeadCasingLayer() {
    SymbolLayer headCasingLayer = (SymbolLayer) mapVinaMap.getStyle().getLayer(RouteConstants.ARROW_HEAD_CASING_LAYER_ID);
    if (headCasingLayer != null) {
      mapVinaMap.getStyle().removeLayer(headCasingLayer);
    }
    return new SymbolLayer(RouteConstants.ARROW_HEAD_CASING_LAYER_ID, RouteConstants.ARROW_HEAD_SOURCE_ID).withProperties(
      PropertyFactory.iconImage(RouteConstants.ARROW_HEAD_ICON_CASING),
      iconAllowOverlap(true),
      iconIgnorePlacement(true),
      PropertyFactory.iconSize(interpolate(
        linear(), zoom(),
        stop(RouteConstants.MIN_ARROW_ZOOM, RouteConstants.MIN_ZOOM_ARROW_HEAD_CASING_SCALE),
        stop(RouteConstants.MAX_ARROW_ZOOM, RouteConstants.MAX_ZOOM_ARROW_HEAD_CASING_SCALE)
      )),
      PropertyFactory.iconOffset(RouteConstants.ARROW_HEAD_CASING_OFFSET),
      PropertyFactory.iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
      PropertyFactory.iconRotate(get(RouteConstants.ARROW_BEARING)),
      PropertyFactory.visibility(NONE),
      PropertyFactory.iconOpacity(
        step(zoom(), RouteConstants.OPAQUE,
          stop(
            RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
          )
        )
      )
    );
  }

  private void createArrowLayerList(LineLayer shaftLayer, LineLayer shaftCasingLayer, SymbolLayer headLayer,
                                    SymbolLayer headCasingLayer) {
    arrowLayerIds = new ArrayList<>();
    arrowLayerIds.add(shaftCasingLayer.getId());
    arrowLayerIds.add(shaftLayer.getId());
    arrowLayerIds.add(headCasingLayer.getId());
    arrowLayerIds.add(headLayer.getId());
  }
}
