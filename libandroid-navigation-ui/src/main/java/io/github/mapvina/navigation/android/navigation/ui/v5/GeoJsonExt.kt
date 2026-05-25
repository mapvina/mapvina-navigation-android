package io.github.mapvina.navigation.android.navigation.ui.v5

import io.github.mapvina.spatialk.geojson.Point
import io.github.mapvina.spatialk.geojson.LineString
import io.github.mapvina.spatialk.geojson.Feature
import io.github.mapvina.spatialk.geojson.FeatureCollection
import io.github.mapvina.spatialk.geojson.GeoJsonObject
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
fun Point.toMapVina(): io.github.mapvina.geojson.Point {
    return io.github.mapvina.geojson.Point.fromJson(GeoJsonObject.toJson(this))
}

fun LineString.toMapVina(): io.github.mapvina.geojson.LineString {
    return io.github.mapvina.geojson.LineString.fromJson(GeoJsonObject.toJson(this))
}

fun Feature<*, *>.toMapVina(): io.github.mapvina.geojson.Feature {
    return io.github.mapvina.geojson.Feature.fromJson(GeoJsonObject.toJson(this))
}

fun FeatureCollection<*, *>.toMapVina(): io.github.mapvina.geojson.FeatureCollection {
    return io.github.mapvina.geojson.FeatureCollection.fromJson(GeoJsonObject.toJson(this))
}

fun List<Point>.toMapVinaPoints(): List<io.github.mapvina.geojson.Point> = map { it.toMapVina() }

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
fun io.github.mapvina.geojson.Point.toKmp(): Point {
    return GeoJsonObject.fromJson(this.toJson()) as Point
}

@JvmName("mapvinaLineStringToKmp")
fun io.github.mapvina.geojson.LineString.toKmp(): LineString {
    return GeoJsonObject.fromJson(this.toJson()) as LineString
}

@JvmName("mapvinaFeatureToKmp")
fun io.github.mapvina.geojson.Feature.toKmp(): Feature<*, *> {
    return GeoJsonObject.fromJson(this.toJson()) as Feature<*, *>
}

@JvmName("mapvinaToKmpPoints")
fun List<io.github.mapvina.geojson.Point>.toKmpPoints(): List<Point> = map { it.toKmp() }

@JvmName("mapvinaToKmpFeatures")
fun List<io.github.mapvina.geojson.Feature>.toKmpFeatures(): List<Feature<*, *>> = map { it.toKmp() }
