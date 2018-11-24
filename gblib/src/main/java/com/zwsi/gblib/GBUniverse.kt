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


    internal constructor(numberOfStars: Int, numberOfRaces: Int) {
        this.numberOfStars = numberOfStars
        this.numberOfRaces = numberOfRaces
        GBDebug.l3("Making Stars")
        makeStars()
        makeRaces()
        //makeShips()
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

        println ("News:")
        for (s in news) {
            println(s)
        }

    }

    private fun makeStars() {
        GBDebug.l3("Making Stars")
        GBStar.resetStarCoordinates()
        for (i in 0 until numberOfStars) {
            GBStar(this)
        }

    }

    private fun makeRaces() {
        GBDebug.l3("Making Races")

        // TODO: Replace with full GBData driven solution instead of hard coded

        val r1 = GBRace(this, 0)
        val r2 = GBRace(this, 1)
        val r3 = GBRace(this, 2)
        val r4 = GBRace(this, 3)

        landPopulation(allStars[0].starPlanets[0], r1.uid, 100)

        if (numberOfStars > 1) {
            landPopulation(allStars[1].starPlanets[0], r2.uid, 100)
        }

        if (numberOfStars > 2) {
            landPopulation(allStars[2].starPlanets[0], r1.uid, 50)
            landPopulation(allStars[2].starPlanets[0], r2.uid, 50)
        }

        if (numberOfStars > 3) {
            landPopulation(allStars[3].starPlanets[0], r3.uid, 100)
        }

    }

    private fun makeShips() {
        GBDebug.l3("Making Ships")

        // TODO: Replace with user driven solution instead of hard coded

        GBShip(0, allRaces[0], 1, allStars[0].starPlanets[0].uid)
        GBShip(0, allRaces[1], 1, allStars[1].starPlanets[0].uid)
        GBShip(0, allRaces[2], 1, allStars[2].starPlanets[0].uid)

        if (numberOfStars > 3) {
            GBShip(0, allRaces[3], 1, allStars[3].starPlanets[0].uid)
        }

        GBShip(1, allRaces[0], 2, allStars[0].starPlanets[0].uid)
        GBShip(1, allRaces[1], 2, allStars[1].starPlanets[0].uid)
        GBShip(1, allRaces[2], 2, allStars[2].starPlanets[0].uid)
        if (numberOfStars > 3) {
            GBShip(1, allRaces[3], 2, allStars[3].starPlanets[0].uid)
        }

        GBShip(2, allRaces[0], 3, allStars[2].uid)
        GBShip(2, allRaces[1], 3, allStars[2].uid)
        GBShip(2, allRaces[2], 3, allStars[2].uid)
        GBShip(2, allRaces[3], 3, allStars[2].uid)

        GBShip(2, allRaces[0], 4, allStars[2].uid)
        GBShip(2, allRaces[1], 4, allStars[2].uid)
        GBShip(2, allRaces[2], 4, allStars[2].uid)
        GBShip(2, allRaces[3], 4, allStars[2].uid)
    }



    internal fun doUniverse() {
        GBDebug.l3("Doing Universe: " + orders.toString())

        news.clear()
        for (s in allStars) {
            for (p in s.starPlanets) {
                p.doPlanet()
            }
        }
        missionController.checkMissionStatus()
        news.add(missionController.getCurrentMission())

        GBDebug.l3("Current Orders: " + orders.toString())

        for (o in orders) {
            o.execute()
        }
    }

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s.starPlanets.toTypedArray()
    } // TODO should this be Star? But what about getting all the allPlanets?

    fun getSectors(p: GBPlanet): Array<GBSector> {
        return p.sectors
    } //TODO should this be in planet? Or Data?


    fun makeFactory(p: GBPlanet) {
        GBDebug.l3("universe: Making factory for ?? on " + p.name + "")

        var order = GBOrder(this)
        order.makeFactory(p)

        GBDebug.l3("Order made: " + order.toString())

        orders.add(order)

        GBDebug.l3("Current Orders: " + orders.toString())
    }

    fun makePod(s: GBShip) {
        GBDebug.l3("universe: Making Pod for ?? in Factory " + s.name + "")

        var order = GBOrder(this)
        order.makePod(s)

        GBDebug.l3("Ship made: " + order.toString())

        orders.add(order)

        GBDebug.l3("Current Orders: " + orders.toString())
    }

    fun landPopulation(p: GBPlanet, uId: Int, number: Int) {
        GBDebug.l3("universe: Landing 100 of " + allRaces[uId].name + " on " + p.name + "")
        p.landPopulation(allRaces[uId], number)
    }



}
