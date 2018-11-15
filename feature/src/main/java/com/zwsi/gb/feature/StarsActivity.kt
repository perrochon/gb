package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBTest
import com.zwsi.gblib.GBUniverse

class StarsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars)

        var universe = GBTest.universe

        var starField = findViewById<ImageView>(R.id.starField)

//        val  paint = Paint()
//        paint.color = android.R.color.holo_orange_light
//        paint.setTextSize(140f)

        val stats = findViewById<TextView>(R.id.template)
        val paint = stats.paint



        val bs = BitmapFactory.decodeResource(getResources(), R.drawable.star)

        var merged = Bitmap.createBitmap(universe!!.universeMaxX, universe!!.universeMaxY, bs.config);
        var canvas = Canvas(merged);

        val stars = universe!!.getStars()
        for (s in stars) {
            canvas.drawBitmap(bs, s.getX().toFloat(), s.getY().toFloat(), null)
            canvas.drawText(s.name,s.getX().toFloat() + 30, s.getY().toFloat()+10, paint)
        }

        starField.setImageBitmap(merged)

    }
}
