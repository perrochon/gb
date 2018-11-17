package com.zwsi.gb.feature

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import com.zwsi.gblib.GBController

class RaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        val imageView = findViewById<ImageView>(R.id.RaceView)
        imageView.setImageResource(R.drawable.impi)

        val stats = findViewById<TextView>(R.id.RaceStats)
        val paint = stats.paint
        paint.textSize = 40f

        val universe = GBController.universe
        val races = universe!!.racesArray
        val r = races[0]

        stats.append("\n")
        stats.append("Name : " + (r!!.name) +"\n")
        stats.append("Type : " + (r.birthrate) +"\n")
        stats.append("Size : " + (r.explore) +"\n")
        stats.append("Owner: " + (r.absorption) +"\n")

    }
}
