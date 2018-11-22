package com.zwsi.gb.feature

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.util.*

import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue

import com.qozix.tileview.TileView
import com.qozix.tileview.plugins.CoordinatePlugin
import com.qozix.tileview.plugins.HotSpotPlugin
import com.qozix.tileview.plugins.InfoWindowPlugin
import com.qozix.tileview.plugins.MarkerPlugin
import com.qozix.tileview.plugins.PathPlugin
import java.util.*

/**
 * @author Mike Dunn, 2/4/18.
 */

class MapActivity : AppCompatActivity() {

    private val sites = ArrayList<DoubleArray>()

//    // The below is fast
//    companion object {
//        val NORTH = -75.17261900652977
//        val WEST = 39.9639998777094
//        val SOUTH = -75.12462846235614
//        val EAST = 39.93699709962642
//    }
//
//    init {
//        sites.add(doubleArrayOf(-75.1468350, 39.9474180))
//        sites.add(doubleArrayOf(-75.1472000, 39.9482000))
//        sites.add(doubleArrayOf(-75.1437980, 39.9508290))
//        sites.add(doubleArrayOf(-75.1479650, 39.9523130))
//    }


    // In all usage below, x is coordinate[1], y is coordinate[0]

    // The below is slow
    companion object {
        val NORTH = 0.0
        val WEST = 0.0
        val SOUTH = 100.0
        val EAST = 100.0
    }

    init {
        sites.add(doubleArrayOf(25.1, 25.9))
        sites.add(doubleArrayOf(26.1, 33.9))
        sites.add(doubleArrayOf(32.1, 30.9))
        sites.add(doubleArrayOf(35.1, 18.9))
        sites.add(doubleArrayOf(25.1, 25.9))
        sites.add(doubleArrayOf(25.1, 44.9))
        sites.add(doubleArrayOf(75.1, 55.9))
        sites.add(doubleArrayOf(75.1, 88.9))
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val infoView = TextView(this)
        infoView.setPadding(100, 100, 100, 100)
        infoView.setBackgroundColor(Color.GRAY)

        val tileView = findViewById<TileView>(R.id.tileview)
        TileView.Builder(tileView)
            .setSize(17934, 13452)
            .defineZoomLevel("tiles/starfield-0-0_0.jpg")
            .defineZoomLevel(1, "tiles/starfield-1-0_0.jpg")
            .defineZoomLevel(2, "tiles/starfield-2-0_0.jpg")
            .installPlugin(MarkerPlugin(this))
            .installPlugin(InfoWindowPlugin(infoView))
            .installPlugin(CoordinatePlugin(WEST, NORTH, EAST, SOUTH))
            .installPlugin(HotSpotPlugin())
            .installPlugin(PathPlugin())
            //.addReadyListener { this.onReady(it) }
            .build()

    }

//
//    private fun onReady(tileView: TileView) {
//
//        val coordinatePlugin = tileView.getPlugin(CoordinatePlugin::class.java)
//        val infoWindowPlugin = tileView.getPlugin(InfoWindowPlugin::class.java)
//        val hotSpotPlugin = tileView.getPlugin(HotSpotPlugin::class.java)
//        val markerPlugin = tileView.getPlugin(MarkerPlugin::class.java)
//
//        // drop some markers, with info window expansions
//        val template = "Clicked marker at:\n%1\$f\n%2\$f"
//        val markerClickListener = View.OnClickListener{ view ->
//            val coordinate = view.getTag() as DoubleArray
//            val x = coordinatePlugin.longitudeToX(coordinate[1])
//            val y = coordinatePlugin.latitudeToY(coordinate[0])
//            tileView.smoothScrollTo(x - tileView.measuredWidth / 2, y - tileView.measuredHeight / 2)
//            val label = String.format(Locale.US, template, coordinate[0], coordinate[1])
//            val infoView = infoWindowPlugin.getView<TextView>()
//            infoView.text = label
//            infoWindowPlugin.show(x, y, -0.5f, -1f)
//        }
//
//        for (coordinate in sites) {
//            val x = coordinatePlugin.longitudeToX(coordinate[1])
//            val y = coordinatePlugin.latitudeToY(coordinate[0])
//            val marker = ImageView(this)
//            marker.tag = coordinate
//            marker.setImageResource(R.drawable.star)
//            marker.setOnClickListener(markerClickListener)
//            markerPlugin.addMarker(marker, x, y, -0.5f, -1f, 0f, 0f)
//        }

        // draw a path
//        val paint = Paint()
//        paint.style = Paint.Style.STROKE
//        paint.color = -0xbd790c
//        paint.strokeWidth = 0f
//        paint.isAntiAlias = true
//
//        val metrics = resources.displayMetrics
//        paint.setShadowLayer(
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, metrics),
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, metrics),
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, metrics),
//                0x66000000)
//        paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, metrics)
//        paint.pathEffect = CornerPathEffect(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, metrics))
//
//        val points = ArrayList<Point>()
//        for (coordinate in sites) {
//            val point = Point()
//            point.x = coordinatePlugin.longitudeToX(coordinate[1])
//            point.y = coordinatePlugin.latitudeToY(coordinate[0])
//            points.add(point)
//        }
//
//        val pathPlugin = tileView.getPlugin(PathPlugin::class.java)
//        pathPlugin.drawPath(points, paint)
//
//        // hotspot
//        val hotSpot = hotSpotPlugin.addHotSpot(points) { h -> Log.d("TV", "hot spot touched: " + h.tag) }
//        hotSpot.tag = "Any piece of data..."

        // frame it
//        val coordinate = sites[0]
//        val x = coordinatePlugin.longitudeToX(coordinate[1])
//        val y = coordinatePlugin.latitudeToY(coordinate[0])
//        tileView.scrollTo(x, y)

//    }



}
