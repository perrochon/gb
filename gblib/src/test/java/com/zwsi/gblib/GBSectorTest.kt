// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.u
import org.junit.Assert.assertTrue
import org.junit.Assert.*
import org.junit.Test

class GBSectorTest {

    fun consistency(s: GBSector) {

        assertEquals(GBData.sectorTypeConsoleFromIdx(s.type), s.typeSymbol)
        assertEquals(GBData.sectorMoneyFromIdx(s.type), s.revenue)

        if (s.population == 0) {
            assertEquals(-1, s.uidSectorOwner)
        } else {
            assertNotEquals(-1, s.uidSectorOwner)
        }
    }

    @Test
    fun basicSector() {
        val universe = GBController.makeUniverse()
        val p = universe.planet(0)
        val s = GBSector(p.uid)
        assertEquals(u.planet(s.uidPlanet), p)
        assertEquals(-1, s.type)
        assertEquals("?", s.typeSymbol)
        assertEquals(0, s.population)
        assertEquals(-1, s.uidSectorOwner) // FIXME This failing until universe constructor is fixed.

        for (i in 0..7) {
            for (j in 1..10) { // Try a few times, as sector type is random
                s.chooseType(i)
                consistency(s)
            }
        }

        val allRaces = universe.allRaces.values.first()
        val r = allRaces
        p.adjustPopulation(s,r, 100)
        assertEquals(r.uid,s.uidSectorOwner)
        assertEquals(r, s.sectorOwner)
        assertEquals(r.birthrate, s.sectorOwner.birthrate)
        assertEquals(100, s.population)
        p.adjustPopulation(s,r,-50)
        assertEquals(50, s.population)
        p.adjustPopulation(s,r,-50)
        assertEquals(0, s.population)
        assertEquals(-1, s.uidSectorOwner)
    }

    @Test
    fun growPopulation() {
        val universe = GBController.makeUniverse()
        val p = universe.planet(0)
        val r = universe.allRaces.toList().component1().second
        val s = GBSector(p.uid)

        s.chooseType(2)

        assertEquals(0, s.population)
        p.growPopulation(s)
        assertEquals(0, s.population)

        p.adjustPopulation(s,r, 10)
        //GBLog.i("Birthrate = ${s.getBirthrate()}")
        //GBLog.i("MaxPopulation = ${s.maxPopulation}")
        //GBLog.i("Population = ${s.planetPopulation}")

        assertEquals(10, s.population)
        for (i in 1..1000) {
            p.growPopulation(s)
            //GBLog.i("Population = ${s.planetPopulation}")
        }
        assertTrue("Population not close to MaxPopulation", s.maxPopulation < s.population + 10)
    }

    @Test
    fun testMovePopulation() {
        val universe = GBController.makeUniverse()
        val p = universe.planet(0)
        val r = GBController.u.allRaces.toList().component1().second
        val s1 = GBSector(p.uid)
        val s2 = GBSector(p.uid)

        s1.chooseType(2)
        s2.chooseType(2)
        p.adjustPopulation(s1,r, 10)
        p.adjustPopulation(s2,r, 0)
        assertEquals(r.uid,s1.uidSectorOwner)
        assertEquals(-1,s2.uidSectorOwner)
        assertEquals(10, s1.population)
        assertEquals(0, s2.population)
        p.movePopulation(s1,3, s2)
        assertEquals(7, s1.population)
        assertEquals(3, s2.population)
        p.movePopulation(s1,3, s2)
        assertEquals(4, s1.population)
        assertEquals(6, s2.population)
        p.movePopulation(s1,4, s2)
        assertEquals(0, s1.population)
        assertEquals(10, s2.population)
        assertEquals(-1,s1.uidSectorOwner)
        assertEquals(r.uid,s2.uidSectorOwner)
    }

    @Test
    fun testAllUniverseSectors() {
        val universe = GBController.makeUniverse()
        for ((_, planet) in universe.allPlanets) {
            for (s in planet.sectors)
                consistency(s)
        }

    }


}
