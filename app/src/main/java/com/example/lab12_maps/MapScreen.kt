package com.example.lab12_maps

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    val context = LocalContext.current
    val mountainIcon = remember {
        val drawable = requireNotNull(ContextCompat.getDrawable(context, R.drawable.marcador_montana))
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    val locations = listOf(
        LatLng(-16.433415, -71.5442652) to "José Luis Bustamante y Rivero",
        LatLng(-16.4205151, -71.4945209) to "Paucarpata",
        LatLng(-16.3524187, -71.5675994) to "Zamacola"
    )
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = camera) {
        Marker(state = rememberMarkerState(position = arequipa), title = "Arequipa, Perú", icon = mountainIcon)
        locations.forEach { (position, name) ->
            Marker(state = rememberMarkerState(position = position), title = name, snippet = "Punto de interés")
        }
    }
}
