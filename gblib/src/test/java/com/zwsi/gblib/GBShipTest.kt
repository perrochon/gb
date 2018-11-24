// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.*
import org.junit.Test
import javax.swing.DebugGraphics

class GBShipTest {

    fun consistency(ship: GBShip) {

        val universe = GBController.universe

        GBDebug.l3("Looking at ship " + ship.uid + " with name " + ship.name)

        assertTrue(ship.name.length > 0)

        assertEquals(universe, universe)

        assertTrue(universe.allShips.contains(ship))
        assertEquals(ship.uid, universe.allShips.indexOf(ship))

        assertTrue(ship.owner.raceShips.contains(ship))
        assertEquals(ship.uid, ship.owner.raceShips.indexOf(ship))

        when (ship.level) {
            1 -> {
                assertTrue(universe.allPlanets[ship.locationuid].landedShips.contains(ship))
                assertFalse(universe.allPlanets[ship.locationuid].orbitShips.contains(ship))
                assertFalse(universe.allStars[ship.locationuid].starShips.contains(ship))
                assertFalse(universe.universeShips.contains(ship))
            }
            2 -> {
                assertFalse(universe.allPlanets[ship.locationuid].landedShips.contains(ship))
                assertTrue(universe.allPlanets[ship.locationuid].orbitShips.contains(ship))
                assertFalse(universe.allStars[ship.locationuid].starShips.contains(ship))
                assertFalse(universe.universeShips.contains(ship))
            }
            3 -> {
                assertFalse(universe.allPlanets[ship.locationuid].landedShips.contains(ship))
                assertFalse(universe.allPlanets[ship.locationuid].orbitShips.contains(ship))
                assertTrue(universe.allStars[ship.locationuid].starShips.contains(ship))
                assertFalse(universe.universeShips.contains(ship))
            }
            4 -> {
                assertFalse(universe.allPlanets[ship.locationuid].landedShips.contains(ship))
                assertFalse(universe.allPlanets[ship.locationuid].orbitShips.contains(ship))
                assertFalse(universe.allStars[ship.locationuid].starShips.contains(ship))
                assertTrue(universe.universeShips.contains(ship))
            }
            else -> {
                assert(false)
            }

        }

        // Make sure this ship is only in one location
        var found = 0

        GBDebug.l3("Looking all over for ship: " + ship.uid)

        for (sh in universe.universeShips) {
            if (sh.uid == ship.uid) {
                found++
                GBDebug.l3("Found in deep space: ship: " + sh.uid)
            }
        }
        for (st in universe.allStars) {
            for (sh in st.starShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBDebug.l3("Found in star: ship: " + sh.uid + " in star: " + st.uid)
                }
            }
        }
        for (pl in universe.allPlanets) {
            for (sh in pl.orbitShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBDebug.l3("Found in orbit: ship: " + sh.uid + " around planet: " + pl.uid)
                }
            }
            for (sh in pl.landedShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBDebug.l3("Found landed: ship: " + sh.uid + " ion planet: " + pl.uid)
                }
            }
        }
        assertEquals(1, found)

        // Make sure ship is only in one race
        found = 0
        for (race in ship.owner.universe.allRaces) {
            for (sh in race.raceShips) {
                if (sh.uid == ship.uid)
                    found++
            }
        }
        assertEquals(1, found)

    }

    // TODO use @After, but need to figure out access to Universe
    fun uniqueLocations() {
        val un = GBController.universe
        for (ship in un.allShips) {
            var found = 0
            for (sh in un.universeShips) {
                if (sh.uid == ship.uid)
                    found++
            }
            for (st in un.allStars) {
                for (sh in st.starShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for (pl in un.allPlanets) {
                for (sh in pl.orbitShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for (pl in un.allPlanets) {
                for (sh in pl.landedShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            assertEquals(1, found)
        }
    }

    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        GBDebug.l3("Testing " + universe.allShips.size + " ships")

        // Test all ships there are
        for (ship in universe.allShips) {
           consistency(ship)
        }

        // Just in case there aren't any, make a few more
        val s = universe.allStars[0]
        val r: GBRace = universe.allRaces[0]


        var sh = GBShip(0, r, 4, 1)
        consistency(sh)

        sh = GBShip(0, r, 3, s.uid)
        consistency(sh)

        sh = GBShip(1, r, 2, s.starPlanets[0].uid)
        consistency(sh)

        sh = GBShip(1, r, 1, s.starPlanets[0].uid)
        consistency(sh)

        uniqueLocations()
    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val s1 = universe.allStars[1]
        val r0: GBRace = universe.allRaces[0]

        val sh0 = GBShip(0, r0, 3, s0.uid)
        consistency(sh0)

        s1.starShips.add(sh0)
        consistency(sh0)
        uniqueLocations()

    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoRacesFailsConsistency() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val r0: GBRace = universe.allRaces[0]
        val r1: GBRace = universe.allRaces[1]

        val sh0 = GBShip(1, r0, 3, s0.uid)
        consistency(sh0)

        r1.raceShips.add(sh0)

        consistency(sh0)
        uniqueLocations()
    }

}
