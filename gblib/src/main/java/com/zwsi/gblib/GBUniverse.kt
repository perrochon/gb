package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.PlanetaryOrbit
import com.zwsi.gblib.GBData.Companion.rand
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import java.util.*
import kotlin.math.PI

class GBUniverse(internal var numberOfStars: Int, val numberOfRaces: Int) {

    //numberOfRaces <= what we have in GBData. >= 4 or tests will fail

    val universeMaxX: Int
        get() = GBData.UniverseMaxX

    val universeMaxY: Int
        get() = GBData.UniverseMaxY

    val systemBoundary: Float
        get() = GBData.SystemBoundary

    val planetaryOrbit: Float
        get() = GBData.PlanetaryOrbit

    internal var nextGlobalID = 1000  // FIXME PERSISTENCE persist

    fun getNextGlobalId(): Int {
        return nextGlobalID++
    }

    //internal var numberOfRaces = GBController.numberOfRaces

    // Stars, Planets, Races are immutable lists (once built) of immutable elements. Things that do change are e.g. locations of things
    // exposing these (for now)
    // TODO: With Locks, we should not need synchronized collections anymore,
    // e.g. Collections.synchronizedMap(hashMapOf<Int, GBStar>())
    internal var allStars: MutableMap<Int, GBStar> =  hashMapOf<Int, GBStar>() // all the stars
    internal var allPlanets: MutableMap<Int, GBPlanet> = hashMapOf<Int, GBPlanet>() // all the planets
    internal var allRaces: MutableMap<Int, GBRace> = hashMapOf<Int, GBRace>() // all the races

    // List of ships. Lists are mutable and change during updates (dead ships...)
    // Not exposed to the app
    internal var allShips: MutableMap<Int, GBShip> = hashMapOf<Int, GBShip>() // all ships, alive or dead

    // Deep Space Ships UID
    internal var deepSpaceUidShips: MutableSet<Int> = HashSet<Int>() // UID of ships.

    // some dead ships in the Universe. Keep here so they don't get garbage collected too early. Keep one turn
    internal var deadShips: MutableMap<Int, GBShip> = hashMapOf()

    // Helper functions for map access. Also include null check.
    fun star(uid: Int): GBStar {
        return allStars[uid]!!
    }

    fun planet(uid: Int): GBPlanet {
        return allPlanets[uid]!!
    }

    fun race(uid: Int): GBRace {
        return allRaces[uid]!!
    }

    fun ship(uid: Int): GBShip {
        return allShips[uid]!!
    }

    // Results of turns. Basically replaced every turn
    val allShots: MutableList<GBVector> = arrayListOf<GBVector>()
    val news: MutableList<String> = arrayListOf<String>()
    val orders: MutableList<GBOrder> = arrayListOf<GBOrder>()

    var autoDo = false // FIXME Almost certain this shouldn't be in universe

    var turn = 0

    // FIXME PERSISTENCE persist turn, which races are playing


    fun getAllShotsList(): List<GBVector> {
        return allShots.toList()
    }

    internal fun consoleDraw() {
        println("=============================================")
        println("The Universe")
        println("=============================================")
        println("The Universe contains $numberOfStars star(s).\n")

        for ((_, star) in allStars) {
            star.consoleDraw()
        }
        println("The Universe contains $numberOfRaces race(s).\n")

        for ((_, race) in allRaces) {
            race.consoleDraw()
        }

        println("News:")
        for (s in news) {
            println(s)
        }

    }

    fun makeStarsAndPlanets() {
        GBLog.i("Making stars and planets")
        areas.clear()
        var uidPlanet = 0
        for (uidStar in 0 until numberOfStars) {
            val numberOfPlanets =
                GBData.rand.nextInt(GBData.MaxNumberOfPlanets - GBData.MinNumberOfPlanets) + GBData.MinNumberOfPlanets
            val coordinates = getStarCoordinates()
            allStars[uidStar] =
                GBStar(getNextGlobalId(), uidStar, numberOfPlanets, coordinates.first, coordinates.second)

            val orbitDist: Float = GBData.MaxSystemOrbit.toFloat() / numberOfPlanets.toFloat()

            for (sidPlanet in 0 until numberOfPlanets) {
                val loc =
                    GBLocation(allStars[uidStar]!!, (sidPlanet + 1f) * orbitDist, rand.nextFloat() * 2f * PI.toFloat())

                allPlanets[uidPlanet] = GBPlanet(getNextGlobalId(), uidPlanet, sidPlanet, uidStar, loc)
                star(uidStar).starUidPlanetList.add(uidPlanet)
                uidPlanet++;
            }
        }
    }

    // Get random, but universally distributed coordinates for allStars
    // Approach: break up the universe into n areas of equal size, and put one star in each area
    // where n is the smallest square number bigger than numberOfStars. Then shuffle the areas as some will remain
    // empty.

    @Transient
    var areas: ArrayList<Int> = ArrayList() // we fill it up on first call to GetStarCoordinates

    // Knowing areas (and keeping them) at a higher level, if they are made hierarchical
    // (Say 4 areas each with 5 sub-areas, then place allStars into sub area) then place one race in each area.
    // Of course for 17 allRaces, this would lead to three levels with 64 sub-sub-areas. Quadratic may be better.

    fun getStarCoordinates(): Pair<Int, Int> {

        val nos = numberOfStars.toDouble() // TODO we don't have u yet....
        val dim = java.lang.Math.ceil(java.lang.Math.sqrt(nos)).toInt()

        if (areas.isEmpty()) {

            // TODO Does this work? areas = IntArray(dim * dim) { i -> i-1}

            for (i in 0 until dim * dim) {
                areas.add(i, i)
            }
            areas.shuffle()
        }

        val coordinates = IntArray(size = 2)
        val area = areas.removeAt(0)

        val areaX = area % dim   // coordinates of the chosen area
        val areaY = area / dim   // coordinates of the chosen area
        val areaWidth = GBData.UniverseMaxX / dim
        val areaHeight = GBData.UniverseMaxY / dim
        val marginX = areaWidth / 10 // no star in the edge of the area
        val marginY = areaHeight / 10 // no star in the edge of the area

        GBLog.d("Adding Star to area " + area + "[" + areaX + "][" + areaY + "] (" + areaWidth + "x" + areaHeight + "){" + marginX + ", " + marginY + "}")

        coordinates[0] = GBData.rand.nextInt(areaWidth - 2 * marginX) + areaX * areaWidth + marginX
        coordinates[1] = GBData.rand.nextInt(areaHeight - 2 * marginY) + areaY * areaHeight + marginY

        return Pair(coordinates[0], coordinates[1])
    }


    fun makeRaces() {
        GBLog.i("Making and landing Races")

        // TODO: Replace with full configuration driven solution instead of hard code.
        // We only need one race for the early mission, but we land the others for God Mode...

        // PERF ?? Each starPlanetsList call below re-creates the List. But this all happens only once.
        // And the code below will need to change anyway.

        // The single player
        val r0 = GBRace(getNextGlobalId(), 0, 0, allStars[0]!!.starPlanetsList[0].uid)
        allRaces[0] = r0
        landPopulation(allStars[0]!!.starPlanetsList[0], r0.uid, 100)


        // We only need one race for the early mission, but we create and land the others for God Mode...
        // Eventually, they will be dynamically landed (from tests, or from app)
        val r1 = GBRace(getNextGlobalId(), 1, 1, allStars[1]!!.starPlanetsList[0].uid)
        allRaces[1] = r1
        val r2 = GBRace(getNextGlobalId(), 2, 2, allStars[2]!!.starPlanetsList[0].uid)
        allRaces[2] = r2
        val r3 = GBRace(getNextGlobalId(), 3, 3, allStars[3]!!.starPlanetsList[0].uid)
        allRaces[3] = r3

        landPopulation(allStars[1]!!.starPlanetsList[0], r1.uid, 100)
        landPopulation(allStars[2]!!.starPlanetsList[0], r2.uid, 100)
        landPopulation(allStars[3]!!.starPlanetsList[0], r3.uid, 100)
        landPopulation(allStars[4]!!.starPlanetsList[0], r1.uid, 50)
        landPopulation(allStars[4]!!.starPlanetsList[0], r2.uid, 50)
        landPopulation(allStars[4]!!.starPlanetsList[0], r3.uid, 50)
    }

    internal fun doUniverse() {
        GBLog.d("Doing Universe: " + orders.toString())

        news.clear()

        // FEATURE only do this if other races are playing
        AutoPlayer.playBeetle()
        AutoPlayer.playImpi()
        AutoPlayer.playTortoise()

        for (o in orders) {
            o.execute()
        }
        orders.clear()

        for ((_, star) in allStars) {
            for (p in star.starPlanetsList) {
                p.doPlanet()
            }
        }

        for ((_, sh) in allShips.filter { (_, ship) -> ship.health <= 0 }) {
            sh.killShip()
        }

        for ((_, sh) in allShips.filter { (_, ship) -> ship.health > 0 }) {
            sh.doShip()
        }

        fireShots()

        // last thing we do...
        turn++

        SaveReload()
        // PERF without reload single digit ms update time, with reload low 100's ms update time.


    }

    fun SaveReload() {
        //FIXME PERSISTENCE Great Persistence Experiment
        // Not ready just yet...
        val moshi = Moshi.Builder().build()
        val gameInfo1 = GBSavedGame("The Real Thing", u)
        val jsonAdapter: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        val json = jsonAdapter.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter.lenient().fromJson(json)!!

        assert(gameInfo1 == gameInfo2)

        u.allStars = gameInfo2.starList!!
        u.allPlanets = gameInfo2.planetList!!
        u.allRaces = gameInfo2.raceList!!

        u.allShips = gameInfo2.shipList!!
        u.deepSpaceUidShips.clear()
        u.allShips.filterValues { it.loc.level == DEEPSPACE }.keys.forEach{u.deepSpaceUidShips.add(it)}
        u.deadShips.clear()
    }

    fun fireShots() {
        allShots.clear()

        // PERF Create one list of all insystem ships, then find shots
        // System Ships shoot at System only
        for ((_, star) in allStars) {
            for (sh1 in star.starShipList.shuffled()) {
                if (sh1.idxtype == CRUISER && (sh1.health > 0)) {
                    for (sh2 in star.starShipList) {
                        if ((sh2.health > 0) && (sh1.uidRace != sh2.uidRace)) {
                            if (sh1.loc.getLoc().distance(sh2.loc.getLoc()) < 5) {
                                fireOneShot(sh1, sh2)
                            }
                        }
                    }

                }
            }
        }
        // Orbit Ships shoot at System, Orbit, or landed ships
        for ((_, p) in allPlanets) {
            for (sh1 in p.orbitShips.shuffled()) {
                if ((sh1.idxtype == CRUISER && (sh1.health > 0))) {
                    for (sh2 in u.star(p.uidStar).starShipList.union(p.orbitShips).union(p.landedShips)) {
                        if ((sh2.health > 0) && (sh1.uidRace != sh2.uidRace)) {
                            if (sh1.loc.getLoc().distance(sh2.loc.getLoc()) < 5) {
                                fireOneShot(sh1, sh2)
                            }
                        }
                    }

                }
            }
        }
    }

    fun fireOneShot(sh1: GBShip, sh2: GBShip) {
        allShots.add(GBVector(sh1.loc.getLoc(), sh2.loc.getLoc()))
        GBLog.d("Firing shot from ${sh1.name} to ${sh2.name} in ${sh1.loc.getLocDesc()}")
        sh2.health -= 40 // Cruiser Shot makes 40 damage

    }


    fun makeFactory(p: GBPlanet, race: GBRace) {
        GBLog.d("universe: Making factory for ${race.name} on ${p.name}.")

        val loc =
            GBLocation(p, rand.nextInt(p.width), rand.nextInt(p.height)) // TODO Have caller give us a better location?

        val order = GBOrder()

        order.makeFactory(loc, race)

        GBLog.d("Order made: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }

    fun makePod(factory: GBShip) {
        GBLog.d("universe: Making Pod for ?? in Factory " + factory.name + "")

        val order = GBOrder()
        order.makePod(factory)

        GBLog.d("Pod ordered: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }

    fun makeCruiser(factory: GBShip) {
        GBLog.d("universe: Making Cruiser for ?? in Factory " + factory.name + "")

        val order = GBOrder()
        order.makeCruiser(factory)

        GBLog.d("Cruiser ordered: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
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
            PlanetaryOrbit,
            rand.nextFloat() * 2 * PI.toFloat()
        ) // TODO Have caller give us a better location?
        sh.dest = loc

    }

    fun landPopulation(p: GBPlanet, uidRace: Int, number: Int) {
        GBLog.d("universe: Landing 100 of " + race(uidRace).name + " on " + p.name + "")
        p.landPopulationOnEmptySector(race(uidRace), number)
    }


}
