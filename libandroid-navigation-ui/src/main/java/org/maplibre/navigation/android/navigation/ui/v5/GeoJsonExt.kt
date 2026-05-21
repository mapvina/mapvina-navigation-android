package com.mapvina.navigation.android.navigation.ui.v5

import com.mapvina.geojson.common.toJvm
import com.mapvina.geojson.model.Point
import com.mapvina.geojson.Point as JvmPoint

fun List<Point>.toJvmPoints(): List<JvmPoint> = map { pt -> pt.toJvm() }
