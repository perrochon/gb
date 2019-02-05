// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBData.Companion.PlanetaryOrbit
import com.zwsi.gblib.GBData.Companion.SystemBoundary
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

        assertTrue(ship.race.raceShipsUID.contains(ship.uid))
        assertTrue(ship.race.getRaceShipsList().contains(ship)) // This fails because list is cached

        if (!universe.deadShips.contains(ship)) {

            when (ship.loc.level) {
                LANDED -> {
                    assertTrue(universe.allPlanets[ship.loc.uidRef].landedShips.contains(ship))
                }
                ORBIT -> {
                    assertTrue(universe.allPlanets[ship.loc.uidRef].orbitShips.contains(ship))
                }
                SYSTEM -> {
                    assertTrue(universe.allStars[ship.loc.uidRef].starShips.contains(ship))
                }
                DEEPSPACE -> {
                    assertTrue(universe.deepSpaceShips.contains(ship))
                }
                else -> {
                    assert(false)
                }
            }
        }

        // Make sure this ship is only in one location
        var found = 0

        GBLog.d("Looking all over for ship: " + ship.uid)

        for (sh in universe.deepSpaceShips) {  // TODO use "contains"
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
        for ((key, race) in universe.allRaces) {
            for (uid in race.getRaceShipsUIDList()) {
                if (uid == ship.uid)
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
            for (sh in un.deepSpaceShips) {
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
        GBController.makeUniverse()

        GBLog.d("Testing " + universe.allShips.size + " ships")

        // Test all ships there are
        for (ship in universe.allShips) {
            consistency(ship)
        }

        // Just in case there aren't any, make a few more
        val s = universe.allStars[0]
        val p: GBPlanet = s.starPlanets[0]
        val r: GBRace = universe.allRaces.toList().component1().second!!

        var sh = GBShip(1, r, GBLocation(500f, 500f))
        consistency(sh)

        sh = GBShip(0, r, GBLocation(s, SystemBoundary, 2f))
        consistency(sh)

        sh = GBShip(1, r, GBLocation(p, PlanetaryOrbit, 2f))
        consistency(sh)

        sh = GBShip(1, r, GBLocation(p, 1, 1))
        consistency(sh)

        uniqueLocations()


    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val universe = GBController.makeUniverse()

        val s1 = universe.allStars[1]
        val r0= GBController.universe.allRaces.toList().component1().second!!

        val sh0 = GBShip(0, r0, GBLocation(s1, 30f, PlanetaryOrbit))
        consistency(sh0)

        s1.starShips.add(sh0)
        consistency(sh0)
        uniqueLocations()

    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoRacesFailsConsistency() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val r0= GBController.universe.allRaces.toList().component1().second!!
        val r1= GBController.universe.allRaces.toList().component2().second!!

        val sh0 = GBShip(1, r0, GBLocation(s0, 30f, 1f))
        consistency(sh0)

        r1.raceShipsUID.add(sh0.uid)

        consistency(sh0)
        uniqueLocations()
    }

    @Test
    fun moveCruiser() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val p0 = s0.starPlanets[0]
        val s1 = universe.allStars[1]
        val p1 = s1.starPlanets[1]
        val r0 = GBController.universe.allRaces.toList().component1().second!!

        val locations = arrayListOf<GBLocation>()

        locations.add(GBLocation(p0, 1, 1))
        locations.add(GBLocation(p1, 1, 2))
        locations.add(GBLocation(p0, 2, 1))
        locations.add(GBLocation(p1, 2, 2))
        locations.add(GBLocation(p0, 1f, 1f))
        locations.add(GBLocation(p1, 1f, 2f))
        locations.add(GBLocation(p0, 1.1f, .5f))
        locations.add(GBLocation(p1, 1.1f, 3f))
        locations.add(GBLocation(s0, 20f, 1f))
        locations.add(GBLocation(s1, 21f, 2f))
        locations.add(GBLocation(s0, 10f, 3f))
        locations.add(GBLocation(s1, 15f, 0.5f))
        locations.add(GBLocation(500f, 500f))

        val sh0 = GBShip(2, r0, locations.first())

        var lastLocation: GBxy

        for (loc1 in locations) {
            sh0.changeShipLocation(loc1)
            consistency(sh0)
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
        val r0 = GBController.universe.allRaces.toList().component1().second!!

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(1, r0, loc01)

        sh.dest = loc02

        var i = 0
        var lastLocation : GBLocation
        while (sh.loc != loc02) {

            lastLocation = sh.loc

            sh.doShip()

            var distance_moved = lastLocation.getLoc().distance(sh.loc.getLoc())
            assert(distance_moved < 5, {
                "Ship moved $distance_moved from ${lastLocation.getLocDesc()} to ${sh.loc.getLocDesc()} : " +
                        "${lastLocation.getLoc()} -> ${sh.loc.getLoc()}"
            })

            uniqueLocations()
            assertTrue(i < 30)
            i++
        }
    }


    @Test
    fun sendPodOtherSystem() {
        val universe = GBController.makeUniverse()

        val s0 = universe.allStars[0]
        val s1 = universe.allStars[1]
        val p0 = s0.starPlanets[0]
        val p1 = s1.starPlanets[1]
        val r0 = GBController.universe.allRaces.toList().component1().second!!

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(1, r0, loc01)

        sh.dest = loc02

        var i = 0
        var lastLocation: GBLocation
        while (sh.loc != loc02) {

            lastLocation = sh.loc

            sh.doShip()

            var distance_moved = lastLocation.getLoc().distance(sh.loc.getLoc())
            assert(distance_moved < 5, {
                "Ship moved $distance_moved from ${lastLocation.getLocDesc()} to ${sh.loc.getLocDesc()} : " +
                        "${lastLocation.getLoc()} -> ${sh.loc.getLoc()}"
            })

            uniqueLocations()
            assertTrue(i < 2000)
            i++
        }

    }
}