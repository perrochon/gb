package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.PI
import kotlin.system.measureNanoTime

class GBController {

    companion object {

        // gblib is not thread safe. lock is used to synchronize access.
        // Currently only locking for access to collections (e.g. for copying to ViewModel) which throw exceptions
        // Not locking for reading stars, ships, planets and such. Stale information is ok to read.
        // TODO QUALITY Review all the locking
        val lock = ReentrantLock()

        val numberOfStars = 24
        val numberOfRaces = 4   // <= what we have in GBData. >= 4 or tests will fail

        // Small universe has some hard coded rules around how many stars and how many planets per systems
        // Do not change these values...
        val numberOfStarsSmall = 5

        // Big Universe just has a lot of stars, but is otherwise the same as regular sized
        val numberOfStarsBig = 100

        var elapsedTimeLastUpdate = 0L

        private var _u: GBUniverse? = null

        val u: GBUniverse // FIXME PERSISTENCE make this internal
            get() {
                if (_u == null) {
                    makeUniverse(numberOfStars, numberOfRaces)
                }
                return _u ?: throw AssertionError("Failed to make")
            }

        fun makeUniverse(stars: Int = numberOfStars, races: Int = numberOfRaces): String {
            GBLog.i("Making Universe with $stars stars")
            var json: String? = null;
            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u = GBUniverse(stars, races)
                    _u!!.makeStarsAndPlanets()
                    _u!!.makeRaces()
                    json = Save()
                    // PERF without reload single digit ms update time, with reload low 100's ms update time.
                }
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            GBLog.d("Universe made with $stars stars")
            return json ?: throw AssertionError("Json with saved game is null")

            // FIXME Need to build a unit test that makes sure the json we send out here is consistent, and enough.

        }

        fun makeSmallUniverse(): String {
            return makeUniverse(numberOfStarsSmall, numberOfRaces)
        }

        fun makeBigUniverse(): String {
            return makeUniverse(numberOfStarsBig, numberOfRaces)
        }

        // Accessors to various lists.
        // TODO Limit visibility here to what each race can see
        // TODO More defensive coding in terms of lock? We assume these are called only from within a locked section

        fun getAllStarsMap(): Map<Int, GBStar> {
            return _u!!.allStars.toMap()
        }

        fun getAllPlanetsMap(): Map<Int, GBPlanet> {
            return _u!!.allPlanets.toMap()
        }

        fun getAllRacesMap(): Map<Int, GBRace> {
            return _u!!.allRaces.toMap()
        }

        fun getAllShipsMap(): Map<Int, GBShip> {
            return _u!!.allShips.toMap()
        }

        fun getAllDeepSpaceUidShipsList(): List<Int> {
            return _u!!.deepSpaceUidShips.toList()
        }

        fun getStarPlanetsList(uidStar: Int): List<GBPlanet> {
            return _u!!.star(uidStar).starUidPlanetSet.map { u.planet(it) }
        }

        fun getStarShipList(uidStar: Int): List<GBShip> {
            return _u!!.star(uidStar).starUidShipSet.map { u.ship(it) }
        }

        fun getPlanetLandedShipsList(uidPlanet: Int): List<GBShip> {
            return _u!!.planet(uidPlanet).landedUidShips.map { u.ship(it) }
        }

        fun getPlanetOrbitShipsList(uidPlanet: Int): List<GBShip> {
            return _u!!.planet(uidPlanet).orbitUidShips.map { u.ship(it) }
        }

        fun getRaceShipsList(uidRace: Int): List<GBShip> {
            return _u!!.race(uidRace).raceShipsUIDList.map { u.ship(it) }
        }

        fun makeStuff() {
            //playBeetle()
            //playImpi()
            //playTortoise()
        }

        fun doUniverse(): String {
            GBLog.i("Runing Game Turn ${_u!!.turn}")
            var json: String? = null;
            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u!!.doUniverse()
                    json = Save()
                    // PERF without reload single digit ms update time, with reload low 100's ms update time.
                }
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            return json ?: throw AssertionError("Json with saved game is null")
        }

        fun Save(): String {
            val moshi = Moshi.Builder().build()
            val gameInfo = GBSavedGame("Current Game", u)
            val jsonAdapter: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
            val json = jsonAdapter.toJson(gameInfo)
            // SERVER Want to save in the controller, but Controller has no Android access so can't find the directory
            // For now we just save from an app module
            //File(context.filesDir, "CurrentGame.json").writeText(json)
            return json
        }

        fun makeFactory(p: GBPlanet, race: GBRace) {
            GBLog.d("universe: Making factory for ${race.name} on ${p.name}.")

            val loc =
                GBLocation(p, GBData.rand.nextInt(p.width), GBData.rand.nextInt(p.height)) // TODO Have caller give us a better location?

            val order = GBOrder()

            order.makeFactory(loc, race)

            GBLog.d("Order made: " + order.toString())

            u.orders.add(order)

            GBLog.d("Current Orders: " + u.orders.toString())
        }

        fun makePod(factory: GBShip) {
            GBLog.d("universe: Making Pod for ?? in Factory " + factory.name + "")

            val order = GBOrder()
            order.makePod(factory)

            GBLog.d("Pod ordered: " + order.toString())

            u.orders.add(order)

            GBLog.d("Current Orders: " + u.orders.toString())
        }

        fun makeCruiser(factory: GBShip) {
            GBLog.d("universe: Making Cruiser for ?? in Factory " + factory.name + "")

            val order = GBOrder()
            order.makeCruiser(factory)

            GBLog.d("Cruiser ordered: " + order.toString())

            u.orders.add(order)

            GBLog.d("Current Orders: " + u.orders.toString())
        }


        fun flyShipLanded(sh: GBShip, p: GBPlanet) {
            GBLog.d("Setting Destination of " + sh.name + " to " + p.name)

            val loc = GBLocation(p, 0, 0) // TODO Have caller give us a better location?
            sh.dest = loc

        }

        fun flyShipOrbit(sh: GBShip, p: GBPlanet) {
            GBLog.d("Setting Destination of " + sh.name + " to " + p.name)

            val loc = GBLocation(
                p,
                GBData.PlanetaryOrbit,
                GBData.rand.nextFloat() * 2 * PI.toFloat()
            ) // TODO Have caller give us a better location?
            sh.dest = loc

        }

        fun landPopulation(p: GBPlanet, uidRace: Int, number: Int) {
            GBLog.d("universe: Landing 100 of " + u.race(uidRace).name + " on " + p.name + "")
            p.landPopulationOnEmptySector(u.race(uidRace), number)
        }


    }
}
