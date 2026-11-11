package com.example.lab12_maps

import android.graphics.Bitmap
import android.graphics.Canvas
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.SphericalUtil
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

private val arequipa = LatLng(-16.4040102, -71.559611)
private val yura = LatLng(-16.2520984, -71.6836503)

@Composable
fun MapScreen() {
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var menuOpen by remember { mutableStateOf(false) }
    var showDistance by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(arequipa, 12f)
    }
    val context = LocalContext.current
    var locationMessage by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var qrMessage by remember { mutableStateOf("") }
    var qrLocation by remember { mutableStateOf<LatLng?>(null) }
    val qrPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val image = InputImage.fromFilePath(context, uri)
                val scanner = BarcodeScanning.getClient()
                scanner.process(image)
                    .addOnSuccessListener { codes ->
                        val value = codes.firstOrNull()?.rawValue.orEmpty()
                        val coordinates = parseGeoQr(value)
                        if (coordinates != null) {
                            val point = LatLng(coordinates.first, coordinates.second)
                            qrLocation = point
                            qrMessage = "Ubicación del QR encontrada"
                            scope.launch { camera.animate(CameraUpdateFactory.newLatLngZoom(point, 15f), 1500) }
                        } else {
                            qrMessage = "El QR no contiene una ubicación válida"
                        }
                    }
                    .addOnFailureListener { qrMessage = "No se pudo leer la imagen" }
                    .addOnCompleteListener { scanner.close() }
            } catch (_: Exception) {
                qrMessage = "No se pudo abrir la imagen"
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun findLocation() {
        locationMessage = "Buscando ubicación"
        val token = CancellationTokenSource()
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, token.token)
            .addOnSuccessListener { location ->
                if (location == null) {
                    locationMessage = "No se pudo obtener la ubicación"
                } else {
                    val point = LatLng(location.latitude, location.longitude)
                    currentLocation = point
                    locationMessage = "Ubicación encontrada"
                    scope.launch { camera.animate(CameraUpdateFactory.newLatLngZoom(point, 15f), 1500) }
                }
            }
            .addOnFailureListener { locationMessage = "No se pudo obtener la ubicación" }
    }
    val permissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) findLocation()
        else locationMessage = "Permiso de ubicación no concedido"
    }
    val mountainIcon = remember {
        MapsInitializer.initialize(context)
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
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = MapProperties(mapType = mapType)
        ) {
            Marker(state = rememberMarkerState(position = arequipa), title = "Arequipa, Perú", icon = mountainIcon)
            locations.forEach { (position, name) ->
                Marker(state = rememberMarkerState(position = position), title = name, snippet = "Punto de interés")
            }
            currentLocation?.let { point ->
                Marker(state = rememberMarkerState(position = point), title = "Mi ubicación")
            }
            qrLocation?.let { point ->
                Marker(state = rememberMarkerState(position = point), title = "Ubicación del QR")
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
        Surface(modifier = Modifier.padding(8.dp), shadowElevation = 3.dp) {
            Column(Modifier.padding(8.dp)) {
                Row {
                    Box {
                        Button(onClick = { menuOpen = true }) { Text("Tipo de mapa") }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            listOf(
                                "Normal" to MapType.NORMAL,
                                "Satélite" to MapType.SATELLITE,
                                "Terreno" to MapType.TERRAIN,
                                "Híbrido" to MapType.HYBRID
                            ).forEach { (label, type) ->
                                DropdownMenuItem(text = { Text(label) }, onClick = {
                                    mapType = type
                                    menuOpen = false
                                })
                            }
                        }
                    }
                    Button(onClick = {
                        scope.launch { camera.animate(CameraUpdateFactory.newLatLngZoom(arequipa, 14f), 1500) }
                    }, modifier = Modifier.padding(start = 8.dp)) { Text("Arequipa") }
                }
                Button(onClick = {
                    scope.launch { camera.animate(CameraUpdateFactory.newLatLngZoom(yura, 12f), 3000) }
                }) { Text("Ir a Yura") }
                Button(onClick = {
                    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
                        findLocation()
                    } else {
                        permissionRequest.launch(arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
                    }
                }) { Text("Mi ubicación") }
                if (locationMessage.isNotEmpty()) Text(locationMessage)
                Button(onClick = { showDistance = !showDistance }) { Text("Medir distancia") }
                if (showDistance) {
                    val km = SphericalUtil.computeDistanceBetween(plaza.first(), lambramani.first()) / 1000.0
                    Text("Plaza - Lambramani: ${String.format(Locale.US, "%.2f", km)} km en línea recta")
                }
                Button(onClick = { qrPicker.launch("image/*") }) { Text("Abrir QR de ubicación") }
                if (qrMessage.isNotEmpty()) Text(qrMessage)
            }
        }
    }
}
