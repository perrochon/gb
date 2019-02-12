// Copyright 2018-2019 Louis Perrochon. All rights reserved
// To Save Game State.
// This saves much of what is in GBUniverse. Eventually, maybe GBUniverse should be persisted.

package com.zwsi.gblib

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GBSavedGame(
    val description: String = "Test Saved Game",
    val turn: Int = -1,
    val universeMaxX: Int = -1,
    val universeMaxY: Int = -1,
    val systemBoundary: Float = -1f,
    val planetaryOrbit: Float = -1f,
    val starList: MutableMap<Int, GBStar>? = null,
    val planetList: MutableMap<Int, GBPlanet>? = null,
    val raceList: MutableMap<Int, GBRace>? = null,
    val shipList: MutableMap<Int, GBShip>? = null,
    val shots: MutableList<GBVector>? = null,
    val news: MutableList<String>? = null
) {

    // If you want to save everything, just pass in the Universe :-)
    constructor(description: String, u: GBUniverse) : this(
        description,
        u.turn,
        u.universeMaxX,
        u.universeMaxY,
        u.systemBoundary,
        u.planetaryOrbit,
        u.allStars,
        u.allPlanets,
        u.allRaces,
        u.allShips,
        u.allShots,
        u.news
    ) {
    }
}
