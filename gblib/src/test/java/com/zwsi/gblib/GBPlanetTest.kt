// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test

import org.junit.Assert.*

class GBPlanetTest {

    fun consistent(planet: GBPlanet){
        assertTrue(planet.name.length > 0)

        assertTrue(planet.star.starUidPlanets.contains(planet.uid))
        assertTrue(planet.star.starPlanetsList.contains(planet))
        assertTrue(u.planets.containsValue(planet))
        assertEquals(planet,u.planets[planet.uid])

        assertEquals(planet.width, planet.height * 2)

        var count = 0
        for ((_, star) in u.stars) {
            for (pl in star.starPlanetsList) {
                if (pl.uid == planet.uid)
                    count++
            }
        }
        assertEquals(1, count)
    }

    @Test
    fun basic() {
        GBController.makeUniverse()


        for ((_, planet) in u.planets) {
            consistent(planet)
        }
    }

    @Test
    fun sizes() {
        GBController.makeUniverse()

        for ((_, planet) in u.planets) {
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
        GBController.makeUniverse()

        u.planets.forEach { (_, p) ->
            assert(p.loc.getLoc().distance(p.star.loc.getLoc())< (GBData.MaxSystemOrbit*1.1f), {"Planet too far from star"} )
        }
    }

    @Test
    fun sectorDirections() {
        GBController.makeUniverse()

        for ((_, planet) in u.planets) {
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

        GBController.makeUniverse()

        for ((_, planet) in u.planets) {

            val rt1 = planet.loc.getSLocP()
            val xy1 = planet.loc.getSLocC()

            planet.movePlanet()

            val rt2 = planet.loc.getSLocP()
            val xy2 = planet.loc.getSLocC()

            assertEquals(rt1.r, rt2.r)
            assert(xy1.distance(xy2) < 0.99) // Or pods can never catch up
        }
    }

}
