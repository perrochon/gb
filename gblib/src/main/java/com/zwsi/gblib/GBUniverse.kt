package com.zwsi.gblib

class GBUniverse internal constructor(
    internal var numberOfRaces: Int // how many star Systems in the Universe
) {

    // All these variables are package private, because (for now?) we trust the package
    var stars: Array<GBStar?>
        internal set // the star Systems //
    // TODO need to figure out where these live
    internal var numberOfStars: Int = 0 // how many star Systems in the Universe
    var racesArray: Array<GBRace?> // the star Systems //

    val universeMaxX: Int
        get() = GBData.getUniverseMaxX()
    val universeMaxY: Int
        get() = GBData.getUniverseMaxY()

    init {

        this.numberOfStars = GBData.getNumberOfStars()
        stars = arrayOfNulls(size = numberOfStars)
        racesArray = arrayOfNulls(numberOfRaces)

        // Place Stars
        GBDebug.l3("Making Stars")
        makeStars()

        makeRaces()

        GBDebug.l3("Universe made")
    }

    internal fun consoleDraw() {
        println("=============================================")
        println("The Universe")
        println("=============================================")
        println("The Universe contains $numberOfStars star(s).\n")

        for (i in stars) {
            i?.consoleDraw()
        }
        println("The Universe contains $numberOfRaces race(s).\n")

        for (i in racesArray) {
            i?.consoleDraw()
        }

    }

    private fun makeStars() {
        GBDebug.l3("Making Stars")
        GBStar.resetStarCoordinates()
        for (i in 0 until numberOfStars) {
            stars[i] = GBStar(i)
        }

    }

    private fun makeRaces() {
        GBDebug.l3("Making Races")

        // Temporary hack

        var sector: GBSector
        racesArray[0] = GBRace(0, 0)
        landPopulation(stars[0]!!.planetsArray[0]!!, racesArray[0]!!.uId)

        if (numberOfStars > 1) {
            racesArray[1] = GBRace(1, 1)
            landPopulation(stars[1]!!.planetsArray[0]!!, racesArray[1]!!.uId)

        }

        if (numberOfStars > 2) {
            landPopulation(stars[2]!!.planetsArray[0]!!, racesArray[0]!!.uId)
            landPopulation(stars[2]!!.planetsArray[0]!!, racesArray[1]!!.uId)
        }

    }

    fun landPopulation(p: GBPlanet, uId: Int) {
        GBDebug.l3("GBUniverse: Landing 100 of " + racesArray[uId]!!.name + " on " + p.name + "")
        p.landPopulation(racesArray[uId]!!, 100)
    }


    internal fun doUniverse() {
        for (s in stars) {
            for (p in s!!.planetsArray) {
                p!!.doPlanet()
            }
        }

    }

    fun getPlanets(s: GBStar): Array<GBPlanet?> {
        return s!!.planetsArray
    } // TODO should this be Star? But what about getting all the planets?

    fun getSectors(p: GBPlanet): Array<GBSector> {
        return p!!.sectors
    } //TODO should this be in planet? Or Data?


}
