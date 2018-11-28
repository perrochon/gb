// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
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

        assertTrue(ship.race.raceShips.contains(ship))
        assertEquals(ship.uid, ship.race.raceShips.indexOf(ship))

        when (ship.loc.level) {
            LANDED -> {
                assertTrue(universe.allPlanets[ship.loc.refUID].landedShips.contains(ship))
            }
            ORBIT -> {
                assertTrue(universe.allPlanets[ship.loc.refUID].orbitShips.contains(ship))
            }
            SYSTEM -> {
                assertTrue(universe.allStars[ship.loc.refUID].starShips.contains(ship))
            }
            DEEPSPACE -> {
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
        for (race in ship.race.universe.allRaces) {
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
        val p : GBPlanet = s.starPlanets[0]
        val r: GBRace = universe.allRaces[0]


        var sh = GBShip(0, r, GBLocation(500f,500f))
        consistency(sh)

        sh = GBShip(0, r, GBLocation(s, 10f, 1f))
        consistency(sh)

        sh = GBShip(1, r, GBLocation(p, 5f, 1f))
        consistency(sh)

        sh = GBShip(1, r, GBLocation(p, 1,1))
        consistency(sh)

        uniqueLocations()
    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val s1 = universe.allStars[1]
        val r0: GBRace = universe.allRaces[0]

        val sh0 = GBShip(0, r0, GBLocation(s1, 30f,1f))
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

        val sh0 = GBShip(1, r0, GBLocation(s0, 30f,1f))
        consistency(sh0)

        r1.raceShips.add(sh0)

        consistency(sh0)
        uniqueLocations()
    }

}
