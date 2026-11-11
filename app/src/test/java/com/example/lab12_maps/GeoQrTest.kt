package com.example.lab12_maps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GeoQrTest {
    @Test fun readsCoordinatesWithOptionalQuery() {
        assertEquals(-16.398866 to -71.536961, parseGeoQr("geo:-16.398866,-71.536961?q=plaza"))
    }

    @Test fun rejectsInvalidCoordinates() {
        listOf("", "https://example.com", "geo:91,20", "geo:2,181", "geo:1,x", "geo:1,2,3")
            .forEach { assertNull(parseGeoQr(it)) }
    }
}
