package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.distance
import android.graphics.drawable.Drawable
import android.util.AndroidException
import android.widget.RadioButton
import android.widget.RadioGroup




class DestinationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination)

        val uidShip = intent.extras.getInt("uidShip")
        val ship = vm.ship(uidShip)

        val linearLayout = findViewById<LinearLayout>(R.id.Destinations)

        val rg = RadioGroup(this) //create the RadioGroup
        rg.orientation = RadioGroup.VERTICAL

        var destination : RadioButton

        val shipStar = ship.getStar() // FIXME This likely returns a server object.... here and elsewhere...
        val shipPlanet = ship.loc.getPlanet()
        val planetDrawable = getDrawable(R.drawable.planet) // FIXME Get this from bitmap factory?

        if (shipStar != null) {
            for (p in shipStar.starUidPlanets.map { vm.planet(it) }) {
                destination = RadioButton(this)
                destination.text = "Planet ${p.name}"
                destination.height = 150
                if (shipPlanet != null && p.uid == shipPlanet.uid) {
//                    destination.text("(this planet)") // FIXME This crashes
                }
                destination.setCompoundDrawablesWithIntrinsicBounds(planetDrawable, null, null, null)
                destination.setOnClickListener(View.OnClickListener {
                    destinationClicked(it)
                })
                rg.addView(destination)
            }
        }

        val sortedStars =
            vm.stars.toList().sortedBy { (_, s) ->
                s.loc.getLoc().distance(ship.loc.getLoc())
            }.take(5)

        val starDrawable = getDrawable(R.drawable.star) // FIXME Get this from bitmap factory?
        for ((_, star) in sortedStars) {
            destination = RadioButton(this)
            destination.text = "System ${star.name}"
            destination.height = 150
            if (shipStar != null && star.uid == shipStar.uid) {
//                destination.append("(this system)") // FIXME This crashes
            }
            destination.setCompoundDrawablesWithIntrinsicBounds(starDrawable, null, null, null)
            destination.setOnClickListener(View.OnClickListener {
                destinationClicked(it)
            })
            rg.addView(destination)
        }

        linearLayout.addView(rg)

    }

    private fun destinationClicked(button: View) {


    }

}
