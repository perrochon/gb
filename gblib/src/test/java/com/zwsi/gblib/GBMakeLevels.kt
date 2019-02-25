// Copyright 2018-2019 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import org.junit.Test
import java.io.File


// This is not really a test, this creates (temporary) levels for the game
// They need to manually be copied into the resources directory
//
class GBMakeLevels {


    //@Test
    fun makeLevels() {
        GBController.makeUniverse()
        val turns = 100
        var json: String = ""

        for (i in 1..turns) {
            json = GBController.doUniverse()
        }
        File("levels/level1.json").writeText(json)

        for (i in 1..turns) {
            json = GBController.doUniverse()
        }
        File("levels/level2.json").writeText(json)

        for (i in 1..turns) {
            json = GBController.doUniverse()
        }
        File("levels/level3.json").writeText(json)

    }

}

