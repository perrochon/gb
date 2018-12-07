package com.zwsi.gb.feature

import android.app.Activity
import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBxy
import kotlin.system.measureNanoTime



class GBViewModel {

    companion object {

        var viewStars = universe.allStars
        var viewPlanets = universe.allPlanets
        var viewRaces = universe.allRaces

        var viewShips = universe.getAllShipsList()
        var viewUniverseShips = universe.getUniverseShipsList()
        var viewStarShips: ArrayList<List<GBShip>> = ArrayList()
        var viewOrbitShips: ArrayList<List<GBShip>> = ArrayList()
        var viewShipTrails: ArrayList<List<GBxy>> = ArrayList()

        var viewShots = universe.getAllShotsList()

        var lastTurn = -1
        var updateTimeTurn = 0L
        var elapsedBackendTimeTurn = 0L

        var mapView: SubsamplingScaleImageView? = null

        var times = mutableMapOf<String, Long>()

        init {
            fillViewStarShips()
            fillViewShipTrails()
            update()
        }

        fun update() {
            updateTimeTurn = measureNanoTime {

                // Not updating stars and planets as those lists don't change
                // TODO: Deep copy of stars and planets? Then copy changed data

                // Ships
                times["Ships"] = measureNanoTime { viewShips = universe.getAllShipsList() }

                times["UShips"] = measureNanoTime { viewUniverseShips = universe.getUniverseShipsList() }

                times["SShips"] = measureNanoTime { fillViewStarShips()}

                times["OShips"] = measureNanoTime { fillViewOrbitShips() }

               // times["Trails"] = measureNanoTime { fillViewShipTrails() }

                times["Shots"] = measureNanoTime { viewShots = universe.getAllShotsList()}

                elapsedBackendTimeTurn = GBController.elapsedTimeLastUpdate
            }

            // TODO convert all coordinates to source coordinates after updating? Saves a few multiplications

            if (mapView != null) {
                mapView!!.invalidate()
            }
        }

        fun fillViewStarShips() {
            viewStarShips.clear()
            for (s in viewStars) {
                viewStarShips.add(s.uid, s.getStarShipsList().filter { it.health > 0 })
            }
        }

        fun fillViewOrbitShips() {
            viewOrbitShips.clear()
            for (p in viewPlanets) {
                viewOrbitShips.add(p.uid, p.getOrbitShipsList().filter { it.health > 0 })
            }
        }

        fun fillViewShipTrails() {
            viewShipTrails.clear()
            for (sh in viewShips) {
                viewShipTrails.add(sh.uid, sh.getTrailList())
            }
        }

    }


}