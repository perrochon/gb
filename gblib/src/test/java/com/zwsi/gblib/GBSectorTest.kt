// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import junit.framework.Assert.assertTrue
import org.junit.Assert.*
import org.junit.Test

class GBSectorTest {

    fun consistency(s: GBSector) {

        assertEquals(GBData.sectorTypeConsoleFromIdx(s.type), s.typeSymbol)
        assertEquals(GBData.sectorMoneyFromIdx(s.type), s.revenue)

        if (s.population == 0) {
            assertNull(s.owner)
        } else {
            assertNotNull(s.owner)
        }

    }

    @Test
    fun basicSector() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[0]
        val s = GBSector(p)
        assertEquals(s.planet, p)
        assertEquals(-1, s.type)
        assertEquals("?", s.typeSymbol)
        assertEquals(0, s.population)
        assertNull(s.owner)

        for (i in 0..7) {
            for (j in 1..10) { // Try a few times, as sector type is random
                s.chooseType(i)
                consistency(s)
            }
        }

        val allRaces = universe.allRaces.values.first()
        val r = allRaces
        s.adjustPopulation(r, 100)
        assertEquals(r,s.owner)
        assertEquals(r.birthrate, s.owner!!.birthrate)
        assertEquals(100, s.population)
        s.adjustPopulation(r,-50)
        assertEquals(50, s.population)
        s.adjustPopulation(r,-50)
        assertEquals(0, s.population)
        assertNull(s.owner)
    }

    @Test
    fun testGrowPopulation() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[0]
        val r = universe.allRaces.toList().component1().second!!
        val s = GBSector(p)

        s.chooseType(2)

        assertEquals(0, s.population)
        s.growPopulation()
        assertEquals(0, s.population)

        s.adjustPopulation(r, 10)
        //GBLog.i("Birthrate = ${s.getBirthrate()}")
        //GBLog.i("MaxPopulation = ${s.maxPopulation}")
        //GBLog.i("Population = ${s.population}")

        assertEquals(10, s.population)
        for (i in 1..1000) {
            s.growPopulation()
            //GBLog.i("Population = ${s.population}")
        }
        assertTrue("Population not close to MaxPopulation", s.maxPopulation < s.population + 10)
    }

    @Test
    fun testMovePopulation() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[0]
        val r = GBController.universe.allRaces.toList().component1().second!!
        val s1 = GBSector(p)
        val s2 = GBSector(p)

        s1.chooseType(2)
        s2.chooseType(2)
        s1.adjustPopulation(r, 10)
        s2.adjustPopulation(r, 0)
        assertEquals(r,s1.owner)
        assertEquals(null,s2.owner)
        assertEquals(10, s1.population)
        assertEquals(0, s2.population)
        s1.movePopulation(3, s2)
        assertEquals(7, s1.population)
        assertEquals(3, s2.population)
        s1.movePopulation(3, s2)
        assertEquals(4, s1.population)
        assertEquals(6, s2.population)
        s1.movePopulation(4, s2)
        assertEquals(0, s1.population)
        assertEquals(10, s2.population)
        assertEquals(null,s1.owner)
        assertEquals(r,s2.owner)
    }

    @Test
    fun testAllUniverseSectors() {
        val universe = GBController.makeUniverse()
        for (p in universe.allPlanets) {
            for (s in p.sectors)
                consistency(s)
        }

    }


}
