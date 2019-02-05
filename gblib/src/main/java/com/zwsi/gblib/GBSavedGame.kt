// Copyright 2018 Louis Perrochon. All rights reserved
// To Save Game State

package com.zwsi.gblib

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GBSavedGame(
    val raceList: Map<Int, GBRace>?,
    val starList: Map<Int, GBStar>?,
    val planetList: Map<Int, GBPlanet>?
) {

}
