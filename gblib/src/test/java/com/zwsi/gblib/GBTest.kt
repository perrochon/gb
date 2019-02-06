// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib

import com.zwsi.gblib.AutoPlayer.Companion.playBeetle
import com.zwsi.gblib.AutoPlayer.Companion.playImpi
import com.zwsi.gblib.AutoPlayer.Companion.playTortoise
import com.zwsi.gblib.GBController.Companion.u
import kotlin.system.measureNanoTime

class GBTest {

    // FIXME exclude using categories.
    //@Test
    fun runUniverse() {

        println("Welcome to GB Test")
        GBController.makeUniverse()
        playBeetle()
        playImpi()
        playTortoise()

        var elapsed = 0L

        for (i in 1..11000) {

            elapsed = measureNanoTime {
                GBController.doUniverse()
            }

            println("Turn: $i ${elapsed / 1000}ms Ships: ${u.getAllShipsList().size} Dead: ${u.getDeadShipsList().size}")

//                print("Time for do: $elapsed ")
//                for (j in 1..elapsed/10000 ) {
//                    print (" ")
//                }
//                print ("*\n")
        }
    }
}
