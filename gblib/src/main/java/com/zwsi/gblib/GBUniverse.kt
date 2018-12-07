package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.rand
import com.zwsi.gblib.GBLocation.Companion.LANDED
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int

    // TODO Concurrency - for now these are all synchronized. Expensive, but may be safer until we figure out threads

    // Stars, Planets, Races are immutable lists (once built) of immutable elements. Things that do change are e.g. locations of things
    // exposing these (for now)
    val allStars: MutableList<GBStar> = Collections.synchronizedList(arrayListOf<GBStar>()) // all the stars
    val allPlanets : MutableList<GBPlanet> = Collections.synchronizedList(arrayListOf<GBPlanet>()) // all the planets
    val allRaces: MutableList<GBRace> = Collections.synchronizedList(arrayListOf<GBRace>()) // all the races

    // List of ships. Lists are mutable and change during updates (dead ships...)
    // Not exposed to the app
    internal val allShips: MutableList<GBShip> = Collections.synchronizedList(arrayListOf<GBShip>()) // all ships, alive or dead
    internal val deepSpaceShips: MutableList<GBShip> = Collections.synchronizedList(arrayListOf()) // ships in transit between system
    internal val deadShips: MutableList<GBShip> = Collections.synchronizedList(arrayListOf()) // all dead ships in the Universe

    internal var lastShipUpdate = -1
    internal var allShipsList = allShips.toList()
    internal var deepSpaceShipsList = deepSpaceShips.toList()
    internal var deadShipsList = deadShips.toList()

    // Results of turns. Basically replaced every turn
    val allShots: MutableList<GBVector> = Collections.synchronizedList(arrayListOf<GBVector>())
    val news : MutableList<String> = Collections.synchronizedList(arrayListOf<String>())
    val orders : MutableList<GBOrder> = Collections.synchronizedList(arrayListOf<GBOrder>())

    class GBInstruction(var t: Int, var code: () -> Unit?) {}
    var scheduledActions : MutableList<GBInstruction>  = Collections.synchronizedList(arrayListOf<GBInstruction>())

    var autoDo = false
    var turn = 0

    constructor(numberOfStars: Int) {
        this.numberOfStars = numberOfStars
        this.numberOfRaces = GBController.numberOfRaces
        GBLog.d("Making Stars")
        makeStars()
        makeRaces()
        GBLog.d("Universe made")
    }

    val universeMaxX: Int
        get() = GBData.UniverseMaxX

    val universeMaxY: Int
        get() = GBData.UniverseMaxY

    val systemBoundary: Int
        get() = GBData.SystemBoundary

    fun getNumberOfStars(): Int {
        return numberOfStars
    }

    fun getAllShipsList(): List<GBShip> {
        if (turn > lastShipUpdate) {
            allShipsList = allShips.toList()
        }
        return allShipsList
    }

    fun getUniverseShipsList(): List<GBShip> {
        if (turn > lastShipUpdate) {
            deadShipsList= deadShips.toList().filter { it.health > 0 }
        }
        return deepSpaceShips
    }

    fun getAllShotsList(): List<GBVector> {
        return allShots.toList()
    }

    internal fun consoleDraw() {
        println("=============================================")
        println("The Universe")
        println("=============================================")
        println("The Universe contains $numberOfStars star(s).\n")

        for (i in allStars) {
            i.consoleDraw()
        }
        println("The Universe contains $numberOfRaces race(s).\n")

        for (i in allRaces) {
            i.consoleDraw()
        }

        println("News:")
        for (s in news) {
            println(s)
        }

    }

    private fun makeStars() {
        GBLog.i("Making stars and planets")
        GBStar.resetStarCoordinates()
        for (i in 0 until numberOfStars) {
            GBStar(this)
        }

    }

    private fun makeRaces() {
        GBLog.i("Making and landing Races")

        // TODO: Replace with full configuration driven solution instead of hard code.
        // We only need one race for the early mission, but we land the others for God Mode...

        // The single player
        val r0 = GBRace(this, 0, allStars[0].starPlanets[0])
        landPopulation(allStars[0].starPlanets[0], r0.uid, 100)


        // We only need one race for the early mission, but we land the others for God Mode...
        // Eventually, they will be dynamically landed (from tests, or from app)
        val r1 = GBRace(this, 1, allStars[1].starPlanets[0])
        val r2 = GBRace(this, 2, allStars[2].starPlanets[0])
        val r3 = GBRace(this, 3, allStars[3].starPlanets[0])


        landPopulation(allStars[1].starPlanets[0], r1.uid, 100)
        landPopulation(allStars[2].starPlanets[0], r2.uid, 100)
        landPopulation(allStars[3].starPlanets[0], r3.uid, 100)
        landPopulation(allStars[4].starPlanets[0], r1.uid, 50)
        landPopulation(allStars[4].starPlanets[0], r2.uid, 50)
        landPopulation(allStars[4].starPlanets[0], r3.uid, 50)
    }


    internal fun doUniverse() {
        GBLog.d("Doing Universe: " + orders.toString())

        news.clear()

        scheduledActions.forEach() {
            GBLog.d("Looking at action for turn ${it.t}")
            if ((it.t == turn) || (it.t == -1)) {
                run { it.code() }
            }
        }
        // TODO PERFORMANCE / MEMORY LEAK remove actions from before this turn

        for (o in orders) {
            o.execute()
        }
        orders.clear()

        for (s in allStars) {
            for (p in s.starPlanets) {
                p.doPlanet()
            }
        }


        for (sh in getAllShipsList()) {
            sh.doShip()
        }

        fireShots()

        // last thing we do...
        turn++

    }

    fun fireShots() { // TODO use filtered lists
        allShots.clear()
        for (s in allStars) {
            for (sh1 in s.starShips.shuffled()) {
                if (sh1.idxtype == CRUISER) {
                    for (sh2 in s.starShips) {
                        if ((sh2.health > 0) && (sh2.idxtype == POD)) {
                            allShots.add(GBVector(sh1.loc.getLoc(), sh2.loc.getLoc()))
                            GBLog.d("Firing shot from ${sh1.name} to ${sh2.name} in ${sh1.loc.getLocDesc()}")
                            sh2.health = 0
                        }
                    }

                }
            }
        }
        for (p in allPlanets) {
            for (sh1 in p.orbitShips.shuffled()) {
                if (sh1.idxtype == CRUISER) {
                    for (sh2 in p.star.starShips) {
                        if ((sh2.health > 0) && (sh2.idxtype == POD)) {
                            allShots.add(GBVector(sh1.loc.getLoc(), sh2.loc.getLoc()))
                            GBLog.d("Firing shot from ${sh1.name} to ${sh2.name} in ${sh1.loc.getLocDesc()}")
                            sh2.health = 0
                        }
                    }

                }
            }
        }

    }

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s.starPlanets.toTypedArray()
    } // TODO should this be Star? But what about getting all the allPlanets?

//    fun getSectors(p: GBPlanet): Array<GBSector> {
//        return p.sectors
//    } //TODO should this be in planet? Or Data?


    fun makeFactory(p: GBPlanet, race: GBRace) {
        GBLog.d("universe: Making factory for ${race.name} on ${p.name}.")

        var loc = GBLocation(p, 0, 0) // TODO Have caller give us a better location

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

        var loc = GBLocation(p, 1f, rand.nextFloat()*2*PI.toFloat()) // TODO Have caller give us a better location
        sh.dest = loc

    }

    fun landPopulation(p: GBPlanet, uId: Int, number: Int) {
        GBLog.d("universe: Landing 100 of " + allRaces[uId].name + " on " + p.name + "")
        p.landPopulation(allRaces[uId], number)
    }


    fun makeStuff() {

        GBLog.d("Making Stuff in turn $turn")

        var now = turn + 1 // just in case we have a turn running....

        var code = {}

        for (r in allRaces) {
            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                universe.makeFactory(p, r)
            }
            scheduledActions.add(GBInstruction(now, code))
        }

        for (i in 0..999) {
            code = {
                val factory = allRaces[2].raceShips.find { it.idxtype == FACTORY }
                GBLog.d("Ordered Pod")
                factory?.let { universe.makePod(it) }
            }
            scheduledActions.add(GBInstruction(now + 1 + i*5, code))

        }
        code = {
            GBLog.d("Directed Pod")
            // Getting all pods, not just alive pods, so even "dead" pods will start moving again. Ok for God to do.
            val pod = allRaces[2].raceShips.find { (it.idxtype == POD) && (it.dest == null) }
            pod?.let { universe.flyShipLanded(it, universe.allPlanets[rand.nextInt(10)]) }
        }
        scheduledActions.add(GBInstruction(-1, code))

        for (j in arrayOf(0, 1, 3)) {
            for (i in 0..5) {
                code = {
                    val factory = allRaces[j].raceShips.find { it.idxtype == FACTORY }
                    GBLog.d("Ordered Cruiser")
                    factory?.let { universe.makeCruiser(it) }
                }
                scheduledActions.add(GBInstruction(now + 1 + i * 10, code))
            }
        }

        code = {
            GBLog.d("Directed Cruiser")
            // Getting all ships, not just alive ships, so even "dead" pods will start moving again. Ok for God to do.
            val cruiser = universe.getAllShipsList().find { (it.idxtype == CRUISER) && (it.dest == null) && (it.loc.level == LANDED) }
            cruiser?.let { universe.flyShipOrbit(it, universe.allPlanets[rand.nextInt(5)]) }
        }
        scheduledActions.add(GBInstruction(-1, code))
    }
}
