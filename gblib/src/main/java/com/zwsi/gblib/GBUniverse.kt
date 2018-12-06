package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.rand
import java.util.*

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int
    var allStars = Collections.synchronizedList(arrayListOf<GBStar>()) // the all the stars
    var allPlanets = Collections.synchronizedList(arrayListOf<GBPlanet>()) // all the planets
    var allRaces = Collections.synchronizedList(arrayListOf<GBRace>()) // all the races

    val allShips = Collections.synchronizedList(arrayListOf<GBShip>())
    val deadShips: MutableList<GBShip> = Collections.synchronizedList(arrayListOf()) // all the ships in the Universe
    val universeShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf()) // ships in transit between system

    val allShots = Collections.synchronizedList(arrayListOf<GBVector>())

    var news = arrayListOf<String>()

    var orders = arrayListOf<GBOrder>()

    class GBInstruction(var t: Int, var code: () -> Unit?) {}

    var scheduledActions = mutableListOf<GBInstruction>()

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
        get() = GBData.getUniverseMaxX()
    val universeMaxY: Int
        get() = GBData.getUniverseMaxY()

    fun getNumberOfStars(): Int {
        return numberOfStars
    }


    fun getAllShipsList(): List<GBShip> {
        return allShips.toList()
    }


    fun getUniverseShipsList(): List<GBShip> {
        return universeShips.toList()
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
            GBLog.d("Looking at action")
            GBLog.d(it.toString())
            if ((it.t == turn) || (it.t == -1)) {
                run { it.code() }
            }
        }

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


        turn++

    }

    fun fireShots() { // TODO use filtered lists
        allShots.clear()
//        for (sh1 in allShips) {
//            if (sh1.idxtype == CRUISER) {
//                for (sh2 in allShips) {
//                    if (sh2.idxtype == POD) {
//                        allShots.add(GBVector(sh1.loc.getLoc(), sh2.loc.getLoc()))
//                        GBLog.d("Firing shot from ${sh1.name} to ${sh2.name} in ${sh1.loc.getLocDesc()}")
//                    }
//                }
//            }
//        }
        for (s in allStars) {
            for (sh1 in s.getStarShipsList()) {
                if (sh1.idxtype == CRUISER) {
                    for (sh2 in s.getStarShipsList()) {
                        if (sh2.idxtype == POD) {
                            allShots.add(GBVector(sh1.loc.getLoc(), sh2.loc.getLoc()))
                            GBLog.d("Firing shot from ${sh1.name} to ${sh2.name} in ${sh1.loc.getLocDesc()}")

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
        GBLog.d("universe: Making factory for ?? on " + p.name + "")

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


    fun flyShip(sh: GBShip, p: GBPlanet) {
        GBLog.d("Setting Destination of " + sh.name + " to " + p.name)

        var loc = GBLocation(p, 0, 0) // TODO Have caller give us a better location
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

        for (i in 0..99) {
            code = {
                val factory = allRaces[2].raceShips.find { it.idxtype == FACTORY }
                GBLog.d("Ordered Pod")
                factory?.let { universe.makePod(it) }
            }
            scheduledActions.add(GBInstruction(now + 1 + i * 5, code))

        }
        code = {
            GBLog.d("Directed Pod")
            // Getting all pods, not just alive pods, so even "dead" pods will start moving again. Ok for God to do.
            val pod = allRaces[2].raceShips.find { (it.idxtype == POD) && (it.dest == null) }
            pod?.let { universe.flyShip(it, universe.allPlanets[rand.nextInt(10)]) }
        }
        scheduledActions.add(GBInstruction(-1, code))

        for (j in arrayOf(0, 1, 3)) {
            for (i in 0..20) {
                code = {
                    val factory = allRaces[j].raceShips.find { it.idxtype == FACTORY }
                    GBLog.d("Ordered Cruiser")
                    factory?.let { universe.makeCruiser(it) }
                }
                scheduledActions.add(GBInstruction(now + 1 + i * 100, code))
            }
        }

        code = {
            GBLog.d("Directed Cruiser")
            // Getting all ships, not just alive ships, so even "dead" pods will start moving again. Ok for God to do.
            val cruiser = universe.getAllShipsList().find { (it.idxtype == CRUISER) && (it.dest == null) }
            cruiser?.let { universe.flyShip(it, universe.allPlanets[rand.nextInt(5)]) }
        }
        scheduledActions.add(GBInstruction(-1, code))
    }
}
