package com.zwsi.gblib

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int
    var allStars: MutableList<GBStar> = arrayListOf()  // the all the stars
    var allPlanets: MutableList<GBPlanet> = arrayListOf() // all the planets
    var allRaces: MutableList<GBRace> = arrayListOf() // all the races
    var allShips: MutableList<GBShip> = arrayListOf() // all the ships


    internal constructor(numberOfStars: Int, numberOfRaces: Int) {
        this.numberOfStars = numberOfStars
        this.numberOfRaces = numberOfRaces
        GBDebug.l3("Making Stars")
        makeStars()
        makeRaces()
        makeShips()
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

        landPopulation(allStars[0].starPlanets[0], r1.uid, 100)

        if (numberOfStars > 1) {
            landPopulation(allStars[1].starPlanets[0], r2.uid, 100)
        }

        if (numberOfStars > 2) {
            landPopulation(allStars[2].starPlanets[0], r1.uid, 50)
            landPopulation(allStars[2].starPlanets[0], r2.uid, 50)
        }

    }

    private fun makeShips() {
        GBDebug.l3("Making Ships")

        // TODO: Replace with user driven solution instead of hard coded

        val sh1 = GBShip(allRaces[0], allStars[0])
        val sh2 = GBShip(allRaces[1], allStars[0])
    }

    fun landPopulation(p: GBPlanet, uId: Int, number: Int) {
        GBDebug.l3("universe: Landing 100 of " + allRaces[uId].name + " on " + p.name + "")
        p.landPopulation(allRaces[uId], number)
    }


    internal fun doUniverse() {
        for (s in allStars) {
            for (p in s.starPlanets) {
                p.doPlanet()
            }
        }

    }

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s.starPlanets.toTypedArray()
    } // TODO should this be Star? But what about getting all the allPlanets?

    fun getSectors(p: GBPlanet): Array<GBSector> {
        return p.sectors
    } //TODO should this be in planet? Or Data?


}
