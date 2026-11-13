package com.example.myapplication.ui.screens.Map.componentes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.R
import com.example.myapplication.data.Review
import com.example.myapplication.ui.screens.Map.ReviewCluster
import com.example.myapplication.ui.screens.Map.clusterReviews
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import kotlin.math.floor

private const val DEFAULT_ZOOM = 2.5
private const val SINGLE_POINT_ZOOM = 14.0
private const val FIT_PADDING_PX = 120
private const val CLUSTER_FIT_PADDING_PX = 160
private const val SELECTED_SCALE = 1.45f
private const val SAME_PLACE_DEGREES = 0.0001

/**
 * Modo oscuro de las teselas: invertir los colores y rotar el tono 180° (el truco clásico de «invert + hue-rotate»)
 * oscurece el mapa pero conserva azules para el agua y verdes para la tierra; solo invertir los deja marrones.
 */
private val DARK_TILES_FILTER: ColorMatrixColorFilter = run {
    val invert = ColorMatrix(
        floatArrayOf(
            -0.9f, 0f, 0f, 0f, 235f,
            0f, -0.9f, 0f, 0f, 235f,
            0f, 0f, -0.9f, 0f, 235f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    val hue180 = ColorMatrix(
        floatArrayOf(
            -0.574f, 1.430f, 0.144f, 0f, 0f,
            0.426f, 0.430f, 0.144f, 0f, 0f,
            0.426f, 1.430f, -0.856f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    invert.postConcat(hue180)
    ColorMatrixColorFilter(invert)
}

// ---- Proveedor del mapa: único lugar donde se define de dónde salen las teselas -------------------
// OpenStreetMap "Mapnik" (https://tile.openstreetmap.org). No requiere API key, solo un User-Agent propio.
private val TILE_SOURCE = TileSourceFactory.MAPNIK

/**
 * Mapa OpenStreetMap (osmdroid) con marcadores personalizados, agrupación de marcadores cercanos,
 * botones + / −, y modo claro/oscuro según el tema de la app. Es el único archivo que conoce osmdroid:
 * el resto de la pantalla trabaja con [Review].
 */
@Composable
fun OsmMapView(
    reviews: List<Review>,
    selectedReviewId: String?,
    onMarkerClick: (String) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Modo oscuro: se deduce del tema real de la app (que puede forzarlo con el interruptor del Perfil)
    val darkMode = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val latest by rememberUpdatedState(MapInputs(reviews, selectedReviewId, darkMode, onMarkerClick, onMapClick))
    val framing = remember { booleanArrayOf(false) }
    val lastZoomBucket = remember { doubleArrayOf(Double.NaN) }

    val mapView = remember(context) {
        configureOsmdroid(context)
        MapView(context).apply {
            setTileSource(TILE_SOURCE)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER) // usamos + / − de Compose
            minZoomLevel = 2.0
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(GeoPoint(15.0, -30.0))
        }
    }

    fun render(map: MapView) = renderMap(map, latest, framing)

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        // Al cambiar el nivel de zoom se vuelven a agrupar los marcadores (por medio nivel, no por cada frame).
        val listener = object : MapListener {
            override fun onScroll(event: ScrollEvent?) = false
            override fun onZoom(event: ZoomEvent?): Boolean {
                val bucket = floor(mapView.zoomLevelDouble * 2) / 2
                if (bucket != lastZoomBucket[0]) {
                    lastZoomBucket[0] = bucket
                    render(mapView)
                }
                return false
            }
        }
        mapView.addMapListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            mapView.removeMapListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onDetach()
        }
    }

    // clipToBounds: sin él el MapView dibuja teselas fuera de su área y tapa la barra superior
    Box(modifier = modifier.clipToBounds()) {
        AndroidView(
            modifier = Modifier.matchParentSize().clipToBounds(),
            factory = { mapView },
            update = { map -> render(map) }
        )

        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(onClick = { mapView.controller.zoomIn() }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.map_zoom_in))
            }
            SmallFloatingActionButton(onClick = { mapView.controller.zoomOut() }) {
                Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.map_zoom_out))
            }
        }
    }
}

private data class MapInputs(
    val reviews: List<Review>,
    val selectedId: String?,
    val darkMode: Boolean,
    val onMarkerClick: (String) -> Unit,
    val onMapClick: () -> Unit
)

private fun renderMap(map: MapView, inputs: MapInputs, framing: BooleanArray) {
    // Modo claro/oscuro: en oscuro se invierten los colores de las teselas
    map.overlayManager.tilesOverlay.setColorFilter(if (inputs.darkMode) DARK_TILES_FILTER else null)

    map.overlays.clear()
    // Primero el receptor de toques al mapa: los marcadores se agregan después y lo atienden antes.
    map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            inputs.onMapClick()
            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean = false
    }))
    map.overlays.add(CopyrightOverlay(map.context))

    val clusters = clusterReviews(inputs.reviews, map.zoomLevelDouble, inputs.selectedId)
    clusters.sortedBy { c -> c.reviews.any { it.id == inputs.selectedId } }.forEach { cluster ->
        map.overlays.add(markerFor(map, cluster, inputs))
    }

    if (!framing[0] && inputs.reviews.isNotEmpty()) {
        framing[0] = true
        fitToReviews(map, inputs.reviews)
    }
    map.invalidate()
}

private fun markerFor(map: MapView, cluster: ReviewCluster, inputs: MapInputs): Marker =
    Marker(map).apply {
        position = GeoPoint(cluster.latitude, cluster.longitude)
        setInfoWindow(null) // la información se muestra en la tarjeta de Compose
        if (cluster.isSingle) {
            val review = cluster.reviews.single()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = BitmapDrawable(map.resources, pinBitmap(map.context, review.rating, review.id == inputs.selectedId))
            title = review.title
            setOnMarkerClickListener { _, _ ->
                inputs.onMarkerClick(review.id)
                true
            }
        } else {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = BitmapDrawable(map.resources, clusterBitmap(map.context, cluster.reviews.size))
            setOnMarkerClickListener { _, _ ->
                zoomIntoCluster(map, cluster, inputs)
                true
            }
        }
    }

/** Tocar un grupo acerca el mapa a sus reseñas; si todas están en el mismo punto se abre la más reciente. */
private fun zoomIntoCluster(map: MapView, cluster: ReviewCluster, inputs: MapInputs) {
    val points = cluster.reviews.mapNotNull { r -> r.location?.let { GeoPoint(it.latitude, it.longitude) } }
    val box = BoundingBox.fromGeoPoints(points)
    if (box.latitudeSpan < SAME_PLACE_DEGREES && box.longitudeSpan < SAME_PLACE_DEGREES) {
        map.controller.animateTo(GeoPoint(cluster.latitude, cluster.longitude), 18.0, 400L)
        inputs.onMarkerClick(cluster.reviews.first().id)
    } else {
        map.zoomToBoundingBox(box, true, CLUSTER_FIT_PADDING_PX)
    }
}

private fun fitToReviews(map: MapView, reviews: List<Review>) {
    val points = reviews.mapNotNull { r -> r.location?.let { GeoPoint(it.latitude, it.longitude) } }
    if (points.isEmpty()) return
    val box = BoundingBox.fromGeoPoints(points)
    // El mapa todavía puede no tener tamaño en la primera composición: se encuadra cuando ya lo tiene.
    map.post {
        if (points.size == 1 || (box.latitudeSpan == 0.0 && box.longitudeSpan == 0.0)) {
            map.controller.setZoom(SINGLE_POINT_ZOOM)
            map.controller.setCenter(points.first())
        } else {
            map.zoomToBoundingBox(box, false, FIT_PADDING_PX)
        }
    }
}

// ---- Marcadores personalizados -----------------------------------------------------------------------

private fun ratingColor(rating: Int) = when {
    rating >= 4 -> Color.rgb(46, 125, 50)   // verde
    rating == 3 -> Color.rgb(249, 168, 37)  // ámbar
    else -> Color.rgb(198, 40, 40)          // rojo
}

/** Pin teñido según la puntuación, con el número de estrellas dentro; más grande si está seleccionado. */
private fun pinBitmap(context: Context, rating: Int, selected: Boolean): Bitmap {
    val scale = if (selected) SELECTED_SCALE else 1f
    val base = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_map_pin)!!.mutate())
    DrawableCompat.setTint(base, ratingColor(rating))
    val width = (base.intrinsicWidth * scale).toInt()
    val height = (base.intrinsicHeight * scale).toInt()
    val bitmap = base.toBitmap(width, height)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        textSize = height * 0.34f
    }
    // El círculo del pin está en el 37 % superior; el texto se centra ahí
    val y = height * 0.37f - (paint.descent() + paint.ascent()) / 2
    Canvas(bitmap).drawText(rating.toString(), width / 2f, y, paint)
    return bitmap
}

/** Círculo con el número de reseñas del grupo; crece un poco con la cantidad. */
private fun clusterBitmap(context: Context, count: Int): Bitmap {
    val density = context.resources.displayMetrics.density
    val diameter = ((34 + minOf(count, 30)) * density).toInt()
    val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = diameter / 2f
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(34, 34, 34) }
    val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3 * density
    }
    canvas.drawCircle(center, center, center - ring.strokeWidth, fill)
    canvas.drawCircle(center, center, center - ring.strokeWidth, ring)

    val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        textSize = diameter * 0.38f
    }
    canvas.drawText(count.toString(), center, center - (text.descent() + text.ascent()) / 2, text)
    return bitmap
}

/** osmdroid pide un agente de usuario propio (política de OpenStreetMap) y una caché que no necesite permisos. */
private fun configureOsmdroid(context: Context) {
    Configuration.getInstance().apply {
        // OSM bloquea los User-Agent genéricos (p. ej. el nombre de paquete de ejemplo): debe identificar la app
        userAgentValue = "ReviewLab/1.0 (Android; academic project)"
        osmdroidBasePath = File(context.cacheDir, "osmdroid")
        osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
    }
}
