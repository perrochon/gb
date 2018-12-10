// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib

import com.zwsi.gb.gblib.AutoPlayer.Companion.playBeetle
import kotlin.system.measureNanoTime

class GBTest {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            println("Welcome to GB Test")
            GBController.makeUniverse()
            playBeetle()

            var elapsed = 0L

            for (i in 1..1000000) {

                elapsed = measureNanoTime {
                    GBController.doUniverse()
                }

                print("Time for do: $elapsed ")
                for (j in 1..elapsed/10000 ) {
                    print (" ")
                }
                print ("*\n")
            }
        }

    }
}
