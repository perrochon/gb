// Copyright 2018-2019 Louis Perrochon. All rights reserved


package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test

import org.junit.Assert.*

class GBRaceTest {

    fun consistent(r: GBRace){
        GBController.u
        assertTrue(r.description.length > 0)
        assertTrue(u.races.containsValue(r))
        assertEquals(r, u.race(r.uid))
    }

    @Test
    fun basic() {
        GBController.makeUniverse()

        for ((_,race) in u.races) {
            consistent(race)
            assertEquals(race.raceShips.size,0 )
        }

        // TODO TEST add ships to race and check again

        assert(u.race(0) == u.race(0))
        assert(u.race(0) != u.race(1))

        // Testing Data Class Behavior
        val copy1 = u.race(1)
        assert(u.race(1) == copy1)
        assert(u.race(1) === copy1) // Pointing to the same class

        val copy2 = u.race(1).copy()
        assert(u.race(1) == copy2)
        assert(u.race(1) !== copy2) // Pointing to different class
    }

    @Test
    fun JSONRace() {
        GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        val race1 = GBRace( 0, 0, 0)
        val jsonAdapter = moshi.adapter<GBRace>(GBRace::class.java)
        val json = jsonAdapter.toJson(race1)
        val race2 = jsonAdapter.fromJson(json)

        assert(race1 == race2)
    }

//    @Test
//    fun JSONMap() {
//        GBController.makeUniverse()
//
//        val moshi = Moshi.Builder().build()
//
//        val gameInfo1 = GBSavedGame("Racelist only", races = u.races)
//        //File("testoutput/GBRaceTestJSONMap.in.txt").writeText(gameInfo1.toString())
//
//        val jsonAdapter2: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
//        val json = jsonAdapter2.toJson(gameInfo1)
//        val gameInfo2 = jsonAdapter2.lenient().fromJson(json)
//
//        //File("testoutput/GBRaceTestJSONMap.json").writeText(json)
//        //File("testoutput/GBRaceTestJSONMap.out.txt").writeText(gameInfo2.toString())
//
//        assert(gameInfo1 == gameInfo2)
//    }
}
