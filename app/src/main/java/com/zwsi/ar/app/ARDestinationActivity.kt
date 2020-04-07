package com.zwsi.ar.app

import android.graphics.LightingColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.ar.app.GlobalStuff.Companion.handleClickInSelectionActivity
import com.zwsi.gblib.*
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.PATROL
import kotlinx.android.synthetic.main.activity_destination.*


class ARDestinationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination)

        text_version.text=BuildConfig.VERSIONNAME

        val uidShip = intent.extras!!.getInt("uidShip")
        val ship = vm.ship(uidShip)

        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(8, 8, 8, 8)

        val destinationsList = arrayListOf<Button>()

        val shipStar = ship.loc.getVMStar()
        // val shipPlanet = ship.loc.getVMPlanet() // TODO mark "current" planet, as well as current destination?

        var uidSelectedPlanet = -1
        var uidSelectedStar = -1

        if (ship.dest != null) {
            if (ship.dest!!.level == ORBIT || ship.dest!!.level == LANDED) {
                uidSelectedPlanet = ship.dest!!.getVMPlanet()!!.uid
            } else if (ship.dest!!.level == PATROL) {
                uidSelectedStar = ship.dest!!.getVMUidStar()
            }
        }

        if (shipStar != null) {
            for (p in shipStar.starUidPlanets.map { vm.planet(it) }) {

                val planetDrawable = getDrawable(p.getDrawableResource())!!

                val button = getButton(planetDrawable, "Planet ${p.name}")
                if (uidSelectedPlanet == p.uid) {
                    button.background.colorFilter = LightingColorFilter(0x55555, 0x774400)
                }
                button.setOnClickListener {
                    handleClickInSelectionActivity(it, destinationsList)
                    uidSelectedPlanet = p.uid
                    uidSelectedStar = -1
                }

                layout_destinations.addView(button, lp)
                destinationsList.add(button)
            }

        }

        val sortedStars =
            vm.stars.toList().sortedBy { (_, s) ->
                s.loc.getLoc().distance(ship.loc.getLoc())
            }.take(5)

        val starDrawable = getDrawable(R.drawable.star)

        for ((_, star) in sortedStars) {

            val button = getButton(starDrawable!!, "System ${star.name}")
            if (uidSelectedStar == star.uid) {
                button.background.colorFilter = LightingColorFilter(0x55555, 0x774400)
            }
            button.setOnClickListener {
                handleClickInSelectionActivity(it, destinationsList)
                uidSelectedPlanet = -1
                uidSelectedStar = star.uid

            }

            layout_destinations.addView(button, lp)
            destinationsList.add(button)
        }

        button_set.setOnClickListener {
            if (uidSelectedPlanet != -1) {

                val planet = vm.planet(uidSelectedPlanet)

                if (ship.idxtype == GBData.POD || ship.idxtype == GBData.SHUTTLE) {
                    GBController.flyShipLanded(ship.uid, planet.uid) // update server side
                    ship.dest = GBLocation(planet, 0, 0) // update vm
                } else {
                    GBController.flyShipPlanetOrbit(ship.uid, planet.uid)// update server side
                    ship.dest = GBLocation(planet, GBData.PlanetOrbit, 0f) // update vm
                }

            } else if (uidSelectedStar != -1) {
                val star = vm.star(uidSelectedStar)
                val patrolPoint: GBPatrolPoint
                if (GBData.rand.nextBoolean()) {
                    patrolPoint = vm.patrolPoint(star.starUidPatrolPoints.first())
                } else {
                    patrolPoint = vm.patrolPoint(star.starUidPatrolPoints.drop(1).first())
                }
                GBController.flyShipStarPatrol(ship.uid, patrolPoint.uid)// update server side
                ship.dest = GBLocation(patrolPoint, 0f, 0f) // update vm
            }
            GlobalStuff.updateActions(it)
            finish()
        }

        button_cancel.setOnClickListener {
            finish()
        }

    }

    private fun getButton(drawable: Drawable, text: String): Button {

        drawable.bounds = Rect(0, 0, 100, 100)

        val button = Button(this)
        button.transformationMethod = null
        button.text = text
        button.setBackgroundResource(R.drawable.border_message)
        button.setPadding(50, button.paddingTop, button.paddingRight, button.paddingBottom)
        button.setCompoundDrawables(drawable, null, null, null)
        return button
    }


}
