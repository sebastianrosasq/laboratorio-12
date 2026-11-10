package com.example.lab12_maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private val arequipa = LatLng(-16.4040102, -71.559611)

@Composable
fun MapScreen() {
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(arequipa, 12f)
    }
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = camera) {
        Marker(state = rememberMarkerState(position = arequipa), title = "Arequipa, Perú")
    }
}
