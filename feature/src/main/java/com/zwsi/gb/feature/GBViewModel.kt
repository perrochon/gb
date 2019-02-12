package com.zwsi.gb.feature

import android.arch.lifecycle.MutableLiveData
import android.graphics.PointF
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBController.Companion.lock
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBxy
import kotlin.system.measureNanoTime


class GBViewModel {

    // Recommended is tying this to the life cycle of one (1) activity, and have different ones for each activity
    // However MapView requires a huge fraction of all the data, and most other views are subsets so it seems
    // wasteful to not share the ViewModel. That's why this is an app scoped singleton.
    //

    // All App classes need to go through ViewModel to access any data.
    // ViewModel copies all Stars, Planets, Races, Ships and all of their collections. // FIXME Really?

    companion object {


        // maybe live data will come from GBController or so, and pass it all over, and this will go away?
        val currentTurn by lazy { MutableLiveData<Int>() }

        var viewStars = GBController.getAllStarsMap()
        var viewPlanets = GBController.getAllPlanetsMap()
        var viewRaces = GBController.getAllRacesMap()

        var viewShips = GBController.getAllShipsMap()
        var viewDeepSpaceShips = GBController.getAllDeepSpaceUidShipsList()
        //var viewDeadShips = u.getDeadShipsList()


        // FIXME  THINK THIS THROUGH: If we "restore" on update (JSONify copy) we have copies of all stars, planets, etc.
        // And we don't need the below separate copies. If we don't "restore", we point to the original.
        // How does it work if we send over the wire? We copy each time?
        // "Copying" (save/restore) the whole bundle allows us to put the non-GodMode filters into GBLib.
        // So maybe going forward, we send a copy of the data on update.

        // PERF Likely, we want GBObjects below. They may take more memory, but operations should be faster on draw
        // FIXME We don't have a view of starPlanetLists...
        var viewStarPlanets: HashMap<Int, List<GBPlanet>> = HashMap()
        var viewStarShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewOrbitShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewLandedShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewRaceShips: HashMap<Int, List<GBShip>> = HashMap()
        var viewShipTrails: HashMap<Int, List<GBxy>> = HashMap()

        var viewShots = u.getAllShotsList()

        var timeModelUpdate = 0L
        var timeLastTurn = 0L

        var times = mutableMapOf<String, Long>()

        init {
            fillViewStarShips()
            fillViewShipTrails()
            fillViewOrbitShips()
            fillViewLandedShips()
            update()
        }

        fun update() {
            // This is (currently) called from the worker thread. So need to call postValue on the LiveData
            timeModelUpdate = measureNanoTime {

                lock.lock();  // lock for copying over data into view
                try {
                    // Not updating stars and planets as those lists don't change
                    // If we made a deep copy of stars and planets we would need to copy changed data, e.g. location
                    // If we save/reload stars each time, we need to get the new versions.

                    // Ships
                    times["mAS"] = measureNanoTime { viewStars = GBController.getAllStarsMap() }

                    times["mAP"] = measureNanoTime { viewPlanets = GBController.getAllPlanetsMap() }

                    times["mAR"] = measureNanoTime { viewRaces = GBController.getAllRacesMap() }

                    times["mAs"] = measureNanoTime { viewShips = GBController.getAllShipsMap() }

                    times["mDs"] = measureNanoTime { viewDeepSpaceShips = GBController.getAllDeepSpaceUidShipsList() }  // FIXME PERSISTENCE

                    //times["m+s"] = measureNanoTime { viewDeadShips = u.getDeadShipsList() }

                    times["mSP"] = measureNanoTime { fillViewStarPlanets() }

                    times["mSs"] = measureNanoTime { fillViewStarShips() }

                    times["mOs"] = measureNanoTime { fillViewOrbitShips() }

                    times["mLs"] = measureNanoTime { fillViewLandedShips() }

                    times["mRs"] = measureNanoTime { fillViewRaceShips() }

                    times["mSt"] = measureNanoTime { fillViewShipTrails() }

                    times["msh"] = measureNanoTime { viewShots = u.getAllShotsList() }
                } finally {
                    lock.unlock()
                }
            }


            timeLastTurn = GBController.elapsedTimeLastUpdate

            /*
            Note: You must call the setValue(T) method to update the LiveData object from the main thread.
            If the code is executed in a worker thread, you can use the postValue(T) method instead
            to update the LiveData object.
             */
            // FIXME why is this not setValue
            currentTurn.postValue(u.turn)

        }

        fun fillViewStarPlanets() {
            viewStarPlanets.clear()
            for ((_, s) in viewStars) {
                viewStarPlanets.put(s.uid, GBController.getStarPlanetsList(s.uid))
            }
        }

        fun fillViewStarShips() {
            viewStarShips.clear()
            for ((_, s) in viewStars) {
                viewStarShips.put(s.uid, GBController.getStarShipList(s.uid))
            }
        }

        fun fillViewOrbitShips() {  // PERF combine with the next and iterate only once
            viewOrbitShips.clear()
            for ((_, p) in viewPlanets) {
                viewOrbitShips.put(p.uid, GBController.getPlanetOrbitShipsList(p.uid))
            }
        }

        fun fillViewLandedShips() {
            viewLandedShips.clear()
            for ((_, p) in viewPlanets) {
                viewLandedShips.put(p.uid, GBController.getPlanetLandedShipsList(p.uid))
            }
        }

        fun fillViewRaceShips() {
            viewRaceShips.clear()
            for ((_, race) in viewRaces) {
                viewRaceShips.put(race.uid, race.raceShipsList)
            }
        }

        fun fillViewShipTrails() {
            viewShipTrails.clear()
            for ((_, s) in viewShips.filterValues { it.health > 0 }) {
                viewShipTrails.put(s.uid, s.trailList)
            }
        }

    }


}