package com.zwsi.gblib

class GBUniverse {

    internal var numberOfStars: Int
    internal var numberOfRaces: Int
    var allStars: MutableList<GBStar> = arrayListOf()  // the allStars
    var allPlanets: MutableList<GBPlanet> = arrayListOf() // the allPlanets
    var allRaces: MutableList<GBRace> = arrayListOf() // the allRaces

    // how many star Systems in the Universe
    internal constructor(numberOfStars: Int, numberOfRaces: Int) {
        this.numberOfStars = numberOfStars
        this.numberOfRaces = numberOfRaces
        GBDebug.l3("Making Stars")
        makeStars()
        makeRaces()
        GBDebug.l3("Universe made")
    }

    val universeMaxX: Int
        get() = GBData.getUniverseMaxX()
    val universeMaxY: Int
        get() = GBData.getUniverseMaxY()

    fun getNumberOfStars() : Int { return numberOfStars}

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
            GBStar(i, this)
        }

    }

    private fun makeRaces() {
        GBDebug.l3("Making Races")

        // TODO: Replace with full GBData driven solution instead of hard coded

        var sector: GBSector
        allRaces.add(GBRace(0, 0))
        landPopulation(allStars[0].starPlanets[0]!!, allRaces[0].uId, 100)

        if (numberOfStars > 1) {
            allRaces.add(GBRace(1, 1))
            landPopulation(allStars[1].starPlanets[0]!!, allRaces[1].uId, 100)

        }

        if (numberOfStars > 2) {
            landPopulation(allStars[2].starPlanets[0]!!, allRaces[0].uId, 50)
            landPopulation(allStars[2].starPlanets[0]!!, allRaces[1].uId, 50)
        }

    }

    fun landPopulation(p: GBPlanet, uId: Int, number: Int) {
        GBDebug.l3("universe: Landing 100 of " + allRaces[uId].name + " on " + p.name + "")
        p.landPopulation(allRaces[uId], number)
    }


    internal fun doUniverse() {
        for (s in allStars) {
            for (p in s.starPlanets) {
                p!!.doPlanet()
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
