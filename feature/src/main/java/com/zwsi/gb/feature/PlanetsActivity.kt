package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import com.zwsi.gblib.GBTest

class PlanetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planets)

        val homePlanetImageView = findViewById<ImageView>(R.id.homeplanet)

        val d = BitmapFactory.decodeResource(getResources(), R.drawable.desert)
        val f = BitmapFactory.decodeResource(getResources(), R.drawable.forest)
        val g = BitmapFactory.decodeResource(getResources(), R.drawable.gas)
        val i = BitmapFactory.decodeResource(getResources(), R.drawable.ice)
        val l = BitmapFactory.decodeResource(getResources(), R.drawable.land)
        val m = BitmapFactory.decodeResource(getResources(), R.drawable.mountain)
        val r = BitmapFactory.decodeResource(getResources(), R.drawable.rock)
        val w = BitmapFactory.decodeResource(getResources(), R.drawable.water)

        val merged = Bitmap.createBitmap(300, 200, d.config)
        val canvas = Canvas(merged)
        canvas.drawBitmap(i, 0f, 0f, null)
        canvas.drawBitmap(i, 50f, 0f, null)
        canvas.drawBitmap(i, 150f, 0f, null)
        canvas.drawBitmap(m, 200f, 0f, null)
        canvas.drawBitmap(m, 250f, 0f, null)
        canvas.drawBitmap(i, 300f, 0f, null)
        canvas.drawBitmap(l, 0f, 50f, null)
        canvas.drawBitmap(l, 50f, 50f, null)
        canvas.drawBitmap(l, 150f, 50f, null)
        canvas.drawBitmap(f, 200f, 50f, null)
        canvas.drawBitmap(f, 250f, 50f, null)
        canvas.drawBitmap(w, 300f, 50f, null)
        canvas.drawBitmap(w, 0f, 100f, null)
        canvas.drawBitmap(l, 50f, 100f, null)
        canvas.drawBitmap(l, 150f, 100f, null)
        canvas.drawBitmap(l, 200f, 100f, null)
        canvas.drawBitmap(i, 250f, 100f, null)
        canvas.drawBitmap(w, 300f, 100f, null)
        canvas.drawBitmap(w, 0f, 100f, null)
        canvas.drawBitmap(r, 50f, 100f, null)
        canvas.drawBitmap(r, 150f, 100f, null)
        canvas.drawBitmap(i, 200f, 100f, null)
        canvas.drawBitmap(i, 250f, 100f, null)
        canvas.drawBitmap(i, 300f, 100f, null)

        homePlanetImageView.setImageBitmap(merged)

        // Now add the other planets below
        val linearLayout1 = findViewById(R.id.planetsLinearLayout) as LinearLayout

        for (x in 0..2) {
            val planet = ImageView(this)

            val merged = Bitmap.createBitmap(300, 200, d.config)
            val canvas = Canvas(merged)
            canvas.drawBitmap(i, 0f, 0f, null)
            canvas.drawBitmap(i, 50f, 0f, null)
            canvas.drawBitmap(i, 150f, 0f, null)
            canvas.drawBitmap(m, 200f, 0f, null)
            canvas.drawBitmap(m, 250f, 0f, null)
            canvas.drawBitmap(i, 300f, 0f, null)
            canvas.drawBitmap(l, 0f, 50f, null)
            canvas.drawBitmap(l, 50f, 50f, null)
            canvas.drawBitmap(l, 150f, 50f, null)
            canvas.drawBitmap(f, 200f, 50f, null)
            canvas.drawBitmap(f, 250f, 50f, null)
            canvas.drawBitmap(w, 300f, 50f, null)

            planet.setImageBitmap(merged)

            linearLayout1.addView(planet)
        }




        val universe = GBTest.getUniverse()
        val stars = universe.getStars()
        for (s in stars) {
            val planets = universe.getPlanets(s)
            for (p in planets) {
                val sectors = universe.getSectors(p)
                for (h in 0 until sectors.size-1) {
                    for (w in 0 until sectors[h].size-1) {
                        print(sectors[h][w].toString())
                    }
                }


            }
        }


    }
}
