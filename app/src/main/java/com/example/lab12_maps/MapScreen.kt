package com.example.lab12_maps

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private val arequipa = LatLng(-16.4040102, -71.559611)
private val yura = LatLng(-16.2520984, -71.6836503)

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
    val mall = listOf(
        LatLng(-16.432292, -71.509145), LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310), LatLng(-16.432566, -71.508853)
    )
    val lambramani = listOf(
        LatLng(-16.422704, -71.530830), LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110), LatLng(-16.423050, -71.530600)
    )
    val plaza = listOf(
        LatLng(-16.398866, -71.536961), LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289), LatLng(-16.399299, -71.536721)
    )
    LaunchedEffect(Unit) {
        delay(3500)
        camera.animate(CameraUpdateFactory.newLatLngZoom(yura, 12f), durationMs = 3000)
    }
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = camera) {
        Marker(state = rememberMarkerState(position = arequipa), title = "Arequipa, Perú", icon = mountainIcon)
        locations.forEach { (position, name) ->
            Marker(state = rememberMarkerState(position = position), title = name, snippet = "Punto de interés")
        }
        listOf(mall, lambramani, plaza).forEach { points ->
            Polygon(points = points, strokeColor = Color.Red, fillColor = Color.Blue.copy(alpha = 0.35f), strokeWidth = 5f)
        }
        Polyline(
            points = listOf(plaza.first(), lambramani.first(), mall.first()),
            color = Color(0xFF1676BB),
            width = 8f
        )
    }
}
