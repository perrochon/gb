// Copyright 2018-2019 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.PlanetaryOrbit
import com.zwsi.gblib.GBData.Companion.SystemBoundary
import org.junit.Assert.assertEquals
import org.junit.Test

class LibTest {

    var shipsMade: Int = 0

    fun consistent() {
        GBController.u
        assertEquals(u.star(0).starPlanetsList[0], u.planet(0))
        assertEquals(u.star(0).starPlanetsList[1], u.planet(1))
    }

    @Test
    fun verifyConstants() {
        // If these are different, some other tests in this class will fail
        assertEquals(24, GBController.numberOfStars)
        assertEquals(4, GBController.numberOfRaces)
        assertEquals(5, GBController.numberOfStarsSmall)
        assertEquals(100, GBController.numberOfStarsBig)
    }

    @Test
    fun SmallUniverse() {
        GBController.makeSmallUniverse()
        consistent()

        assertEquals(100, u.star(0).starPlanetsList[0].planetPopulation)
        assertEquals(100, u.star(1).starPlanetsList[0].planetPopulation)
        assertEquals(100, u.star(2).starPlanetsList[0].planetPopulation)
        assertEquals(100, u.star(3).starPlanetsList[0].planetPopulation)


        populate()
        assertEquals(shipsMade, u.ships.size)
        consistent()


        GBController.doUniverse()
        consistent()
    }


    @Test
    fun BigUniverse() {
        GBController.makeBigUniverse()
        consistent()

        assertEquals(100, u.star(0).starPlanetsList[0].planetPopulation)
        assertEquals(100, u.star(1).starPlanetsList[0].planetPopulation)
        assertEquals(100, u.star(2).starPlanetsList[0].planetPopulation)
        assertEquals(100, u.star(3).starPlanetsList[0].planetPopulation)


        populate()
        assertEquals(shipsMade, u.ships.size)
        consistent()

        GBController.doUniverse()
        consistent()

        // mini-stress test
        for (i in 1..20)
            u.doUniverse()

    }

    @Test
    fun landPopulation() {
        GBController.makeUniverse()
        consistent()
        GBController.landPopulation(1, 0, 55)
        assertEquals(55, u.star(0).starPlanetsList[1].planetPopulation)
    }

    fun populate() {
        makeShips()
    }

    fun makeShips() {
        GBController.makeUniverse()

        val allRaces = u.races
        val allStars = u.stars
        val numberOfStars = allStars.size

        val r0 = allRaces.toList().component1().second
        val r1 = allRaces.toList().component2().second
        val r2 = allRaces.toList().component3().second
        val r3 = allRaces.toList().component4().second

        // Give each race a factory
        GBShip(u.getNextGlobalId(),0, r0.uid, GBLocation(allStars[0]!!.starPlanetsList[0],5,1))
        GBShip(u.getNextGlobalId(),0, r1.uid, GBLocation(allStars[1]!!.starPlanetsList[0],5,1))
        GBShip(u.getNextGlobalId(),0, r2.uid, GBLocation(allStars[2]!!.starPlanetsList[0],4,1))
        shipsMade +=3

        if (numberOfStars > 3) {
            GBShip(u.getNextGlobalId(),0, r3.uid, GBLocation(allStars[3]!!.starPlanetsList[0],PlanetaryOrbit,1f))
            shipsMade +=1
        }

        // Give each race a pod
        GBShip(u.getNextGlobalId(),1, r0.uid, GBLocation(allStars[0]!!.starPlanetsList[0],PlanetaryOrbit,1f))
        GBShip(u.getNextGlobalId(),1, r1.uid, GBLocation(allStars[1]!!.starPlanetsList[0],PlanetaryOrbit,1f))
        GBShip(u.getNextGlobalId(),1, r2.uid, GBLocation(allStars[2]!!.starPlanetsList[0],PlanetaryOrbit,1f))
        shipsMade +=3

        if (numberOfStars > 3) {
            GBShip(u.getNextGlobalId(),1, r3.uid, GBLocation(allStars[3]!!.starPlanetsList[0],PlanetaryOrbit,1f))
            shipsMade +=1
        }

        // Give each race a destroyer in system 3
        GBShip(u.getNextGlobalId(),2, r0.uid, GBLocation(allStars[2]!!,SystemBoundary,1f))
        GBShip(u.getNextGlobalId(),2, r1.uid, GBLocation(allStars[2]!!,SystemBoundary,2f))
        GBShip(u.getNextGlobalId(),2, r2.uid, GBLocation(allStars[2]!!,SystemBoundary,3f))
        GBShip(u.getNextGlobalId(),2, r3.uid, GBLocation(allStars[2]!!,SystemBoundary,.5f))
        shipsMade +=4

        // Give each race a destroyer in deep space
        GBShip(u.getNextGlobalId(),2, r0.uid, GBLocation(500f,500f))
        GBShip(u.getNextGlobalId(),2, r1.uid, GBLocation(500f,500f))
        GBShip(u.getNextGlobalId(),2, r2.uid, GBLocation(500f,500f))
        GBShip(u.getNextGlobalId(),2, r3.uid, GBLocation(500f,500f))
        shipsMade +=4
    }
}
