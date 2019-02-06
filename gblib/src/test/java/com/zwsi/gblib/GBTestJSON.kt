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

        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        println("\n Testing Object")
        val inObject = GBPlanet(u.getNextGlobalId(),0, 0, u.star(0))
        println("  in: " + inObject)
        val jsonAdapter1 = moshi.adapter<GBPlanet>(GBPlanet::class.java).indent("    ")
        val json1 = jsonAdapter1.toJson(inObject)
        println(json1)
        val outObject = jsonAdapter1.fromJson(json1)
        println("  out:" + outObject)
        assert(inObject == outObject)
    }

    @Test
    fun JSONMap() {

        val u = GBController.makeUniverse()
        val moshi = Moshi.Builder().build()

        println("\nTesting GBSavedGame")
        val gameInfo1 = GBSavedGame(null, u.allStars, null)
        println("  GBSavedGame in: " + gameInfo1)
        val jsonAdapter3: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("    ")
        val json3 = jsonAdapter3.toJson(gameInfo1)
        println("  JSON string: " + json3)
        val gameInfo2 = jsonAdapter3.lenient().fromJson(json3)
        println("  GBLocation out:  " + gameInfo2)
        assert(gameInfo1 == gameInfo2)
    }


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

