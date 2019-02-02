// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest tests saving to and restoring from JSON

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.zwsi.gblib.GBController.Companion.universe
import java.util.*


class GBTestJSON {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

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
            val list1 : MutableList<GBLocation> = Collections.synchronizedList(arrayListOf<GBLocation>())
            list1.add(GBLocation(100f, 100f))
            list1.add(GBLocation(1000f, 1000f))
            list1.add(GBLocation(universe.allPlanets[1], 1,1))
            list1.add(GBLocation(universe.allPlanets[1], 1f,1f))
            list1.add(GBLocation(universe.allStars[1], 10f,1f))
            println("  List<GBLocation> in: " + list1)

            val locListType = Types.newParameterizedType(List::class.java, GBLocation::class.java)
            val jsonAdapter2: JsonAdapter<List<GBLocation>> = moshi.adapter(locListType)
            val json2 = jsonAdapter2.toJson(list1)
            println("  JSON string: " + json2)
            val list2 = jsonAdapter2.fromJson(json2)
            println("  List<GBLocation> out:  " + list2)
            assert(list1 == list2)

//            println("\nTesting Location and List combined")
//            val combine = json1 + json2
//            println("  JSON string: " + combine)
//            val loc22 = jsonAdapter1.lenient().fromJson(combine)
//            println("  GBLocation out:  " + loc22)
//            assert(loc1 == loc22)
//
//            val list22 = jsonAdapter2.fromJson(combine)
//            println("  List<GBLocation> out:  " + list22)
//            assert(list1 == list22)


//            println("GBShip")
//            val ship1 = GBShip(0, universe.allRaces[0], loc1)
//            println(ship1)
//            val jsonAdapter3 = moshi.adapter<GBShip>(GBShip::class.java)
//            json = jsonAdapter3.toJson(ship1)
//            println(json)
//            val ship2 = jsonAdapter3.fromJson(json)
//            println(ship2)
//            assert(ship1== ship2)


        }

    }
}
