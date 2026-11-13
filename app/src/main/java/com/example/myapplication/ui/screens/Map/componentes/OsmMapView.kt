package com.example.myapplication.ui.screens.Map.componentes

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.R
import com.example.myapplication.data.Review
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File

private const val DEFAULT_ZOOM = 2.5
private const val SINGLE_POINT_ZOOM = 14.0
private const val FIT_PADDING_PX = 120
private const val SELECTED_SCALE = 1.45f

/**
 * Mapa OpenStreetMap (osmdroid, no requiere API key) con un marcador por reseña. Es el único archivo
 * que conoce osmdroid: el resto de la pantalla trabaja con [Review], así cambiar de proveedor de mapas
 * solo toca este componente.
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
    val mapView = remember(context) {
        configureOsmdroid(context)
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            minZoomLevel = 2.0
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(GeoPoint(15.0, -30.0))
        }
    }
    // El encuadre automático solo ocurre una vez: si el usuario ya movió el mapa no se le quita el control.
    val framing = remember(mapView) { booleanArrayOf(false) }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { map ->
            map.overlays.clear()
            // Primero el receptor de toques al mapa: los marcadores se agregan después y lo atienden antes.
            map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    onMapClick()
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean = false
            }))
            map.overlays.add(CopyrightOverlay(map.context))

            // La seleccionada va al final para dibujarse encima de las demás.
            reviews.sortedBy { it.id == selectedReviewId }.forEach { review ->
                val location = review.location ?: return@forEach
                map.overlays.add(
                    Marker(map).apply {
                        position = GeoPoint(location.latitude, location.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = pin(map.context, review.rating, selected = review.id == selectedReviewId)
                        title = review.title
                        setInfoWindow(null) // la información se muestra en la tarjeta de Compose
                        setOnMarkerClickListener { _, _ ->
                            onMarkerClick(review.id)
                            true
                        }
                    }
                )
            }
            if (!framing[0] && reviews.isNotEmpty()) {
                framing[0] = true
                fitToReviews(map, reviews)
            }
            map.invalidate()
        }
    )
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

/** El color del marcador resume la puntuación de un vistazo: verde (4-5), ámbar (3) y rojo (1-2). */
private fun pin(context: Context, rating: Int, selected: Boolean): BitmapDrawable {
    val color = when {
        rating >= 4 -> Color.rgb(46, 125, 50)
        rating == 3 -> Color.rgb(249, 168, 37)
        else -> Color.rgb(198, 40, 40)
    }
    val base = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_map_pin)!!.mutate())
    DrawableCompat.setTint(base, color)
    val scale = if (selected) SELECTED_SCALE else 1f
    val width = (base.intrinsicWidth * scale).toInt()
    val height = (base.intrinsicHeight * scale).toInt()
    return BitmapDrawable(context.resources, base.toBitmap(width, height))
}

/** osmdroid pide un agente de usuario propio (política de OpenStreetMap) y una caché que no necesite permisos. */
private fun configureOsmdroid(context: Context) {
    Configuration.getInstance().apply {
        userAgentValue = context.packageName
        osmdroidBasePath = File(context.cacheDir, "osmdroid")
        osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
    }
}
