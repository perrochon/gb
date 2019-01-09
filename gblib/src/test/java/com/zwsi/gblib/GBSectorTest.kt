// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GBSectorTest {

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
        assertEquals(0, s.getBirthrate())

        for (i in 0..7) {
            for (j in 1..10) { // Try a few times, as sector type is random
                s.chooseType(i)
                assertEquals(GBData.sectorTypeConsoleFromIdx(s.type), s.typeSymbol)
                assertEquals(GBData.sectorMoneyFromIdx(s.type), s.revenue)
            }
        }

        val r = universe.allRaces[0]
        s.assignPopulation(r, 1000)
        assertEquals(r,s.owner)
        assertEquals(r.birthrate, s.getBirthrate())
        assertEquals(1000, s.population)
    }

    @Test
    fun populationGrowth() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[0]
        val r = universe.allRaces[0]
        val s = GBSector(p)

        s.chooseType(2)
        s.assignPopulation(r, 10)
        GBLog.i("Birthrate = ${s.getBirthrate()}")
        GBLog.i("MaxPopulation = ${s.maxPopulation}")

        assertEquals(10, s.population)
        for (i in 1..100) {
            s.growPopulation()
            GBLog.i("Population = ${s.population}")
        }
    }


}
