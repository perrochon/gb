package com.zwsi.gblib

import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.PlanetaryOrbit
import com.zwsi.gblib.GBData.Companion.rand
import java.util.*
import kotlin.math.PI

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int

    // TODO Concurrency - for now these are all synchronized. Expensive, but may be safer until we figure out threads

    // Stars, Planets, Races are immutable lists (once built) of immutable elements. Things that do change are e.g. locations of things
    // exposing these (for now)
    val allStars: MutableMap<Int, GBStar> = Collections.synchronizedMap(hashMapOf<Int, GBStar>()) // all the stars
    val allPlanets: MutableMap<Int, GBPlanet> = Collections.synchronizedMap(hashMapOf<Int, GBPlanet>()) // all the planets
    val allRaces: MutableMap<Int, GBRace> = Collections.synchronizedMap(hashMapOf<Int, GBRace>()) // all the races

    // List of ships. Lists are mutable and change during updates (dead ships...)
    // Not exposed to the app
    internal val allShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf<GBShip>()) // all ships, alive or dead
    internal val deepSpaceShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf()) // ships in transit between system
    internal val deadShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf()) // all dead ships in the Universe

    internal var lastAllShipsUpdate = -1
    internal var lastDeepSpaceShipsUpdate = -1
    internal var lastDeadShipsUpdate = -1
    internal var allShipsList = allShips.toList()
    internal var deepSpaceShipsList = deepSpaceShips.toList()
    internal var deadShipsList = deadShips.toList()

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
        return allShips[uid]
    }


    // Results of turns. Basically replaced every turn
    val allShots: MutableList<GBVector> = Collections.synchronizedList(arrayListOf<GBVector>())
    val news: MutableList<String> = Collections.synchronizedList(arrayListOf<String>())
    val orders: MutableList<GBOrder> = Collections.synchronizedList(arrayListOf<GBOrder>())

    var autoDo = false // TODO QUALITY Almost certain this shouldn't be in universe
    var turn = 0

    constructor(numberOfStars: Int) {
        this.numberOfStars = numberOfStars
        this.numberOfRaces = GBController.numberOfRaces
        GBLog.d("Making Stars")
        makeStarsAndPlanets()
        makeRaces()
        GBLog.d("Universe made")
    }

    val universeMaxX: Int
        get() = GBData.UniverseMaxX

    val universeMaxY: Int
        get() = GBData.UniverseMaxY

    val systemBoundary: Float
        get() = GBData.SystemBoundary

    val planetaryOrbit: Float
        get() = GBData.PlanetaryOrbit

    fun getNumberOfStars(): Int {
        return numberOfStars
    }

    fun getAllStarsMap(): Map<Int, GBStar> {
        return allStars.toMap()
    }

    fun getAllPlanetsMap(): Map<Int, GBPlanet> {
        return allPlanets.toMap()
    }

    fun getAllRacesMap(): Map<Int, GBRace> {
        return allRaces.toMap()
    }

    fun getAllShipsList(): List<GBShip> {
        if (turn > lastAllShipsUpdate) {
            allShipsList = allShips.toList()
            lastAllShipsUpdate = turn
        }
        return allShipsList
    }

    fun getDeepSpaceShipsList(): List<GBShip> {
        if (turn > lastDeepSpaceShipsUpdate) {
            deepSpaceShipsList = deepSpaceShips.toList().filter { it.health > 0 }
            lastDeepSpaceShipsUpdate = turn
        }
        return deepSpaceShipsList
    }

    fun getDeadShipsList(): List<GBShip> {
        if (turn > lastDeadShipsUpdate) {
            deadShipsList = deadShips.toList()
            lastDeadShipsUpdate = turn
        }
        return deadShipsList
    }

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

    private fun makeStarsAndPlanets() {
        GBLog.i("Making stars and planets")
        areas.clear()
        var uidPlanet = 0
        for (uidStar in 0 until numberOfStars) {
            val numberOfPlanets = 8
            val coordinates = getStarCoordinates()
            allStars[uidStar] = GBStar(uidStar, numberOfPlanets, coordinates.first, coordinates.second)

            for (sidPlanet in 0 until numberOfPlanets) {
                allPlanets[uidPlanet] = GBPlanet(uidPlanet, sidPlanet, allStars[uidStar]!!)
                star(uidStar).starPlanets.add(planet(uidPlanet))
                uidPlanet ++;
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


    private fun makeRaces() {
        GBLog.i("Making and landing Races")

        // TODO: Replace with full configuration driven solution instead of hard code.
        // We only need one race for the early mission, but we land the others for God Mode...

        // The single player
        val r0 = GBRace(0, 0, allStars[0]!!.starPlanets[0].uid)
        allRaces[0] = r0
        landPopulation(allStars[0]!!.starPlanets[0], r0.uid, 100)


        // We only need one race for the early mission, but we create and land the others for God Mode...
        // Eventually, they will be dynamically landed (from tests, or from app)
        val r1 = GBRace(1, 1, allStars[1]!!.starPlanets[0].uid)
        allRaces[1] = r1
        val r2 = GBRace(2, 2, allStars[2]!!.starPlanets[0].uid)
        allRaces[2] = r2
        val r3 = GBRace(3, 3, allStars[3]!!.starPlanets[0].uid)
        allRaces[3] = r3

        landPopulation(allStars[1]!!.starPlanets[0], r1.uid, 100)
        landPopulation(allStars[2]!!.starPlanets[0], r2.uid, 100)
        landPopulation(allStars[3]!!.starPlanets[0], r3.uid, 100)
        landPopulation(allStars[4]!!.starPlanets[0], r1.uid, 50)
        landPopulation(allStars[4]!!.starPlanets[0], r2.uid, 50)
        landPopulation(allStars[4]!!.starPlanets[0], r3.uid, 50)
    }


    internal fun doUniverse() {
        GBLog.d("Doing Universe: " + orders.toString())

        news.clear()

        GBScheduler.doSchedule()

        // TODO PERFORMANCE / MEMORY LEAK remove actions from before this turn

        for (o in orders) {
            o.execute()
        }
        orders.clear()

        for ((_, star) in allStars) {
            for (p in star.starPlanets) {
                p.doPlanet()
            }
        }

        // Move dead ships to deadships
        // TODO: After we correctly implemented ViewModels and LiveData, we should not have to keep dead ships around
        for ((_, star) in allStars) {
            for (sh in star.starShips.filter { it.health == 0 }) {
                sh.killShip()
            }
            for (p in star.starPlanets) {
                for (sh in p.landedShips.filter { it.health == 0 }) {
                    sh.killShip()
                }
                for (sh in p.orbitShips.filter { it.health == 0 }) {
                    sh.killShip()
                }
            }
        }
        for (sh in deepSpaceShips.filter { it.health == 0 }) {
            sh.killShip()
        }

        for (sh in allShips.filter { it.health > 0 }) {
            sh.doShip()
        }

        fireShots()

        // last thing we do...
        turn++

    }

    fun fireShots() { // TODO use filtered lists
        allShots.clear()

        // TODO: Perf and Feature: Create one list of all insystem ships, then find shots
        // System Ships shoot at System only
        for ((_, star) in allStars) {
            for (sh1 in star.starShipsList.shuffled()) {
                if (sh1.idxtype == CRUISER && (sh1.health > 0)) {
                    for (sh2 in star.starShipsList) {
                        if ((sh2.health > 0) && (sh1.race != sh2.race)) {
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
                    for (sh2 in p.star.starShips.union(p.orbitShips).union(p.landedShips)) {
                        if ((sh2.health > 0) && (sh1.race != sh2.race)) {
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

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s.starPlanets.toTypedArray()
    } // TODO Deprecate this. Get it from stars.

//    fun getSectors(p: GBPlanet): Array<GBSector> {
//        return p.sectors
//    } //TODO should this be in planet? Or Data?


    fun makeFactory(p: GBPlanet, race: GBRace) {
        GBLog.d("universe: Making factory for ${race.name} on ${p.name}.")

        var loc =
            GBLocation(p, rand.nextInt(p.width), rand.nextInt(p.height)) // TODO Have caller give us a better location

        var order = GBOrder()

        order.makeFactory(loc, race)

        GBLog.d("Order made: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }

    fun makePod(factory: GBShip) {
        GBLog.d("universe: Making Pod for ?? in Factory " + factory.name + "")

        var order = GBOrder()
        order.makePod(factory)

        GBLog.d("Pod ordered: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }

    fun makeCruiser(factory: GBShip) {
        GBLog.d("universe: Making Cruiser for ?? in Factory " + factory.name + "")

        var order = GBOrder()
        order.makeCruiser(factory)

        GBLog.d("Cruiser ordered: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }


    fun flyShipLanded(sh: GBShip, p: GBPlanet) {
        GBLog.d("Setting Destination of " + sh.name + " to " + p.name)

        var loc = GBLocation(p, 0, 0) // TODO Have caller give us a better location
        sh.dest = loc

    }

    fun flyShipOrbit(sh: GBShip, p: GBPlanet) {
        GBLog.d("Setting Destination of " + sh.name + " to " + p.name)

        var loc = GBLocation(
            p,
            PlanetaryOrbit,
            rand.nextFloat() * 2 * PI.toFloat()
        ) // TODO Have caller give us a better location
        sh.dest = loc

    }

    fun landPopulation(p: GBPlanet, uidRace: Int, number: Int) {
        GBLog.d("universe: Landing 100 of " + race(uidRace).name + " on " + p.name + "")
        p.landPopulationOnEmptySector(race(uidRace), number)
    }


}
