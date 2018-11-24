// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib

class GBTest {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            println("Welcome to GB Test")
            GBController.makeUniverse()
            GBController.universe.consoleDraw()

            GBController.doUniverse()
            GBController.universe.consoleDraw()

            for (i in 0..3 ) GBController.doUniverse()
            GBController.universe.consoleDraw()

            println()
        }
    }
}