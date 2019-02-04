// Copyright 2018 Louis Perrochon. All rights reserved
// GBTest tests saving to and restoring from JSON

package com.zwsi.gblib

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GBSavedGame(
    val location: GBLocation,
    val locationList: List<GBLocation>,
    val race: GBRace
) {

}
