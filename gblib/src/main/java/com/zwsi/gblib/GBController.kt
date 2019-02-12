package com.zwsi.gblib

import java.util.concurrent.locks.ReentrantLock
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

        val u: GBUniverse
            get() {
                if (_u == null) {
                    _u = makeUniverse(numberOfStars, numberOfRaces)
                }
                return _u ?: throw AssertionError("Set to null by another thread")
            }

        fun makeUniverse(stars: Int = numberOfStars, races: Int = numberOfRaces): GBUniverse {
            _u = GBUniverse(stars, races)
            _u!!.makeStarsAndPlanets()
            _u!!.makeRaces()
            GBLog.d("Universe made with $stars stars")
            return _u!!
        }

        fun makeSmallUniverse(): GBUniverse {
            return makeUniverse(numberOfStarsSmall, numberOfRaces)
        }

        fun makeBigUniverse(): GBUniverse {
            return makeUniverse(numberOfStarsBig, numberOfRaces)
        }

        fun doUniverse() {
            GBLog.i("Runing Game Turn ${_u!!.turn}")
            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u!!.doUniverse()
                }
            } finally {
                lock.unlock()
            }
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

    }
}
