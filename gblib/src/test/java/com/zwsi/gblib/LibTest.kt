// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LibTest {

    var shipsMade: Int = 0

    fun consistent() {
        val universe = GBController.universe

        if (GBController.smallUniverse) {
            assertEquals(GBController.numberOfStarsSmall, universe.allStars.size)
        } else if (GBController.bigUniverse) {
            assertEquals(GBController.numberOfStarsBig, universe.allStars.size)
        } else {
            assertEquals(GBController.numberOfStars, universe.allStars.size)
        }
        assertEquals(universe.allStars[0].starPlanets[0], universe.allPlanets[0])
        assertEquals(universe.allStars[0].starPlanets[1], universe.allPlanets[1])
    }

    @Test
    fun verifyConstants() {
        // If these are different, some other tests in this class will fail
        assertEquals(24, GBController.numberOfStars)
        assertEquals(4, GBController.numberOfRaces)
        assertEquals(5, GBController.numberOfStarsSmall)
        assertEquals(4, GBController.numberOfRacesSmall)
        assertEquals(100, GBController.numberOfStarsBig)
        assertEquals(4, GBController.numberOfRacesBig)
    }

    @Test
    fun SmallUniverse() {
        val universe = GBController.makeSmallUniverse()
        consistent()

        assertEquals(100, universe.allStars[0].starPlanets[0].population)
        assertEquals(100, universe.allStars[1].starPlanets[0].population)
        assertEquals(100, universe.allStars[2].starPlanets[0].population)
        assertEquals(100, universe.allStars[3].starPlanets[0].population)


        populate()
        assertEquals(shipsMade, universe.allShips.size)
        consistent()


        GBController.doUniverse()
        consistent()
    }


    @Test
    fun BigUniverse() {
        val universe = GBController.makeBigUniverse()
        consistent()

        assertEquals(100, universe.allStars[0].starPlanets[0].population)
        assertEquals(100, universe.allStars[1].starPlanets[0].population)
        assertEquals(100, universe.allStars[2].starPlanets[0].population)
        assertEquals(100, universe.allStars[3].starPlanets[0].population)


        populate()
        assertEquals(shipsMade, universe.allShips.size)
        consistent()

        GBController.doUniverse()
        consistent()

        // mini-stress test
        for (i in 1..100)
            universe.doUniverse()

    }

    @Test
    fun landPopulation() {
        val universe = GBController.makeUniverse()
        consistent()
        universe.landPopulation(universe.allPlanets[1], universe.allRaces[0].uid, 55)
        assertEquals(55, universe.allStars[0].starPlanets[1].population)
    }

    fun populate() {
        makeShips()
    }

    fun makeShips() {
        val allRaces = GBController.universe.allRaces
        val allStars = GBController.universe.allStars
        val numberOfStars = allStars.size


        // Give each race a factory
        GBShip(0, allRaces[0], GBLocation(allStars[0].starPlanets[0],5,1))
        GBShip(0, allRaces[1], GBLocation(allStars[1].starPlanets[0],5,1))
        GBShip(0, allRaces[2], GBLocation(allStars[2].starPlanets[0],4,1))
        shipsMade +=3

        if (numberOfStars > 3) {
            GBShip(0, allRaces[3], GBLocation(allStars[3].starPlanets[0],5f,1f))
            shipsMade +=1
        }

        // Give each race a pod
        GBShip(1, allRaces[0], GBLocation(allStars[0].starPlanets[0],5f,1f))
        GBShip(1, allRaces[1], GBLocation(allStars[1].starPlanets[0],5f,1f))
        GBShip(1, allRaces[2], GBLocation(allStars[2].starPlanets[0],5f,1f))
        shipsMade +=3

        if (numberOfStars > 3) {
            GBShip(1, allRaces[3], GBLocation(allStars[3].starPlanets[0],5f,1f))
            shipsMade +=1
        }

        // Give each race a destroyer in system 3
        GBShip(2, allRaces[0], GBLocation(allStars[2],5f,1f))
        GBShip(2, allRaces[1], GBLocation(allStars[2],5f,2f))
        GBShip(2, allRaces[2], GBLocation(allStars[2],5f,3f))
        GBShip(2, allRaces[3], GBLocation(allStars[2],5f,.5f))
        shipsMade +=4

        // Give each race a destroyer in deep space
        GBShip(2, allRaces[0], GBLocation(500f,500f))
        GBShip(2, allRaces[1], GBLocation(500f,500f))
        GBShip(2, allRaces[2], GBLocation(500f,500f))
        GBShip(2, allRaces[3], GBLocation(500f,500f))
        shipsMade +=4
    }

}
