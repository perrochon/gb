// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest tests saving to and restoring from JSON

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.zwsi.gblib.GBController.Companion.u
import org.junit.Test
import java.util.*

class GBTestJSON {

    @Test
    fun JSON() {

        // TODO Quality: Move these tests into the Unit Tests of each class.

        println("Welcome to GB JSON Test")
        GBController.makeUniverse()

        val moshi = Moshi.Builder().build()

        println("Testing GBLocation")
        val loc1 = GBLocation(100f, 100f)
        println("  GBLocation in: " + loc1)
        val jsonAdapter1 = moshi.adapter<GBLocation>(GBLocation::class.java)
        val json1 = jsonAdapter1.toJson(loc1)
        println("  JSON string: " + json1)
        val loc2 = jsonAdapter1.fromJson(json1)
        println("  GBLocation out:  " + loc2)
        assert(loc1 == loc2)

        println("\nTesting List")
        val list1: MutableList<GBLocation> = Collections.synchronizedList(arrayListOf<GBLocation>())
        list1.add(GBLocation(100f, 100f))
        list1.add(GBLocation(1000f, 1000f))
        list1.add(GBLocation(u.allPlanets[1], 1, 1))
        list1.add(GBLocation(u.allPlanets[1], 1f, 1f))
        list1.add(GBLocation(u.allStars[1], 10f, 1f))
        println("  List<GBLocation> in: " + list1)

        val locListType = Types.newParameterizedType(List::class.java, GBLocation::class.java)
        val jsonAdapter2: JsonAdapter<List<GBLocation>> = moshi.adapter(locListType)
        val json2 = jsonAdapter2.toJson(list1)
        println("  JSON string: " + json2)
        val list2 = jsonAdapter2.fromJson(json2)
        println("  List<GBLocation> out:  " + list2)
        assert(list1 == list2)

        println("\n Testing GBRace")
        val race1 = GBRace(0, 0, u.allPlanets[0].uid)
        println(race1)
        val jsonAdapter4 = moshi.adapter<GBRace>(GBRace::class.java)
        val json4 = jsonAdapter4.toJson(race1)
        println(json4)
        val race2 = jsonAdapter4.fromJson(json4)
        println(race2)
        assert(race1 == race2)

        println("\nTesting Location, List, Race combined")
        val gameInfo1 = GBSavedGame(loc1, list1, race1)
        println("  GBSavedGame in: " + gameInfo1)
        val jsonAdapter3: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java)
        val json3 = jsonAdapter3.toJson(gameInfo1)
        println("  JSON string: " + json3)
        val gameInfo2 = jsonAdapter3.lenient().fromJson(json3)
        println("  GBLocation out:  " + gameInfo2)
        assert(gameInfo1 == gameInfo2)



//            println("GBShip")
//            val ship1 = GBShip(0, universe.allRaces[0], loc1)
//            println(ship1)
//            val jsonAdapter4 = moshi.adapter<GBShip>(GBShip::class.java)
//            val json4 = jsonAdapter4.toJson(ship1)
//            println(json4)
//            val ship2 = jsonAdapter4.fromJson(json4)
//            println(ship2)
//            assert(ship1== ship2)


    }

}

