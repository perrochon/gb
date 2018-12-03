// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GBShipTest {

    fun consistency(ship: GBShip) {

        val universe = GBController.universe

        GBLog.d("Looking at ship " + ship.uid + " with name " + ship.name)

        assertTrue(ship.name.length > 0)

        assertEquals(universe, universe)

        assertTrue(universe.allShips.contains(ship))
        assertEquals(ship.uid, universe.allShips.indexOf(ship))

        assertTrue(ship.race.getRaceShipsList().contains(ship))
        assertEquals(ship.uid, ship.race.getRaceShipsList().indexOf(ship))

        if (!universe.deadShips.contains(ship)) {

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
        }

        // Make sure this ship is only in one location
        var found = 0

        GBLog.d("Looking all over for ship: " + ship.uid)

        for (sh in universe.universeShips) {  // TODO use "contains"
            if (sh.uid == ship.uid) {
                found++
                GBLog.d("Found in deep space: ship: " + sh.uid)
            }
        }
        for (st in universe.allStars) {
            for (sh in st.starShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBLog.d("Found in star: ship: " + sh.uid + " in star: " + st.uid)
                }
            }
        }
        for (pl in universe.allPlanets) {
            for (sh in pl.orbitShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBLog.d("Found in orbit: ship: " + sh.uid + " around planet: " + pl.uid)
                }
            }
            for (sh in pl.landedShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBLog.d("Found landed: ship: " + sh.uid + " ion planet: " + pl.uid)
                }
            }
        }
        for (sh in universe.deadShips) {
            if (sh.uid == ship.uid) {
                found++
                GBLog.d("Found among the dead: ship: " + sh.uid)
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
            for (sh in un.deadShips) {
                if (sh.uid == ship.uid)
                    found++
            }
            assertEquals(1, found)
        }
    }

    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        GBLog.d("Testing " + universe.allShips.size + " ships")

        // Test all ships there are
        for (ship in universe.allShips) {
            consistency(ship)
        }

        // Just in case there aren't any, make a few more
        val s = universe.allStars[0]
        val p: GBPlanet = s.starPlanets[0]
        val r: GBRace = universe.allRaces[0]

        var sh = GBShip(0, r, GBLocation(500f, 500f))
        consistency(sh)

        sh = GBShip(0, r, GBLocation(s, 10f, 1f))
        consistency(sh)

        sh = GBShip(1, r, GBLocation(p, 5f, 1f))
        consistency(sh)

        sh = GBShip(1, r, GBLocation(p, 1, 1))
        consistency(sh)

        uniqueLocations()


    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val s1 = universe.allStars[1]
        val r0: GBRace = universe.allRaces[0]

        val sh0 = GBShip(0, r0, GBLocation(s1, 30f, 1f))
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

        val sh0 = GBShip(1, r0, GBLocation(s0, 30f, 1f))
        consistency(sh0)

        r1.raceShips.add(sh0)

        consistency(sh0)
        uniqueLocations()
    }

    @Test
    fun moveShip() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val p0 = s0.starPlanets[0]
        val s1 = universe.allStars[1]
        val p1 = s1.starPlanets[1]
        val r0 = universe.allRaces[0]

        val locations = arrayListOf<GBLocation>()

        val loc01 = GBLocation(p0, 1, 1); locations.add(loc01)
        val loc02 = GBLocation(p1, 1, 2); locations.add(loc01)
        val loc03 = GBLocation(p0, 2, 1); locations.add(loc01)
        val loc04 = GBLocation(p1, 2, 2); locations.add(loc01)
        val loc11 = GBLocation(p0, 1f, 1f); locations.add(loc11)
        val loc12 = GBLocation(p1, 1f, 2f); locations.add(loc12)
        val loc13 = GBLocation(p0, 2f, .5f); locations.add(loc13)
        val loc14 = GBLocation(p1, 2f, 3f); locations.add(loc14)
        val loc21 = GBLocation(s0, 20f, 1f); locations.add(loc21)
        val loc22 = GBLocation(s1, 30f, 2f); locations.add(loc22)
        val loc23 = GBLocation(s0, 10f, 3f); locations.add(loc23)
        val loc24 = GBLocation(s1, 15f, 0.5f); locations.add(loc24)
        val loc31 = GBLocation(500f, 500f); locations.add(loc31)

        val sh0 = GBShip(0, r0, loc01)
        // This will not work for spores, as they explode on landing, so use factory for now, which doesn't move...

        for (loc1 in locations) {
            sh0.changeShipLocation(loc1)
            consistency(sh0) // This now fails that pods self destruct. Need to fix the test
            uniqueLocations()
            for (loc2 in locations) {
                sh0.changeShipLocation(loc2)
                consistency(sh0)
                uniqueLocations()
            }
        }
    }

    @Test
    fun sendPodInSystem() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val p0 = s0.starPlanets[0]
        val p1 = s0.starPlanets[1]
        val r0 = universe.allRaces[0]

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(1, r0, loc01)

        sh.dest = loc02

        while (sh.loc != loc02) {
            sh.doShip()
            uniqueLocations()
        }
    }


    @Test
    fun sendPodOtherSystem() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val s1: GBStar = universe.allStars[1]
        val p0 = s0.starPlanets[0]
        val p1 = s1.starPlanets[1]
        val r0 = universe.allRaces[0]

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(1, r0, loc01)

        sh.dest = loc02

        while (sh.loc != loc02) {
            sh.doShip()
            uniqueLocations()
        }

    }
}