// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.assertEquals
import org.junit.Test

class GBShipTest {

    fun consistency(ship: GBShip) {
        assertEquals(ship.uid, ship.owner.universe.allShips.indexOf(ship))
        assert(ship.owner.raceShips.contains(ship))

        when(ship.level){
            1 -> {}
            2 -> {}
            3 -> {
                assert(ship.owner.universe.allStars[ship.locationuid].starShips.contains(ship))
            }
            4 -> {}
            else -> {
                assert(false)
            }

        }

        // Make sure ship is only in one location
        var found = 0
        val un = ship.owner.universe
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
    fun uniqueLocations(un: GBUniverse) {
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
        val universe = GBUniverse(3, 2)
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

        uniqueLocations(universe)
    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val universe = GBUniverse(3, 2)
        val s0 = universe.allStars[0]
        val s1 = universe.allStars[1]
        val r0: GBRace = universe.allRaces[0]

        val sh0 = GBShip(0, r0, 3, s0.uid)
        consistency(sh0)

        s1.starShips.add(sh0)
        consistency(sh0)
        uniqueLocations(universe)

    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoRacesFailsConsistency() {
        val universe = GBUniverse(3, 2)
        val s0 = universe.allStars[0]
        val r0: GBRace = universe.allRaces[0]
        val r1: GBRace = universe.allRaces[1]

        val sh0 = GBShip(1, r0, 3, s0.uid)
        consistency(sh0)

        r1.raceShips.add(sh0)

        consistency(sh0)
        uniqueLocations(universe)
    }

}
