// Copyright 2018 Louis Perrochon. All rights reserved

// Tests GBLocation
//

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.zwsi.gblib.GBController.Companion.u
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class GBLocationTest {

    fun consistent(l: GBLocation) {

        when (l.level) {
            GBLocation.LANDED -> {
                assertEquals(GBLocation.LANDED, l.level)
                assertEquals(-1f, l.x)
                assertEquals(-1f, l.y)
                assertNotEquals(-1, l.uidRef)
                assertEquals(-1f, l.t)
                assertEquals(-1f, l.r)
                assertEquals(l.getLLoc().sx, l.sx)
                assertEquals(l.getLLoc().sy, l.sy)
                assertNotEquals(null, u.allPlanets.getOrNull(l.uidRef)) //
                // Do some checks on the LocDesc
            }
            GBLocation.ORBIT -> {
                assertEquals(GBLocation.ORBIT, l.level)
                assertEquals(l.getOLocC().x, l.x)
                assertEquals(l.getOLocC().y, l.y)
                assertNotEquals(-1, l.uidRef)
                assertEquals(l.getOLocP().r, l.r)
                assertEquals(l.getOLocP().t, l.t)
                assertEquals(-1, l.sx)
                assertEquals(-1, l.sx)
                assertNotEquals(null, u.allPlanets.getOrNull(l.uidRef)) //
                // Do some checks on the LocDesc
            }
            GBLocation.SYSTEM -> {
                assertEquals(GBLocation.SYSTEM, l.level)
                assertEquals(l.getLoc().x, u.allStars[l.uidRef].loc.x + l.x)
                assertEquals(l.getLoc().y, u.allStars[l.uidRef].loc.y + l.y)
                assertNotEquals(-1, l.uidRef)
                assertEquals(l.getSLocP().r, l.r)
                assertEquals(l.getSLocP().t, l.t)
                assertEquals(-1, l.sx)
                assertEquals(-1, l.sx)
                assertNotEquals(null, u.allStars.getOrNull(l.uidRef)) //
                assertEquals(l.x, l.r * cos(l.t))
                assertEquals(l.y, l.r * sin(l.t))

                // Do some checks on the LocDesc
            }
            GBLocation.DEEPSPACE -> {
                assertEquals(GBLocation.DEEPSPACE, l.level)
                assertEquals(l.getLoc().x, l.x)
                assertEquals(l.getLoc().y, l.y)
                assertEquals(-1, l.uidRef)
                assertEquals(-1f, l.r)
                assertEquals(-1f, l.t)
                assertEquals(-1, l.sx)
                assertEquals(-1, l.sx)
                assertEquals("Deep Space", l.getLocDesc())
            }
            else -> {
                assertTrue("Broken Location: $l", false)
            }
        }
    }

    @Test
    fun orbit() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[1]
        val loc = GBLocation(p, 1f, 25f)
        assertEquals(1f, loc.r)
        assertEquals(25f, loc.t)
        consistent(loc)
    }

    @Test(expected = AssertionError::class)
    fun orbitTooFarOut() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[1]
        val loc = GBLocation(p, 3f, 25f)
    }

    @Test
    fun landed() {
        val universe = GBController.makeUniverse()
        val p = universe.allPlanets[2]
        val loc = GBLocation(p, 1, 2)
        assertEquals(1, loc.sx)
        assertEquals(2, loc.sy)
        consistent(loc)
    }

    @Test
    fun system() {
        val universe = GBController.makeUniverse()
        val s = universe.allStars[3]
        val loc = GBLocation(s, 2f, 2f)
        assertEquals(2f, loc.t)
        consistent(loc)
    }

    @Test(expected = AssertionError::class)
    fun systemTooFarOut() {
        val universe = GBController.makeUniverse()
        val s = universe.allStars[3]
        val loc = GBLocation(s, 200f, 2f)
    }

    @Test
    fun deepSpace() {
        GBController.makeUniverse()
        val loc = GBLocation(500f, 400f)
        assertEquals(500f, loc.getLoc().x)
        assertEquals(400f, loc.getLoc().y)
        consistent(loc)
    }

    @Test
    fun gbxyDistance() {
        val a = GBxy(0f, 0f)
        val b = GBxy(0f, 3f)
        val c = GBxy(4f, 3f)
        val d = GBxy(-1f, -1f)
        val e = GBxy(2f, -1f)
        val f = GBxy(-1f, -5f)
        assertEquals(3f, a.distance(b))
        assertEquals(4f, b.distance(c))
        assertEquals(5f, c.distance(a))
        assertEquals(3f, b.distance(a))
        assertEquals(4f, c.distance(b))
        assertEquals(5f, a.distance(c))
        assertEquals(3f, d.distance(e))
        assertEquals(4f, f.distance(d))
        assertEquals(5f, e.distance(f))
        assertEquals(3f, e.distance(d))
        assertEquals(4f, d.distance(f))
        assertEquals(5f, f.distance(e))
    }

    @Test
    fun planetMoveMath() { // TODO Refactor so this test can be done without a universe
        val universe = GBController.makeUniverse()
        val s = universe.allStars[0]
        var loc = GBLocation(s, 10f, 0f)
        var xy = loc.getSLocC()
        var rt = loc.getSLocP()
        assertEquals(10f, xy.x)
        assertEquals(0f, xy.y)
        assertEquals(10f, rt.r)
        loc = GBLocation(s, 10f, PI.toFloat())
        xy = loc.getSLocC()
        rt = loc.getSLocP()
        assertEquals(-10f, xy.x)
        assertEquals(0f, (xy.y * 1000).roundToInt() / 1000f)
        assertEquals(10f, rt.r)
    }

    @Test
    fun towards() {
        towards_helper(0f, 0f, 3f, 4f)
        towards_helper(4f, 3f, 0f, 0f)
        towards_helper(0f, 0f, -3f, -4f)
        towards_helper(-4f, -3f, 0f, 0f)
        towards_helper(3f, 5f, -3f, -4f)
        towards_helper(-4f, 3f, 3f, 7f)
    }

    @Test
    fun towardsItself() {
        towards_helper(0f, 0f, 0f, 0f)
    }

    fun towards_helper(x1: Float, y1: Float, x2: Float, y2: Float) {
        val origin = GBxy(x1, y1)
        val destination = GBxy(x2, y2)
        val distance = origin.distance(destination)
        assertEquals(GBxy(x2, y2), origin.towards(destination, distance * 2)) // we are faster, so will arrive
        assertEquals(
            GBxy(x2, y2),
            origin.towards(destination, distance * 1.00001f)
        ) // we are faster, so will arrive
        assertEquals(GBxy(x2, y2), origin.towards(destination, distance * 1f)) // we are faster, so will arrive
        assertEquals(
            GBxy(x1 + (x2 - x1) / 2f, y1 + (y2 - y1) / 2),
            origin.towards(destination, distance * 0.5f)
        ) // we are faster, so will arrive
        assertEquals(GBxy(x1, y1), origin.towards(destination, 0f))
    }

    @Test
    fun JSONRace() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val loc1 = GBLocation(100f, 100f)
        val jsonAdapter1 = moshi.adapter<GBLocation>(GBLocation::class.java)
        val json1 = jsonAdapter1.toJson(loc1)
        val loc2 = jsonAdapter1.fromJson(json1)
        assert(loc1 == loc2)
    }

    @Test
    fun JSONList() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val list1: MutableList<GBLocation> = Collections.synchronizedList(arrayListOf<GBLocation>())
        list1.add(GBLocation(100f, 100f))
        list1.add(GBLocation(1000f, 1000f))
        list1.add(GBLocation(u.allPlanets[1], 1, 1))
        list1.add(GBLocation(u.allPlanets[1], 1f, 1f))
        list1.add(GBLocation(u.allStars[1], 10f, 1f))

        val locListType = Types.newParameterizedType(List::class.java, GBLocation::class.java)
        val jsonAdapter2: JsonAdapter<List<GBLocation>> = moshi.adapter(locListType)
        val json2 = jsonAdapter2.toJson(list1)
        val list2 = jsonAdapter2.fromJson(json2)
        assert(list1 == list2)
    }

}
