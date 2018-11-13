package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class StarsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars)


        var starField = findViewById<ImageView>(R.id.starField)

        val s = BitmapFactory.decodeResource(getResources(), R.drawable.star)

        var merged = Bitmap.createBitmap(1000, 1000, s.config);
        var canvas = Canvas(merged);

        canvas.drawBitmap(s, 500f, 500f, null)
        canvas.drawBitmap(s, 250f, 750f, null)
        canvas.drawBitmap(s, 600f, 300f, null)

        starField.setImageBitmap(merged)

    }
}
