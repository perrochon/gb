package com.zwsi.gblib

class GBController {

    companion object {

        var universe: GBUniverse? = null
            private set
        private var gameTurns = 0
        fun makeUniverse() {
            GBDebug.l1("Making Universe")
            universe = GBUniverse(2)
            universe!!.consoleDraw()
        }

        fun doUniverse() {
            gameTurns++
            GBDebug.l1("Runing Game Turn $gameTurns")
            universe!!.doUniverse()
            universe!!.consoleDraw()
        }


//        @JvmStatic
//        fun main(args: Array<String>) {
//
//            println("Welcome to GB Test")
//            val tester = GBTest()
//            GBTest.Companion.makeUniverse()
//            for (i in 0..3 ) GBTest.Companion.doUniverse()
//            println()
//            println("$gameTurns game turns done.")
//
//
//        }
    }
}