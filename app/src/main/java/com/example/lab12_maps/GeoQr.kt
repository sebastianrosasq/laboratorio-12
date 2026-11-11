package com.example.lab12_maps

internal fun parseGeoQr(value: String): Pair<Double, Double>? {
    if (!value.startsWith("geo:")) return null
    val parts = value.removePrefix("geo:").substringBefore("?").split(",")
    if (parts.size != 2) return null
    val latitude = parts[0].toDoubleOrNull() ?: return null
    val longitude = parts[1].toDoubleOrNull() ?: return null
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
    return latitude to longitude
}
