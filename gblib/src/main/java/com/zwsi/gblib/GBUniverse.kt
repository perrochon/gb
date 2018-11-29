package com.zwsi.gblib

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int
    var allStars: MutableList<GBStar> = arrayListOf()  // the all the stars
    var allPlanets: MutableList<GBPlanet> = arrayListOf() // all the planets
    var allRaces: MutableList<GBRace> = arrayListOf() // all the races

    var allShips: MutableList<GBShip> = arrayListOf() // all the ships in the Universe
    var universeShips: MutableList<GBShip> = arrayListOf() // ships in transit between system

    var missionController = GBMissionController()

    var news = arrayListOf<String>()

    var orders = arrayListOf<GBOrder>()


    constructor(numberOfStars: Int) {
        this.numberOfStars = numberOfStars
        this.numberOfRaces = GBController.numberOfRaces
        GBDebug.l3("Making Stars")
        makeStars()
//        for (s in allStars) {
//            s.makePlanets()
//        }
        makeRaces()
        news.add(missionController.getCurrentMission())
        GBDebug.l3("Universe made")
    }

    val universeMaxX: Int
        get() = GBData.getUniverseMaxX()
    val universeMaxY: Int
        get() = GBData.getUniverseMaxY()

    fun getNumberOfStars(): Int {
        return numberOfStars
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
        GBDebug.l2("Making stars and planets")
        GBStar.resetStarCoordinates()
        for (i in 0 until numberOfStars) {
            GBStar(this)
        }

    }

    private fun makeRaces() {
        GBDebug.l2("Making and landing Races")

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
        GBDebug.l3("Doing Universe: " + orders.toString())

        news.clear()

        for (o in orders) {
            o.execute()
        }
        orders.clear()

        for (s in allStars) {
            for (p in s.starPlanets) {
            }
        }

        for (sh in allShips) {
            sh.doShip()
        }

        missionController.checkMissionStatus()
        news.add(missionController.getCurrentMission())
    }

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s.starPlanets.toTypedArray()
    } // TODO should this be Star? But what about getting all the allPlanets?

    fun getSectors(p: GBPlanet): Array<GBSector> {
        return p.sectors
    } //TODO should this be in planet? Or Data?


    fun makeFactory(p: GBPlanet) {
        GBDebug.l3("universe: Making factory for ?? on " + p.name + "")

        var loc = GBLocation(p, 0,0 ) // TODO Have caller give us a better location

        var order = GBOrder()

        order.makeFactory(loc)

        GBDebug.l3("Order made: " + order.toString())

        orders.add(order)

        GBDebug.l3("Current Orders: " + orders.toString())
    }

    fun makePod(factory: GBShip) {
        GBDebug.l3("universe: Making Pod for ?? in Factory " + factory.name + "")

        var order = GBOrder()
        order.makePod(factory)

        GBDebug.l3("Ship made: " + order.toString())

        orders.add(order)

        GBDebug.l3("Current Orders: " + orders.toString())
    }


    fun flyShip(sh: GBShip, p: GBPlanet) {
        GBDebug.l3("Creating order to teleport " + sh.name + " to " + p.name)

        var loc = GBLocation(p, 0,0 ) // TODO Have caller give us a better location

        sh.dest = loc

//        var order = GBOrder()
//        order.teleportShip(sh, loc)
//        GBDebug.l3("Order made: " + order.toString())
//        orders.add(order)

        GBDebug.l3("Current Orders: " + orders.toString())
    }

    fun landPopulation(p: GBPlanet, uId: Int, number: Int) {
        GBDebug.l3("universe: Landing 100 of " + allRaces[uId].name + " on " + p.name + "")
        p.landPopulation(allRaces[uId], number)
    }
}
