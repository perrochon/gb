// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib

class GBTest {
    companion object {

        var universe: GBUniverse? = null
            private set
        private var gameTurns = 0

        fun makeUniverse() {
            GBDebug.l1("Making Universe")
            universe = GBUniverse(3,2)
            universe!!.consoleDraw()
        }

        fun doUniverse() {
            gameTurns++
            GBDebug.l1("Runing Game Turn $gameTurns")
            universe!!.doUniverse()
            universe!!.consoleDraw()
        }


        @JvmStatic
        fun main(args: Array<String>) {

            println("Welcome to GB Test")
            val tester = GBTest()
            GBTest.Companion.makeUniverse()
            universe!!.makeFactory(universe!!.allPlanets[0])
            universe!!.doUniverse()


//            for (i in 0..3 ) GBTest.Companion.doUniverse()

            println()
            println("$gameTurns game turns done.")
        }
    }
}