package com.zwsi.gb.feature

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
        var viewDeepSpaceShips = universe.getDeepSpaceShipsList()
        var viewDeadShips = universe.getDeadShipsList()
        var viewStarShips: ArrayList<List<GBShip>> = ArrayList()
        var viewOrbitShips: ArrayList<List<GBShip>> = ArrayList()
        var viewRaceShips: ArrayList<List<GBShip>> = ArrayList()

        var viewShipTrails: ArrayList<List<GBxy>> = ArrayList()

        var viewShots = universe.getAllShotsList()

        var timeModelUpdate = 0L
        var timeLastTurn = 0L

        var mapView: SubsamplingScaleImageView? = null


        var times = mutableMapOf<String, Long>()

        init {
            fillViewStarShips()
            fillViewShipTrails()
            update()
        }

        fun update() {
            timeModelUpdate = measureNanoTime {

                // Not updating stars and planets as those lists don't change
                // If we made a deep copy of stars and planets we would need to copy changed data, e.g. location

                // Ships
                times["A"] = measureNanoTime { viewShips = universe.getAllShipsList() }

                times["D"] = measureNanoTime { viewDeepSpaceShips = universe.getDeepSpaceShipsList() }

                times["+"] = measureNanoTime { viewDeadShips = universe.getDeadShipsList() }

                times["S"] = measureNanoTime { fillViewStarShips() }

                times["O"] = measureNanoTime { fillViewOrbitShips() }

                times["R"] = measureNanoTime { fillViewRaceShips() }

                times["T"] = measureNanoTime { fillViewShipTrails() }

                times["S"] = measureNanoTime { viewShots = universe.getAllShotsList() }

                timeLastTurn = GBController.elapsedTimeLastUpdate
            }

            // TODO convert all coordinates to source coordinates after updating? Saves a few multiplications

            mapView?.invalidate()

        }


        fun fillViewStarShips() {
            viewStarShips.clear()
            for (s in viewStars) {
                viewStarShips.add(s.uid, s.getStarShipsList())
            }
        }

        fun fillViewOrbitShips() {
            viewOrbitShips.clear()
            for (p in viewPlanets) {
                viewOrbitShips.add(p.uid, p.getOrbitShipsList())
            }
        }

        fun fillViewRaceShips() {
            viewRaceShips.clear()
            for (r in viewRaces) {
                viewRaceShips.add(r.uid, r.getRaceShipsList())
            }
        }

        fun fillViewShipTrails() {
            viewShipTrails.clear()
            for (sh in viewShips) {
                viewShipTrails.add(sh.uid, sh.getTrailList())
            }
        }

        /* TODO Why can't I filter viewShips by health > 0
        Why would "add" create this exception. Shouldn't the list take care of this? What is clear() doing?

    java.lang.IndexOutOfBoundsException: Index: 145, Size: 144
        at java.util.ArrayList.add(ArrayList.java:483)
        at com.zwsi.gb.feature.GBViewModel$Companion.fillViewShipTrails(GBViewModel.kt:101)
        at com.zwsi.gb.feature.GBViewModel$Companion.update(GBViewModel.kt:63)
         */

    }


}