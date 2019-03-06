package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBData.Companion.currentGameFileName
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.system.measureNanoTime

// GBController may be called from the UI and the background thread, so we need to lock.

class GBController {

    companion object {

        // gblib is not thread safe. lock is used to synchronize access.
        private val lock = ReentrantLock()

        // Where to store the snapshot after every turn
        var currentFilePath: File? = null

        internal val numberOfStars = 24 // TODO Get Rid of these 4 constants here. GBData may keep some
        internal val numberOfRaces = 4   // <= what we have in GBData. >= 4 or tests will fail

        // Small universe has some hard coded rules around how many stars and how many planets per systems
        // Do not change these values...
        internal val numberOfStarsSmall = 5

        // Big Universe just has a lot of stars, but is otherwise the same as regular sized
        internal val numberOfStarsBig = 100

        var elapsedTimeLastUpdate = 0L
        var elapsedTimeLastJSON = 0L
        var elapsedTimeLastWrite = 0L
        var elapsedTimeLastLoad = 0L

        private var _u: GBUniverse? = null
        val u: GBUniverse // TODO better without _u and a backing field?
            get() {
                return _u ?: throw AssertionError("We have no Universe")
            }

        fun makeAndSaveUniverse(secondPlayer: Boolean): String {

            val json: String
            lock.lock(); // lock for the game turn
            try {
                makeUniverse()
                u.secondPlayer = secondPlayer
                json = saveUniverse()
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            return json
        }

        internal fun makeSmallUniverse() {
            makeUniverse(numberOfStarsSmall, numberOfRaces)
        }

        internal fun makeBigUniverse() {
            makeUniverse(numberOfStarsBig, numberOfRaces)
        }

        internal fun makeUniverse(stars: Int = numberOfStars, races: Int = numberOfRaces) {
            GBLog.i("Making Universe with $stars stars")
            elapsedTimeLastUpdate = measureNanoTime {
                _u = GBUniverse(stars, races)
                _u!!.makeStarsAndPlanets()
                _u!!.makeRaces()
                // PERF without reload single digit ms update time, with reload low 100's ms update time.
            }
            GBLog.d("Universe made with $stars stars")
        }

//        fun makeStuff() {
//            //playBeetle()
//            //playImpi()
//            //playTortoise()
//        }

        fun doAndSaveUniverse(): String {

            val json: String
            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u!!.doUniverse()
                    // PERF without reload single digit ms update time, with reload low 100's ms update time.
                }
                json = saveUniverse()
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            return json
        }

        internal val moshi = Moshi.Builder().build()
        internal val jsonAdapter: JsonAdapter<GBUniverse> = moshi.adapter(GBUniverse::class.java).indent("  ")

        internal fun saveUniverse(): String {

            var json: String = ""
            lock.lock(); // lock while we build the JSON
            try {
                elapsedTimeLastJSON = measureNanoTime {
                    json = jsonAdapter.toJson(_u)!!
                }
            } finally {
                lock.unlock()
            }
            // SERVER Controller has no Android access so can't find the directory
            // TODO find a path in a better way (i.e. the FILE)
            elapsedTimeLastWrite = measureNanoTime {
                if (currentFilePath == null) {
                    File(currentGameFileName).writeText(json)
                } else {
                    File(
                        currentFilePath,
                        currentGameFileName
                    ).writeText(json)
                }
            }
            return json
        }

        fun loadUniverseFromJSON(json: String) {

            // TODO Gracefully handle old versions of saved games...
            // Now we might crash on loading the game, with no way for the user to delete the offending game other than
            // uninstall and re-install.

            var newUniverse: GBUniverse? = null
            elapsedTimeLastLoad = measureNanoTime {

                try {
                    newUniverse = jsonAdapter.lenient().fromJson(json)!!
                } catch (e: Exception) {
                    newUniverse = null
                    GBLog.e("Exception trying to load universe\n${e.toString()}\n$json")
                }
            }

            if (newUniverse != null) {
                lock.lock(); // lock while we switch out u...
                try {
                    _u = newUniverse // get rid of old Universe
                } finally {
                    lock.unlock()
                }
                GBLog.d("Universe loaded with ${u.numberOfStars} stars")
            } else {
                GBLog.e("Couldn't load Universe.")
            }
        }

        // Methods for in-turn orders to the backend. This code only updates the backend (u), not the frontend (vm).
        // These methods try to acquire a lock, so don't call them from inside the library/the worker thread.
        // Tests and App is ok.

        // TODO: DoAndSave can take 200ms. Thus the below calls may wait that long. May need to do Async.

        fun makeFactory(uidPlanet: Int, uidRace: Int) {
            GBController.lock.lock();
            try {
                val order = GBOrder()
                order.makeFactory(uidPlanet, uidRace)

                //GBLog.d("universe: Making factory for ${race.name} on ${planet.name}.")
                u.orders.add(order)

            } finally {
                GBController.lock.unlock()
            }
        }

        fun makePod(uidFactory: Int) {
            GBController.lock.lock();
            try {
                val order = GBOrder()
                order.makePod(uidFactory)

                //GBLog.d("universe: Making Cruiser for ${factory.race.name} in Factory ${factory.name}.")
                u.orders.add(order)

            } finally {
                GBController.lock.unlock()
            }

        }

        fun makeCruiser(uidFactory: Int) {
            GBController.lock.lock();
            try {
                val order = GBOrder()
                order.makeCruiser(uidFactory)

                //GBLog.d("universe: Making Cruiser for ${factory.race.name} in Factory ${factory.name}.")
                u.orders.add(order)
            } finally {
                GBController.lock.unlock()
            }

        }

        fun flyShipLanded(uidShip: Int, uidPlanet: Int) {

            GBController.lock.lock();
            try {
                val ship = u.ship(uidShip)
                val planet = u.planet(uidPlanet)
                val loc = GBLocation(planet, 0, 0) // determine exact location at arrival

                GBLog.d("Setting Destination of " + ship.name + " to " + planet.name)
                ship.dest = loc

            } finally {
                GBController.lock.unlock()
            }
        }

        fun flyShipOrbit(uidShip: Int, uidPlanet: Int) {

            GBController.lock.lock();
            try {
                val ship = u.ship(uidShip)
                val planet = u.planet(uidPlanet)
                val loc = GBLocation(planet, GBData.PlanetOrbit, 0f) // determine exact location at arrival

                GBLog.d("Setting Destination of " + ship.name + " to " + planet.name)
                ship.dest = loc

            } finally {
                GBController.lock.unlock()
            }
        }

        fun landPopulation(uidPlanet: Int, uidRace: Int, number: Int) {

            GBController.lock.lock();
            try {
                val planet = u.planet(uidPlanet)
                val race = u.race(uidRace)

                GBLog.d("universe: Landing 100 of " + race.name + " on " + planet.name + "")
                planet.landPopulationOnEmptySector(race, number)

            } finally {
                GBController.lock.unlock()
            }
        }
    }
}
