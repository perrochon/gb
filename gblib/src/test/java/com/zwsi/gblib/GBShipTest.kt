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
        assert(ship.position.starShips.contains(ship))

        // Make sure ship is only in one star system
        var listed = 0
        for (star in ship.owner.universe.allStars) {
            for (sh in star.starShips) {
                if (sh.uid == ship.uid)
                    listed++
            }
        }
        assertEquals(1, listed)

        // Make sure ship is only in one race
        listed = 0
        for (race in ship.owner.universe.allRaces) {
            for (sh in race.raceShips) {
                if (sh.uid == ship.uid)
                    listed++
            }
        }
        assertEquals(1, listed)

    }

    @Test
    fun basic() {
        val universe = GBUniverse(3, 2)
        val s0 = universe.allStars[0]
        val r0: GBRace = universe.allRaces[0]

        val sh0 = GBShip(0, r0, s0)
        consistency(sh0)

        val s1 = universe.allStars[1]
        val r1: GBRace = universe.allRaces[1]

        val sh1 = GBShip(1, r1, s1)
        consistency(sh1)
    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val universe = GBUniverse(3, 2)
        val s0 = universe.allStars[0]
        val s1 = universe.allStars[1]
        val r0: GBRace = universe.allRaces[0]

        val sh0 = GBShip(0, r0, s0)
        consistency(sh0)

        s1.starShips.add(sh0)
        consistency(sh0)

    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoRacesFailsConsistency() {
        val universe = GBUniverse(3, 2)
        val s0 = universe.allStars[0]
        val r0: GBRace = universe.allRaces[0]
        val r1: GBRace = universe.allRaces[1]

        val sh0 = GBShip(1, r0, s0)
        consistency(sh0)

        r1.raceShips.add(sh0)

        consistency(sh0)
    }

}
