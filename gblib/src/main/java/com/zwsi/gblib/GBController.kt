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

        // Where to store the snapshot after every turn
        var currentFilePath: File? = null

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
//                if (_u == null) {
//                    makeUniverse(numberOfStars, numberOfRaces)
//                }
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
                    json = saveUniverse() // FIXME Take this out of lock. So need to move lock into try block
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
            return _u!!.stars.toMap()
        }

        fun getAllPlanetsMap(): Map<Int, GBPlanet> {
            return _u!!.planets.toMap()
        }

        fun getAllRacesMap(): Map<Int, GBRace> {
            return _u!!.races.toMap()
        }

        fun getAllShipsMap(): Map<Int, GBShip> {
            return _u!!.ships.toMap()
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
                    json = saveUniverse()
                    json = loadUniverse()
                    // PERF without reload single digit ms update time, with reload low 100's ms update time.
                }
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            return json ?: throw AssertionError("Json with saved game is null")
        }

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<GBUniverse> = moshi.adapter(GBUniverse::class.java).indent("  ")

        fun saveUniverse(): String {
            val gameInfo = GBSavedGame("Current Game", u)
            val json = jsonAdapter.toJson(_u)
            // SERVER Want to save in the controller, but Controller has no Android access so can't find the directory
            // For now we just save from an app module
            // The below only works in unit tests
            // FIXME PERSISTENCE set currentFilePath to a good default so no longer need the below.
            if (currentFilePath == null) {
                File("CurrentGame.json").writeText(json)
            } else {
                File(currentFilePath, "CurrentGame.json").writeText(json)
            }
            return json
        }

        fun loadUniverse(): String {
            val json: String
            if (currentFilePath == null) {
                json = File("CurrentGame.json").readText()
            } else {
                json = File(currentFilePath, "CurrentGame.json").readText()
            }
            val newUniverse: GBUniverse = jsonAdapter.lenient().fromJson(json)!!

            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u = newUniverse// get rid of old Universe
                }
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            GBLog.d("Universe loaded with ${u.numberOfStars} stars")
            return json ?: throw AssertionError("Json with saved game is null")

            // FIXME Need to build a unit test that makes sure the json we send out here is consistent, and enough.

        }

        fun loadUniverse(json: String) {

            val newUniverse: GBUniverse = jsonAdapter.lenient().fromJson(json)!!

            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u = newUniverse// get rid of old Universe
                }
            } finally {
                lock.unlock()
            }
            // SERVER Add Fog of War filters here before we return the data (if we worry about cheaters)
            GBLog.d("Universe loaded with ${u.numberOfStars} stars")

        }


        // FIXME PERSISTENCE Put locks inside these calls

        fun makeFactory(uidPlanet: Int, uidRace: Int) {

            val planet = u.planet(uidPlanet)
            val race = u.race(uidRace)

            GBLog.d("universe: Making factory for ${race.name} on ${planet.name}.")

            // TODO Have caller give us a better location (or find one ourselves) for factory ?
            val loc =
                GBLocation(planet, GBData.rand.nextInt(planet.width), GBData.rand.nextInt(planet.height))

            val order = GBOrder()

            order.makeFactory(loc, race)

            GBLog.d("Order made: " + order.toString())

            u.orders.add(order)

            GBLog.d("Current Orders: " + u.orders.toString())
        }

        fun makePod(uidFactory: Int) {

            val factory = u.ship(uidFactory)

            GBLog.d("universe: Making Pod for ?? in Factory " + factory.name + "")

            val order = GBOrder()
            order.makePod(factory)

            GBLog.d("Pod ordered: " + order.toString())

            u.orders.add(order)

            GBLog.d("Current Orders: " + u.orders.toString())
        }

        fun makeCruiser(uidFactory: Int) {

            val factory = u.ship(uidFactory)

            GBLog.d("universe: Making Cruiser for ?? in Factory " + factory.name + "")

            val order = GBOrder()
            order.makeCruiser(factory)

            GBLog.d("Cruiser ordered: " + order.toString())

            u.orders.add(order)

            GBLog.d("Current Orders: " + u.orders.toString())
        }

        // FIXME Which object gets updated here? The one in u, or the one in allships?
        // Need to update the view model, because people may click on it again...

        fun flyShipLanded(uidShip: Int, uidPlanet: Int) {

            val ship = u.ship(uidShip)
            val planet = u.planet(uidPlanet)

            GBLog.d("Setting Destination of " + ship.name + " to " + planet.name)

            val loc = GBLocation(planet, 0, 0) // TODO Have caller give us a better location?
            ship.dest = loc

        }

        fun flyShipOrbit(uidShip: Int, uidPlanet: Int) {

            val ship = u.ship(uidShip)
            val planet = u.planet(uidPlanet)

            GBLog.d("Setting Destination of " + ship.name + " to " + planet.name)

            // TODO Have caller give us a better location?
            val loc = GBLocation(planet, GBData.PlanetaryOrbit, GBData.rand.nextFloat() * 2 * PI.toFloat())
            ship.dest = loc
        }

        fun landPopulation(uidPlanet: Int, uidRace: Int, number: Int) {

            val planet = u.planet(uidPlanet)
            val race = u.race(uidRace)

            GBLog.d("universe: Landing 100 of " + race.name + " on " + planet.name + "")
            planet.landPopulationOnEmptySector(race, number)
        }
    }
}
