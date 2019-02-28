//// Copyright 2018-2019 Louis Perrochon. All rights reserved
//// To save Game State.
//// This saves much of what is in GBUniverse. Eventually, maybe GBUniverse should be persisted.
//
//package com.zwsi.gblib
//
//import com.squareup.moshi.JsonClass
//
//@JsonClass(generateAdapter = true)
//data class GBSavedGame(
//    val description: String = "Test Saved Game",
//    val turn: Int = -1,
//    val universeMaxX: Int = -1,
//    val universeMaxY: Int = -1,
//    val starMaxOrbit: Float = -1f,
//    val planetOrbit: Float = -1f,
//    val nextGlobalID : Int = -1,
//    val stars: MutableMap<Int, GBStar>? = null,
//    val planets: MutableMap<Int, GBPlanet>? = null,
//    val races: MutableMap<Int, GBRace>? = null,
//    val ships: MutableMap<Int, GBShip>? = null,
//    // deepSpace rebuilt on load
//    // dead ships not needed
//    val shots: MutableList<GBVector>? = null,
//    val news: MutableList<String>? = null
//
//    // orders not needed while we only save/restore at beginning of turn
//) {
//
//    // If you want to save everything, just pass in the Universe :-)
//    constructor(description: String, u: GBUniverse) : this(
//        description,
//        u.turn,
//        u.universeMaxX,
//        u.universeMaxY,
//        u.starMaxOrbit,
//        u.planetOrbit,
//        u.nextGlobalID,
//        u.stars,
//        u.planets,
//        u.races,
//        u.ships,
//        u.shots,
//        u.news
//    ) {
//    }
//}
