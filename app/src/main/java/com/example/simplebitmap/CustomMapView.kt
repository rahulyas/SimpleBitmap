package com.example.simplebitmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class CustomMapView(context: Context, attrs: AttributeSet?) : MapView(context, attrs) {

    private var gridlinesOverlay: GridlinesOverlay? = null

    override fun setTileSource(tileSource: ITileSource) {
        super.setTileSource(tileSource)

        // Remove or add the gridlines overlay based on tileSource availability
        if (tileSource != null && gridlinesOverlay == null) {
            gridlinesOverlay = GridlinesOverlay(this)
            overlays.add(gridlinesOverlay)
        } else if (tileSource == null && gridlinesOverlay != null) {
            overlays.remove(gridlinesOverlay)
            gridlinesOverlay = null
        }
    }
    class GridlinesOverlay(mapView: MapView) : Overlay() {

        private val paint: Paint = Paint()

        init {
            paint.color = Color.TRANSPARENT
        }

        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            super.draw(canvas, mapView, shadow)
            val tileSize = 256 * Math.pow(2.0, mapView.zoomLevel.toDouble()).toInt()
            val mapWidth = mapView.width
            val mapHeight = mapView.height
            // Vertical gridlines
            for (x in 0 until mapWidth step tileSize) {
                canvas.drawLine(x.toFloat(), 0f, x.toFloat(), mapHeight.toFloat(), paint)
            }
            // Horizontal gridlines
            for (y in 0 until mapHeight step tileSize) {
                canvas.drawLine(0f, y.toFloat(), mapWidth.toFloat(), y.toFloat(), paint)
            }
        }
    }
}