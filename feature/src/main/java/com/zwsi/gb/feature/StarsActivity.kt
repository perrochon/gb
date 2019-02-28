package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.vm

class StarsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars)

        val starField = findViewById<ImageView>(R.id.starField)

//        val  paint = Paint()
//        paint.color = android.R.color.holo_orange_light
//        paint.setTextSize(140f)

        val stats = findViewById<TextView>(R.id.template)
        val paint = stats.paint



        val bs = BitmapFactory.decodeResource(getResources(), R.drawable.star)

        val merged = Bitmap.createBitmap(vm.universeMaxX, vm.universeMaxY, bs.config);
        val canvas = Canvas(merged);

        for ((_, s) in vm.stars) {
            canvas.drawBitmap(bs, s.loc.getLoc().x, s.loc.getLoc().y, null)
            canvas.drawText(s.name,s.loc.getLoc().x + 30, s.loc.getLoc().y + 10, paint)
        }

        starField.setImageBitmap(merged)

    }
}
