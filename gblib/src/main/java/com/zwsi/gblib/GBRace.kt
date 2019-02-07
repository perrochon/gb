// Copyright 2018-2019 Louis Perrochon. All rights reserved

// Race Logic.

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBRace(val id: Int, val idx: Int, val uid: Int, val uidHome: Int) {
    // id is a unique object ID. Not currently used anywhere FIXME DELETE id can probably be removed
    // idx is the number to go look up static race information in GBData.
    //      Not needed with dynamic race design or load from json

    // TODO val properties outside constructor are not serialized
    // Options: (1) Leave var and live with it (2) move it all into constructor (3) different/custom adaptor

    // Properties that don't really change after construction
    var name: String
    var birthrate: Int
    var explore: Int
    var absorption: Int
    var description: String
    var color: String

    // Properties that DO change after construction
    var population = 0 // (planetary) planetPopulation. Ships don't have planetPopulation

    internal var raceShipsUIDList: MutableList<Int> =
        Collections.synchronizedList(arrayListOf<Int>()) // Ships of this race

    val raceShipsList: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes.
        get() = Collections.synchronizedList(raceShipsUIDList.map { u.ship(it) })


    init {
        name = GBData.getRaceName(idx)
        birthrate = GBData.getRaceBirthrate(idx)
        explore = GBData.getRaceExplore(idx)
        absorption = GBData.getRaceAbsorption(idx)
        description = GBData.getRaceDescription(idx)
        color = GBData.getRaceColor(idx)
        GBLog.i("Created Race $name with birthrate $birthrate")
    }

    fun getHome(): GBPlanet {
        return u.planet(uidHome)
    }

    fun getRaceShipsUIDList(): List<Int> {
        return raceShipsUIDList.toList()
    }

    fun consoleDraw() {
        println("")
        println("    ====================")
        println("    $name Race")
        println("    ====================")
        println("    birthrate:  $birthrate")
        println("    explore:    $explore")
        println("    absorbtion: $absorption")
    }

}
