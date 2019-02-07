// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBPlanetTest {

    fun consistent(planet: GBPlanet){
        val universe = GBController.u
        assertTrue(planet.name.length > 0)

        assertTrue(planet.star.starUidPlanetList.contains(planet.uid))
        assertTrue(planet.star.starPlanetsList.contains(planet))
        assertTrue(universe.allPlanets.containsValue(planet))

        assertEquals(planet.sid,planet.star.starPlanetsList.indexOf(planet))
        assertEquals(planet,universe.allPlanets[planet.uid])

        var count = 0
        for ((uid, star) in universe.allStars) {
            for (pl in star.starPlanetsList) {
                if (pl.uid == planet.uid)
                    count++
            }
        }
        assertEquals(1, count)
    }

    @Test
    fun basic() {
        val universe = GBController.makeUniverse()


        for ((key, planet) in universe.allPlanets) {
            consistent(planet)
        }
    }

    @Test
    fun sizes() {
        val universe = GBController.makeUniverse()


        for ((key, planet) in universe.allPlanets) {
            val width = planet.width
            val height = planet.height

            assertEquals(width * height, planet.size)

            assertEquals(planet.sectorX(0), 0)
            assertEquals(planet.sectorY(0), 0)

            assertEquals(planet.sectorX(width - 1), width - 1)
            assertEquals(planet.sectorY(width - 1), 0)

            assertEquals(planet.sectorX(width), 0)
            assertEquals(planet.sectorY(width), 1)

            assertEquals(planet.sectorX(width * height - 1), width - 1)
            assertEquals(planet.sectorY(width * height - 1), height - 1)
        }
    }

    @Test
    fun distanceToStar() {
        val universe = GBController.makeUniverse()

        universe.allPlanets.forEach { (key, p) ->
            assert(p.loc.getLoc().distance(p.star.loc.getLoc())< (GBData.MaxPlanetOrbit*1.1f), {"Planet too far from star"} )
        }
    }

    @Test
    fun sectorDirections() {
        val universe = GBController.makeUniverse()

        for ((key, planet) in universe.allPlanets) {
            val width = planet.width
            val height = planet.height

            assertEquals(width * height, planet.size)

            assertEquals(planet.east(0), 1)
            assertEquals(planet.west(0), width - 1)
            assertEquals(planet.north(0), 0)
            assertEquals(planet.south(0), width)

            assertEquals(planet.east(width * height - 1), width * (height - 1))
            assertEquals(planet.west(width * height - 1), width * height - 2)
            assertEquals(planet.north(width * height - 1), width * (height - 1) - 1)
            assertEquals(planet.south(width * height - 1), width * height - 1)

            assertEquals(planet.west(width * (height - 1)), width * height - 1)
            assertEquals(planet.east(width * height - 2), width * height - 1)
            assertEquals(planet.south(width * (height - 1) - 1), width * height - 1)
        }
    }

    @Test
    fun planetMoves() {

        val universe = GBController.makeUniverse()

        for ((key, planet) in universe.allPlanets) {

            val rt1 = planet.loc.getSLocP()
            val xy1 = planet.loc.getSLocC()

            planet.movePlanet()

            var rt2 = planet.loc.getSLocP()
            var xy2 = planet.loc.getSLocC()

            assertEquals(rt1.r, rt2.r)
            assert(xy1.distance(xy2) < 0.99) // Or pods can never catch up
        }
    }

}
