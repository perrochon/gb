package com.zwsi.gblib

import jdk.nashorn.internal.ir.annotations.Immutable

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int
    var allStars: MutableList<GBStar> = arrayListOf()  // the all the stars
    var allPlanets: MutableList<GBPlanet> = arrayListOf() // all the planets
    var allRaces: MutableList<GBRace> = arrayListOf() // all the races

    var allShips: MutableList<GBShip> = arrayListOf() // all the ships in the Universe

    var universeShips: MutableList<GBShip> = arrayListOf() // ships in transit between system

    var news = arrayListOf<String>()

    var orders = arrayListOf<GBOrder>()

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

    fun getAllShipsList() : List<GBShip> {
        return allShips.filter { true }.toList()
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
        val r0 = GBRace(this, 0)
        landPopulation(allStars[0].starPlanets[0], r0.uid, 100)


        // We only need one race for the early mission, but we land the others for God Mode...
        // Eventually, they will be dynamically landed (from tests, or from app)
        val r1 = GBRace(this, 1)
        val r2 = GBRace(this, 2)
        val r3 = GBRace(this, 3)


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

//        runProgram()

        turn++

    }

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s.starPlanets.toTypedArray()
    } // TODO should this be Star? But what about getting all the allPlanets?

    fun getSectors(p: GBPlanet): Array<GBSector> {
        return p.sectors
    } //TODO should this be in planet? Or Data?


    fun makeFactory(p: GBPlanet) {
        GBLog.d("universe: Making factory for ?? on " + p.name + "")

        var loc = GBLocation(p, 0, 0) // TODO Have caller give us a better location

        var order = GBOrder()

        order.makeFactory(loc)

        GBLog.d("Order made: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }

    fun makePod(factory: GBShip) {
        GBLog.d("universe: Making Pod for ?? in Factory " + factory.name + "")

        var order = GBOrder()
        order.makePod(factory)

        GBLog.d("Ship made: " + order.toString())

        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }


    fun flyShip(sh: GBShip, p: GBPlanet) {
        GBLog.d("Creating order to teleport " + sh.name + " to " + p.name)

        var loc = GBLocation(p, 0, 0) // TODO Have caller give us a better location

        sh.dest = loc

//        var order = GBOrder()
//        order.teleportShip(sh, loc)
//        GBLog.d("Order made: " + order.toString())
//        orders.add(order)

        GBLog.d("Current Orders: " + orders.toString())
    }

    fun landPopulation(p: GBPlanet, uId: Int, number: Int) {
        GBLog.d("universe: Landing 100 of " + allRaces[uId].name + " on " + p.name + "")
        p.landPopulation(allRaces[uId], number)
    }


//    var program = listOf<Int>()
//    fun runProgram() {
//
//        var steps = program.filter { it == turn }
//
//        for (step in steps) {
//            when (turn) {
//                0 -> {
//                }
//                1 -> {
//                }
//                2 -> {
//                }
//                3 -> {
//                }
//            }
//        }
//    }

    fun makeStuff() {
        GBLog.d("Making Stuff")

        turn = 0

        val p = allPlanets[0]
        makeFactory(p)
        doUniverse()
        val factory = allShips[0]
        var pod : GBShip
        if (factory.idxtype == 0) {

            for (pt in p.star.starPlanets) {
                if (p != pt) {
                    makePod(factory)
                    doUniverse()
                    pod = allShips[allShips.size-1]
                    flyShip(pod, pt)
                }
            }

            makePod(factory)
            doUniverse()
            pod = allShips[allShips.size-1]
            flyShip(pod, allStars[1].starPlanets[0])

            makePod(factory)
            doUniverse()
            pod = allShips[allShips.size-1]
            flyShip(pod, allStars[0].starPlanets[1])

            for (i in 1..5)
                doUniverse()
            flyShip(pod, allStars[0].starPlanets[1])
            for (i in 1..5)
                doUniverse()
            flyShip(pod, allStars[4].starPlanets[0])
            for (i in 1..4)
                doUniverse()

        }
    }

}
