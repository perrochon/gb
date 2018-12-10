package com.zwsi.gb.feature

import android.arch.lifecycle.MutableLiveData
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBxy
import kotlin.system.measureNanoTime


class GBViewModel {

    // Recommended is tying this to the life cycle of one (1) activity, and have different ones for each activity
    // However MapView requires a huge fraction of all the data, and most other views are subsets so it seems
    // wasteful to not share the ViewModel. That's why this is an app scoped singleton.
    //

    companion object {

        // maybe live data will come from GBController or so, and pass it all over, and this will go away?
        val curentTurn by lazy {MutableLiveData<Int>()}

        var viewStars = universe.allStars
        var viewPlanets = universe.allPlanets
        var viewRaces = universe.allRaces

        var viewShips = universe.getAllShipsList()
        var viewDeepSpaceShips = universe.getDeepSpaceShipsList()
        var viewDeadShips = universe.getDeadShipsList()

        var viewStarShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewOrbitShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewRaceShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewShipTrails: HashMap<Int, List<GBxy>> = HashMap()

        var viewShots = universe.getAllShotsList()

        var timeModelUpdate = 0L
        var timeLastTurn = 0L

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

            curentTurn.value = universe.turn

            /*
            Note: You must call the setValue(T) method to update the LiveData object from the main thread. If the code is executed in a worker thread, you can use the postValue(T) method instead to update the LiveData object.
             */
        }

        fun fillViewStarShips() {
            viewStarShips.clear()
            for (s in viewStars) {
                viewStarShips.put(s.uid, s.getStarShipsList())
            }
        }

        fun fillViewOrbitShips() {
            viewOrbitShips.clear()
            for (p in viewPlanets) {
                viewOrbitShips.put(p.uid, p.getOrbitShipsList())
            }
        }

        fun fillViewRaceShips() {
            viewRaceShips.clear()
            for (r in viewRaces) {
                viewRaceShips.put(r.uid, r.getRaceShipsList())
            }
        }

        fun fillViewShipTrails() {
            viewShipTrails.clear()
            for (sh in viewShips.filter { it.health > 0 }) {
                viewShipTrails.put(sh.uid, sh.getTrailList())
            }
        }

    }


}