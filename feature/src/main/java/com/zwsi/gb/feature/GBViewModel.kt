package com.zwsi.gb.feature

import android.arch.lifecycle.MutableLiveData
import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.lock
import com.zwsi.gblib.GBController.Companion.u
import kotlin.system.measureNanoTime


class GBViewModel {

    // Recommended is tying this to the life cycle of one (1) activity, and have different ones for each activity
    // However MapView requires a huge fraction of all the data, and most other views are subsets so it seems
    // wasteful to not share the ViewModel. That's why this is an app scoped singleton.
    //

    // All App classes need to go through ViewModel to access any data.
    // ViewModel copies all Stars, Planets, Races, Ships and all of their collections. // FIXME Really?

    companion object {


        // FIXME PERSISTENCE this initialization
        // Also we don't really need Mutable lists, but we don't want another copy...

        // maybe live data will come from GBController or so, and pass it all over, and this will go away?
        val currentTurn by lazy { MutableLiveData<Int>() }

        lateinit var viewStars: MutableMap<Int, GBStar>
        lateinit var viewPlanets : MutableMap<Int, GBPlanet>
        lateinit var viewRaces : MutableMap<Int, GBRace>
        lateinit var viewShips : MutableMap<Int, GBShip>

        lateinit var viewDeepSpaceShips : List<GBShip>


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

        lateinit var viewShots : MutableList<GBVector>

        var timeModelUpdate = 0L
        var timeLastTurn = 0L
        var timeFileWrite = 0L
        var timeFromJson = 0L

        lateinit var gi : GBSavedGame

        var times = mutableMapOf<String, Long>()

        fun update(gameinfo: GBSavedGame, _elapsedTimeLastUpdate : Long, _writeFileTime: Long, _fromJsonTime: Long) {

            // This is (currently) called from the worker thread. So need to call postValue on the LiveData
            timeModelUpdate = measureNanoTime {

                gi = gameinfo

                timeLastTurn = _elapsedTimeLastUpdate
                timeFileWrite = _writeFileTime
                timeFromJson = _fromJsonTime


                lock.lock();  // lock for copying over data into view
                try {
                    // Not updating stars and planets as those lists don't change
                    // If we made a deep copy of stars and planets we would need to copy changed data, e.g. location
                    // If we save/reload stars each time, we need to get the new versions.

                    // FIXME: The below constructs should really be able to go away as gi has all that is needed
                    // Using gi is safe as it is not accessed from the other thread after initial construction.
                    // FIXME: Lock no longer needed here, really.

                    // Ships
                    times["mAS"] = measureNanoTime { viewStars = gi.starList!! }

                    times["mAP"] = measureNanoTime { viewPlanets = gi.planetList!! }

                    times["mAR"] = measureNanoTime { viewRaces = gi.raceList!! }

                    times["mAs"] = measureNanoTime { viewShips = gi.shipList!! }

                    times["mDs"] = measureNanoTime { getDeepSpaceShipsList() }

                    times["mPs"] = measureNanoTime { fillViewStarPlanetsAndShips() }

                    times["mLO"] = measureNanoTime { fillViewLandedAndOrbitShips() }

                    times["mRs"] = measureNanoTime { fillViewRaceShips() }

                    times["mst"] = measureNanoTime { fillViewShipTrails() }

                    times["msh"] = measureNanoTime { viewShots = gi.shots!! }
                } finally {
                    lock.unlock()
                }
            }

            /*
            Note: You must call the setValue(T) method to update the LiveData object from the main thread.
            If the code is executed in a worker thread, you can use the postValue(T) method instead
            to update the LiveData object.
             */
            // FIXME why is this not setValue
            currentTurn.postValue(gameinfo.turn)

        }

        fun getDeepSpaceShipsList() {
            // PERF ?? If this were a set, I think it would be one list copy less... This is a big list in demo mode
            viewDeepSpaceShips = viewShips.filter { it.value.loc.level == GBLocation.DEEPSPACE }.values.toList()
        }

        fun fillViewStarPlanetsAndShips() {
            viewStarPlanets.clear()
            for ((_, s) in viewStars) {
                viewStarPlanets.put(s.uid, s.starUidPlanetSet.map { viewPlanets[it]!! })
                viewStarShips.put(s.uid, s.starUidShipSet.map { viewShips[it]!! })
            }
        }

        fun fillViewLandedAndOrbitShips() {  // PERF combine with the next and iterate only once
            viewOrbitShips.clear()
            for ((_, p) in viewPlanets) {
                viewLandedShips.put(p.uid, p.landedUidShips.map { viewShips[it]!! })
                viewOrbitShips.put(p.uid, p.orbitUidShips.map { viewShips[it]!! })
            }
        }

        fun fillViewRaceShips() {
            viewRaceShips.clear()
            for ((_, race) in viewRaces) {
                viewRaceShips.put(race.uid, race.raceShipsUIDList.map { viewShips[it]!!})
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