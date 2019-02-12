// Copyright 2018-2019 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
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

        val universe = GBController.u

        GBLog.d("Looking at ship " + ship.uid + " with name " + ship.name)

        assertTrue(ship.name.length > 0)

        assertEquals(universe, universe)

        assertTrue(universe.allShips.containsValue(ship))
        assertEquals(ship, universe.allShips[ship.uid])

        assertTrue(universe.race(ship.uidRace).raceShipsUIDList.contains(ship.uid))
        assertTrue(universe.race(ship.uidRace).raceShipsList.contains(ship)) // This fails because list is cached

        if (ship.health>0) {

            when (ship.loc.level) {
                LANDED -> {
                    assertTrue(universe.planet(ship.loc.uidRef).landedShips.contains(ship))
                }
                ORBIT -> {
                    assertTrue(universe.planet(ship.loc.uidRef).orbitShips.contains(ship))
                }
                SYSTEM -> {
                    assertTrue(universe.star(ship.loc.uidRef).starUidShipSet.contains(ship.uid))
                }
                DEEPSPACE -> {
                    assertTrue(universe.deepSpaceUidShips.contains(ship.uid))
                }
                else -> {
                    assert(false)
                }
            }
        }

        // Make sure this ship is only in one location
        var found = 0

        GBLog.d("Looking all over for ship: " + ship.uid)


        for (uid in universe.deepSpaceUidShips) {  // TODO use "contains"
            if (uid == ship.uid) {
                found++
                GBLog.d("Found in deep space: ship: " + uid)
            }
        }
        for ((_, star) in universe.allStars) {
            for (sh in star.starShipList) {
                if (sh.uid == ship.uid) {
                    found++
                    GBLog.d("Found in star: ship: " + sh.uid + " in star: " + star.uid)
                }
            }
        }
        for ((_, pl) in universe.allPlanets) {
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
        for ((_, race) in universe.allRaces) {
            for (uid in race.getRaceShipsUIDList()) {
                if (uid == ship.uid)
                    found++
            }
        }
        assertEquals(1, found)

    }

    // TODO use @After, but need to figure out access to Universe
    fun uniqueLocations() {
        val un = GBController.u
        for ((_, ship) in un.allShips) {
            var found = 0
            for (uid in un.deepSpaceUidShips) {
                if (uid == ship.uid)
                    found++
            }
            for ((_, st) in un.allStars) {
                for (sh in st.starShipList) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for ((_, pl) in un.allPlanets) {
                for (sh in pl.orbitShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for ((_, pl) in un.allPlanets) {
                for (sh in pl.landedShips) {
                    if (sh.uid == ship.uid)
                        found++
                }
            }
            for ((_, sh) in un.deadShips) {
                if (sh.uid == ship.uid)
                    found++
            }
            assertEquals(1, found)
        }
    }

    @Test
    fun basic() {
        val u = GBController.makeUniverse()

        GBLog.d("Testing " + u.allShips.size + " ships")

        // Test all ships there are
        for ((_, ship) in u.allShips) {
            consistency(ship)
        }

        // Just in case there aren't any, make a few more
        val s = u.star(0)
        val p: GBPlanet = s.starPlanetsList.first()
        val r: GBRace = u.allRaces.toList().component1().second

        var sh = GBShip(u.getNextGlobalId(),1, r.uid, GBLocation(500f, 500f))
        consistency(sh)

        sh = GBShip(u.getNextGlobalId(),0, r.uid, GBLocation(s, SystemBoundary, 2f))
        consistency(sh)

        sh = GBShip(u.getNextGlobalId(),1, r.uid, GBLocation(p, PlanetaryOrbit, 2f))
        consistency(sh)

        sh = GBShip(u.getNextGlobalId(),1, r.uid, GBLocation(p, 1, 1))
        consistency(sh)

        uniqueLocations()


    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoStarsFailsConsistency() {
        val u = GBController.makeUniverse()


        val s0 = u.star(0)
        val s1 = u.star(1)
        val r0= u.race(0)

        val sh0 = GBShip(u.getNextGlobalId(),0, r0.uid, GBLocation(s0, 30f, PlanetaryOrbit))

        consistency(sh0)

        s1.starUidShipSet.add(sh0.uid)
        consistency(sh0)
        uniqueLocations()

    }

    @Test(expected = java.lang.AssertionError::class)
    fun shipInTwoRacesFailsConsistency() {
        val u = GBController.makeUniverse()

        val s0 = u.star(0)
        val r0= GBController.u.allRaces.toList().component1().second
        val r1= GBController.u.allRaces.toList().component2().second

        val sh0 = GBShip(u.getNextGlobalId(),1, r0.uid, GBLocation(s0, 30f, 1f))
        consistency(sh0)

        r1.raceShipsUIDList.add(sh0.uid)

        consistency(sh0)
        uniqueLocations()
    }

    @Test
    fun moveCruiser() {
        val u = GBController.makeUniverse()

        val s0 = u.star(0)
        val p0 = s0.starPlanetsList[0]
        val s1 = u.star(1)
        val p1 = s1.starPlanetsList[1]
        val r0 = GBController.u.allRaces.toList().component1().second

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
        val u = GBController.makeUniverse()

        val s0 = u.star(0)
        val p0 = s0.starPlanetsList[0]
        val p1 = s0.starPlanetsList[1]
        val r0 = u.allRaces.toList().component1().second

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(u.getNextGlobalId(),1, r0.uid, loc01)

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
            assertTrue(i < 30)
            i++
        }
    }


    @Test
    fun sendPodOtherSystem() {
        val u = GBController.makeUniverse()

        val s0 = u.star(0)
        val s1 = u.star(1)
        val p0 = s0.starPlanetsList[0]
        val p1 = s1.starPlanetsList[1]
        val r0 = u.allRaces.toList().component1().second

        val loc01 = GBLocation(p0, 1, 1);
        val loc02 = GBLocation(p1, 1, 2);

        val sh = GBShip(u.getNextGlobalId(),1, r0.uid, loc01)

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
        val u = GBController.makeUniverse()
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

    @Test
    fun JSONMap() {
        val u = GBController.makeSmallUniverse()
        val moshi = Moshi.Builder().build()

        // Need to make ships
        AutoPlayer.playBeetle()

        for (i in 1..2) {
            GBController.doUniverse()
        }

        val gameInfo1 = GBSavedGame("Shiplist Only", shipList = u.allShips)
        //File("testoutput/GBSShipTestJSONMap.in.txt").writeText(gameInfo1.toString())

        val jsonAdapter1: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json = jsonAdapter1.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter1.lenient().fromJson(json)

        //File("testoutput/GBSShipTestJSONMap.map.txt").writeText(u.allShips.toString())
        //File("testoutput/GBSShipTestJSONMap.json").writeText(json)
        //File("testoutput/GBSShipTestJSONMap.out.txt").writeText(gameInfo2.toString())

        assert(gameInfo1 == gameInfo2)
    }
}