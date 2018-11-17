package com.zwsi.gb.feature

import android.content.Intent
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
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP



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
        val stars = universe!!.stars
        val planets = universe!!.getPlanets(stars[starID]!!)
        p = planets[planetID]!!

        // Get the View to draw planet on, then draw planet
        //
        var planetView = findViewById<ImageView>(R.id.PlanetView)
        val merged = Bitmap.createBitmap(p.width *50, p.height *50, d.config)
        var canvas = Canvas(merged)

        val planetStats = findViewById<TextView>(R.id.PlanetStats)
        val paint = planetStats.paint
        paint.textSize = 20f


        for (i in 0 until p.sectors.size) {

            canvas.drawBitmap(bitmaps[p.sectors[i].type],p.sectorX(i) * 50f,p.sectorY(i) *50f,null)

            if (p.sectors[i].getPopulation() > 0) {
                canvas.drawText(
                    p.sectors[i].getPopulation().toString(),
                    p.sectorX(i) * 50f,
                    p.sectorY(i) * 50f + 40f,
                    paint
                )
                canvas.drawText(
                    p.sectors[i].getOwner()!!.name.substring(0,1),
                    p.sectorX(i) * 50f + 20,
                    p.sectorY(i) * 50f + 20f,
                    paint
                )

            }


        }

        planetView.setImageBitmap(merged)

        paint.textSize = 40f

        planetStats.append("\n")
        planetStats.append("Name :" + p.name + "\n")
        planetStats.append("System :" + stars[0]!!.name + "\n")
        planetStats.append("Type :" + p.type + "\n")
        planetStats.append("Size :" + p.size + "\n")
        planetStats.append("Owner:" + p.ownerName + "\n")

    }

    /** Called when the user taps the Planets button */
    fun colonize0(view: View) {
        val universe = GBTest.universe
        universe!!.landPopulation(p, 0)

        //Redraw  - This is ugly, we should just get the Planet View to redraw
        val intent = intent
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_NO_ANIMATION
        finish()
        startActivity(intent)

    }

    /** Called when the user taps the Stars button */
    fun colonize1(view: View) {
        val universe = GBTest.universe
        universe!!.landPopulation(p, 1)

        //Redraw  - This is ugly, we should just get the Planet View to redraw
        val intent = intent
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_NO_ANIMATION
        finish()
        startActivity(intent)

    }

}
