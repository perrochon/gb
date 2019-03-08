// Copyright 2018-2019 Louis Perrochon. All rights reserved

// GBLibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.PlanetOrbit
import com.zwsi.gblib.GBData.Companion.starMaxOrbit
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GBShipTest {

    fun consistency(ship: GBShip) {

        GBLog.d("Looking at ship " + ship.uid + " with name " + ship.name)

        assertTrue(ship.name.length > 0)

        assertEquals(u, u)

        assertTrue(u.ships.containsValue(ship))
        assertEquals(ship, u.ships[ship.uid])

        assertTrue(u.race(ship.uidRace).raceUidShips.contains(ship.uid))
        assertTrue(u.race(ship.uidRace).raceShips.contains(ship)) // This fails because list is cached

        if (ship.health>0) {

            when (ship.loc.level) {
                LANDED -> {
                    assertTrue(u.planet(ship.loc.uidRef).landedShips.contains(ship))
                }
                ORBIT -> {
                    assertTrue(u.planet(ship.loc.uidRef).orbitShips.contains(ship))
                }
                SYSTEM -> {
                    assertTrue(u.star(ship.loc.uidRef).starUidShips.contains(ship.uid))
                }
                DEEPSPACE -> {
                    assertTrue(u.deepSpaceUidShips.contains(ship.uid))
                }
                else -> {
                    assert(false)
                }
            }
        }

        // Make sure this ship is only in one location
        var found = 0

        GBLog.d("Looking all over for ship: " + ship.uid)


        for (uid in u.deepSpaceUidShips) {  // TODO use "contains"
            if (uid == ship.uid) {
                found++
                GBLog.d("Found in deep space: ship: " + uid)
            }
        }
        for ((_, star) in u.stars) {
            for (sh in star.starShips) {
                if (sh.uid == ship.uid) {
                    found++
                    GBLog.d("Found in star: ship: " + sh.uid + " in star: " + star.uid)
                }
            }
        }
        for ((_, pl) in u.planets) {
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
//        for ((_, sh) in universe.deadShips) {
//            if (sh.uid == ship.uid) {
//                found++
//                GBLog.d("Found among the dead: ship: " + sh.uid)
//            }
//        }
        assertEquals(1, found)

        // Make sure ship is only in one race
        found = 0
        for ((_, race) in u.races) {
            for (uid in race.getRaceShipsUIDList()) {
                if (uid == ship.uid)
                    found++
            }
        }
        assertEquals(1, found)

    }

    // TODO use @After, but need to figure out access to Universe
    fun uniqueLocations() {

        for ((_, ship) in u.ships) {
            var found = 0
            for (uid in u.deepSpaceUidShips) {
                if (uid == ship.uid)
                    found++
            }
            for ((_, st) in u.stars) {
                for (sh in st.starShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for ((_, pl) in u.planets) {
                for (sh in pl.orbitShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for ((_, pl) in u.planets) {
                for (sh in pl.landedShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for ((_, sh) in u.deadShips) {
                if (sh.uid == ship.uid)
                    found++
            }
            assertEquals(1, found)
        }
    }

    @Test
    fun basic() {

        GBController.makeUniverse()

        GBLog.d("Testing " + u.ships.size + " ships")

        // Test all ships there are
        for ((_, ship) in u.ships) {
            consistency(ship)
        }

        // Just in case there aren't any, make a few more
        val s = u.star(0)
        val p: GBPlanet = s.starPlanets.first()
        val r: GBRace = u.races.toList().component1().second

        var sh = GBShip(u.getNextGlobalId(),1, r.uid, GBLocation(500f, 500f))
        sh.initializeShip()
        consistency(sh)

        sh = GBShip(u.getNextGlobalId(),0, r.uid, GBLocation(s, starMaxOrbit, 2f))
        sh.initializeShip()
        consistency(sh)

        sh = GBShip(u.getNextGlobalId(),1, r.uid, GBLocation(p, PlanetOrbit, 2f))
        sh.initializeShip()
        consistency(sh)

        sh = GBShip(u.getNextGlobalId(),1, r.uid, GBLocation(p, 1, 1))
        sh.initializeShip()
        consistency(sh)

        uniqueLocations()


    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        GBController.makeUniverse()


        val s0 = u.star(0)
        val s1 = u.star(1)
        val r0= u.race(0)

        val sh0 = GBShip(u.getNextGlobalId(),0, r0.uid, GBLocation(s0, 30f, PlanetOrbit))

        consistency(sh0)

        s1.starUidShips.add(sh0.uid)
        consistency(sh0)
        uniqueLocations()

    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoRacesFailsConsistency() {
        GBController.makeUniverse()

        val s0 = u.star(0)
        val r0= GBController.u.races.toList().component1().second
        val r1= GBController.u.races.toList().component2().second

        val sh0 = GBShip(u.getNextGlobalId(),1, r0.uid, GBLocation(s0, 30f, 1f))
        consistency(sh0)

        r1.raceUidShips.add(sh0.uid)

        consistency(sh0)
        uniqueLocations()
    }

    @Test
    fun moveCruiser() {
        GBController.makeUniverse()

        val s0 = u.star(0)
        val p0 = s0.starPlanets[0]
        val s1 = u.star(1)
        val p1 = s1.starPlanets[1]
        val r0 = GBController.u.races.toList().component1().second

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

        val sh0 = GBShip(u.getNextGlobalId(),2, r0.uid, locations.first())
        sh0.initializeShip()

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
        GBController.makeUniverse()

        val s0 = u.star(0)
        val p0 = s0.starPlanets[0]
        val p1 = s0.starPlanets[1]
        val r0 = u.races.toList().component1().second

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(u.getNextGlobalId(),1, r0.uid, loc01)
        sh.initializeShip()


        sh.dest = loc02

        var i = 0
        var lastLocation : GBLocation
        while (sh.loc != loc02) {

            lastLocation = sh.loc

            sh.doShip()

            val distance_moved = lastLocation.getLoc().distance(sh.loc.getLoc())
            assert(distance_moved < 5, {
                "Ship moved $distance_moved from ${lastLocation.getLocDesc()} to ${sh.loc.getLocDesc()} : " +
                        "${lastLocation.getLoc()} -> ${sh.loc.getLoc()}"
            })

            uniqueLocations()
            assertTrue(i < 40)
            i++
        }
    }


    @Test
    fun sendPodOtherSystem() {
        GBController.makeUniverse()

        val s0 = u.star(0)
        val s1 = u.star(1)
        val p0 = s0.starPlanets[0]
        val p1 = s1.starPlanets[1]
        val r0 = u.races.toList().component1().second

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(u.getNextGlobalId(),1, r0.uid, loc01)
        sh.initializeShip()


        sh.dest = loc02

        var i = 0
        var lastLocation: GBLocation
        while (sh.loc != loc02) {

            lastLocation = sh.loc

            sh.doShip()

            val distance_moved = lastLocation.getLoc().distance(sh.loc.getLoc())
            assert(distance_moved < 5, {
                "Ship moved $distance_moved from ${lastLocation.getLocDesc()} to ${sh.loc.getLocDesc()} : " +
                        "${lastLocation.getLoc()} -> ${sh.loc.getLoc()}"
            })

            uniqueLocations()
            assertTrue(i < 2000)
            i++
        }

    }

    @Test
    fun JSON() {
        GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val p0 = u.planet(0)
        val r0 = u.race(0)
        val loc01 = GBLocation(p0, 1, 1);

        val inObject = GBShip(u.getNextGlobalId(),1, r0.uid, loc01)

        val jsonAdapter1 = moshi.adapter<GBShip>(GBShip::class.java)
        val json1 = jsonAdapter1.toJson(inObject)
        val outObject = jsonAdapter1.fromJson(json1)
        assert(inObject == outObject)
    }

//    @Test
//    fun JSONMap() {
//        GBController.makeSmallUniverse()
//        val moshi = Moshi.Builder().build()
//
//        // Need to make ships
//        GBAutoPlayer.playBeetle()
//
//        for (i in 1..2) {
//            GBController.doUniverse()
//        }
//
//        val gameInfo1 = GBSavedGame("Shiplist Only", ships = u.ships)
//        //File("testoutput/GBSShipTestJSONMap.in.txt").writeText(gameInfo1.toString())
//
//        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
//        val json = jsonAdapter1.toJson(gameInfo1)
//        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)
//
//        //File("testoutput/GBSShipTestJSONMap.map.txt").writeText(u.ships.toString())
//        //File("testoutput/GBSShipTestJSONMap.json").writeText(json)
//        //File("testoutput/GBSShipTestJSONMap.out.txt").writeText(gameInfo2.toString())
//
//        assert(gameInfo1 == gameInfo2)
//    }
}