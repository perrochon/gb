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

        val intent = getIntent()
        val raceID = intent.getIntExtra("race", -1)

        val imageView = findViewById<ImageView>(R.id.RaceView)

        if (raceID == 0)
            imageView.setImageResource(R.drawable.xenost)
        else
            imageView.setImageResource(R.drawable.impit)


        val universe = GBController.universe
        val races = universe!!.racesArray
        val r = races[raceID]

        var stats = findViewById<TextView>(R.id.RaceStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Name : " + (r!!.name) +"\n")
        stats.append("Type : " + (r.birthrate) +"\n")
        stats.append("Size : " + (r.explore) +"\n")
        stats.append("Owner: " + (r.absorption) +"\n")

        stats = findViewById<TextView>(R.id.RaceBackground)
        paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append(r.description)

    }
}
