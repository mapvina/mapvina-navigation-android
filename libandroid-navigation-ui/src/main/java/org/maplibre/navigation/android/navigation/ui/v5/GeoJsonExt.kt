package com.mapvina.navigation.android.navigation.ui.v5

import com.mapvina.spatialk.geojson.Point
import com.mapvina.spatialk.geojson.LineString
import com.mapvina.spatialk.geojson.Feature
import com.mapvina.spatialk.geojson.FeatureCollection
import com.mapvina.spatialk.geojson.GeoJsonObject
import kotlin.jvm.JvmName

// --- KMP to Mapbox JVM ---
fun Point.toJvm(): com.mapbox.geojson.Point {
    return com.mapbox.geojson.Point.fromJson(GeoJsonObject.toJson(this))
}

fun LineString.toJvm(): com.mapbox.geojson.LineString {
    return com.mapbox.geojson.LineString.fromJson(GeoJsonObject.toJson(this))
}

fun Feature<*, *>.toJvm(): com.mapbox.geojson.Feature {
    return com.mapbox.geojson.Feature.fromJson(GeoJsonObject.toJson(this))
}

fun FeatureCollection<*, *>.toJvm(): com.mapbox.geojson.FeatureCollection {
    return com.mapbox.geojson.FeatureCollection.fromJson(GeoJsonObject.toJson(this))
}

fun List<Point>.toJvmPoints(): List<com.mapbox.geojson.Point> = map { it.toJvm() }

// --- KMP to MapVina JVM ---
fun Point.toMapVina(): com.mapvina.geojson.Point {
    return com.mapvina.geojson.Point.fromJson(GeoJsonObject.toJson(this))
}

fun LineString.toMapVina(): com.mapvina.geojson.LineString {
    return com.mapvina.geojson.LineString.fromJson(GeoJsonObject.toJson(this))
}

fun Feature<*, *>.toMapVina(): com.mapvina.geojson.Feature {
    return com.mapvina.geojson.Feature.fromJson(GeoJsonObject.toJson(this))
}

fun FeatureCollection<*, *>.toMapVina(): com.mapvina.geojson.FeatureCollection {
    return com.mapvina.geojson.FeatureCollection.fromJson(GeoJsonObject.toJson(this))
}

fun List<Point>.toMapVinaPoints(): List<com.mapvina.geojson.Point> = map { it.toMapVina() }

// --- Mapbox JVM to KMP ---
@JvmName("mapboxPointToKmp")
fun com.mapbox.geojson.Point.toKmp(): Point {
    return GeoJsonObject.fromJson(this.toJson()) as Point
}

@JvmName("mapboxLineStringToKmp")
fun com.mapbox.geojson.LineString.toKmp(): LineString {
    return GeoJsonObject.fromJson(this.toJson()) as LineString
}

@JvmName("mapboxFeatureToKmp")
fun com.mapbox.geojson.Feature.toKmp(): Feature<*, *> {
    return GeoJsonObject.fromJson(this.toJson()) as Feature<*, *>
}

@JvmName("mapboxToKmpPoints")
fun List<com.mapbox.geojson.Point>.toKmpPoints(): List<Point> = map { it.toKmp() }

@JvmName("mapboxToKmpFeatures")
fun List<com.mapbox.geojson.Feature>.toKmpFeatures(): List<Feature<*, *>> = map { it.toKmp() }

// --- MapVina JVM to KMP ---
@JvmName("mapvinaPointToKmp")
fun com.mapvina.geojson.Point.toKmp(): Point {
    return GeoJsonObject.fromJson(this.toJson()) as Point
}

@JvmName("mapvinaLineStringToKmp")
fun com.mapvina.geojson.LineString.toKmp(): LineString {
    return GeoJsonObject.fromJson(this.toJson()) as LineString
}

@JvmName("mapvinaFeatureToKmp")
fun com.mapvina.geojson.Feature.toKmp(): Feature<*, *> {
    return GeoJsonObject.fromJson(this.toJson()) as Feature<*, *>
}

@JvmName("mapvinaToKmpPoints")
fun List<com.mapvina.geojson.Point>.toKmpPoints(): List<Point> = map { it.toKmp() }

@JvmName("mapvinaToKmpFeatures")
fun List<com.mapvina.geojson.Feature>.toKmpFeatures(): List<Feature<*, *>> = map { it.toKmp() }
