package com.divyansh.weatherly

data class GeoCodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)
