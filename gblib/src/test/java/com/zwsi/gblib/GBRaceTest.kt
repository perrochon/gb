// Copyright 2018-2019 Louis Perrochon. All rights reserved


package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Test

import org.junit.Assert.*

class GBRaceTest {

    fun consistent(r: GBRace){
        val universe = GBController.u
        assertTrue(r.description.length > 0)
        assertTrue(universe.allRaces.containsValue(r))
        assertEquals(r, universe.race(r.uid))
    }

    @Test
    fun basic() {
        val universe = GBController.makeUniverse()

        for ((_,race) in universe.allRaces) {
            consistent(race)
            assertEquals(race.raceShipsList.size,0 )
        }

        // TODO TEST add ships to race and check again

        assert(universe.race(0) == universe.race(0))
        assert(universe.race(0) != universe.race(1))

        // Testing Data Class Behavior
        val copy1 = universe.race(1)
        assert(universe.race(1) == copy1)
        assert(universe.race(1) === copy1) // Pointing to the same class

        val copy2 = universe.race(1).copy()
        assert(universe.race(1) == copy2)
        assert(universe.race(1) !== copy2) // Pointing to different class
    }

    @Test
    fun JSONRace() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val race1 = GBRace(u.getNextGlobalId(), 0, 0, 0)
        val jsonAdapter = moshi.adapter<GBRace>(GBRace::class.java)
        val json = jsonAdapter.toJson(race1)
        val race2 = jsonAdapter.fromJson(json)
        assert(race1 == race2)
    }

    @Test
    fun JSONMap() {
        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val gameInfo1 = GBSavedGame("Racelist only", raceList = u.allRaces)
        val jsonAdapter2: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json3 = jsonAdapter2.toJson(gameInfo1)
        val gameInfo2 = jsonAdapter2.lenient().fromJson(json3)
        assert(gameInfo1 == gameInfo2)
    }
}
