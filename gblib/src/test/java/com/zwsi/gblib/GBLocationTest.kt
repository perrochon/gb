// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class GBLocationTest {

    fun consistent(l: GBLocation) {

        when (l.level) {
            GBLocation.LANDED -> {
                assertEquals(GBLocation.LANDED, l.level)
                assertEquals(-1f, l.x)
                assertEquals(-1f, l.y)
                assertNotEquals(-1, l.refUID)
                assertEquals(-1f, l.t)
                assertEquals(-1f, l.r)
                assertEquals(l.getLLoc().sx, l.sx)
                assertEquals(l.getLLoc().sy, l.sy)
                assertNotEquals(null, universe.allPlanets.getOrNull(l.refUID)) //
                // Do some checks on the LocDesc
            }
            GBLocation.ORBIT -> {
                assertEquals(GBLocation.ORBIT, l.level)
                assertEquals(l.getOLocC().x, l.x)   // cartesian now returns location of planet... Not sure it should
                assertEquals(l.getOLocC().y, l.y)
                assertNotEquals(-1, l.refUID)
                assertEquals(l.getOLocP().r, l.t)
                assertEquals(l.getOLocP().t, l.r)
                assertEquals(-1, l.sx)
                assertEquals(-1, l.sx)
                assertNotEquals(null, universe.allPlanets.getOrNull(l.refUID)) //
                // Do some checks on the LocDesc
            }
            GBLocation.SYSTEM -> {
                assertEquals(GBLocation.SYSTEM, l.level)
                assertEquals(l.getSLocC().x, l.x)
                assertEquals(l.getSLocC().y, l.y)
                assertNotEquals(-1, l.refUID)
                assertEquals(l.getSLocP().r, l.r)
                assertEquals(l.getSLocP().t, l.t)
                assertEquals(-1, l.sx)
                assertEquals(-1, l.sx)
                assertNotEquals(null, universe.allStars.getOrNull(l.refUID)) //

                assertEquals(l.x, universe.allStars[l.refUID].loc.x + l.r * cos(l.t))
                assertEquals(l.y, universe.allStars[l.refUID].loc.y - l.r * sin(l.t))

                // Do some checks on the LocDesc
            }
            GBLocation.DEEPSPACE -> {
                assertEquals(GBLocation.DEEPSPACE, l.level)
                assertEquals(l.getLoc().x, l.x)
                assertEquals(l.getLoc().y, l.y)
                assertEquals(-1, l.refUID)
                assertEquals(-1f, l.r)
                assertEquals(-1f, l.t)
                assertEquals(-1, l.sx)
                assertEquals(-1, l.sx)
                assertEquals("Deep Space", l.getLocDesc())
            }
            else -> {
                GBDebug.gbAssert("Ship in Limbo", { true })
            }
        }
    }

    @Test
    fun orbit() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[1]
        val loc = GBLocation(p,0f, 25f)
        assertEquals(0f, loc.r)
        assertEquals(25f, loc.t)
        consistent(loc)
    }

    @Test
    fun landed() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[2]
        val loc = GBLocation(p,1, 2)
        assertEquals(1, loc.sx)
        assertEquals(2, loc.sy)
        consistent(loc)
    }

    // TODO Should location enforce that insystem is somewhat close to a star???

    @Test
    fun system() {
        val universe = GBController.makeUniverse()
        val s = universe.allStars[3]
        val loc = GBLocation(s,200f, 2f)
        assertEquals(200f, loc.r)
        assertEquals(2f, loc.t)
        consistent(loc)
    }

    @Test
    fun deepSpace() {
        val universe = GBController.makeUniverse()
        val loc = GBLocation(500f, 400f)
        assertEquals(500f, loc.getLoc().x)
        assertEquals(400f, loc.getLoc().y)
        consistent(loc)
    }

}
