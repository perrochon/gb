package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBTest

class PlanetActivity : AppCompatActivity() {

    lateinit var p: GBPlanet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_planet)

        // Get Bitmaps - TODO factor out, this code exists twice. But where to?
        val d = BitmapFactory.decodeResource(getResources(), R.drawable.desert)
        val f = BitmapFactory.decodeResource(getResources(), R.drawable.forest)
        val g = BitmapFactory.decodeResource(getResources(), R.drawable.gas)
        val i = BitmapFactory.decodeResource(getResources(), R.drawable.ice)
        val l = BitmapFactory.decodeResource(getResources(), R.drawable.land)
        val m = BitmapFactory.decodeResource(getResources(), R.drawable.mountain)
        val r = BitmapFactory.decodeResource(getResources(), R.drawable.rock)
        val w = BitmapFactory.decodeResource(getResources(), R.drawable.water)
        val bitmaps = arrayOf(w,l,g,d,m,f,i,r)

        val intent = getIntent()
        val starID = intent.getIntExtra("star", -1)
        val planetID = intent.getIntExtra("planet", -1)

        val universe = GBTest.universe
        val stars = universe!!.getStars()
        val planets = universe!!.getPlanets(stars[starID])
        p = planets[planetID]

        // Get the View to draw planet on, then draw planet
        //
        var planetView = findViewById<ImageView>(R.id.PlanetView)
        val merged = Bitmap.createBitmap(p.width *50, p.height *50, d.config)
        var canvas = Canvas(merged)

        val planetStats = findViewById<TextView>(R.id.PlanetStats)
        val color = planetStats.textColors
        val size = planetStats.textSize
        val paint = planetStats.paint


        val sectors = universe.getSectors(p)
        for (h in 0 until sectors.size) {
            for (w in 0 until sectors[h].size) {
                canvas.drawBitmap(bitmaps[sectors[h][w].type],w*50f,h*50f,null)
                val population = sectors[h][w].population
                if (population > 0) {
                    canvas.drawText(population.toString(), w * 50f, h * 50f + 40f, paint)
                }
            }
        }

        planetView.setImageBitmap(merged)

        planetStats.setText("\n")
        planetStats.append("Name :" + p.name + "\n")
        planetStats.append("System :" + stars[0].name + "\n")
        planetStats.append("Type :" + p.type + "\n")
        planetStats.append("Size :" + p.size + "\n")
        planetStats.append("Owner:" + p.ownerName + "\n")

    }

    /** Called when the user taps the Planets button */
    fun colonize0(view: View) {
        val universe = GBTest.universe
        universe!!.landPopulation(p, 0)
    }

    /** Called when the user taps the Stars button */
    fun colonize1(view: View) {
        val universe = GBTest.universe
        universe!!.landPopulation(p, 1)

    }

}
