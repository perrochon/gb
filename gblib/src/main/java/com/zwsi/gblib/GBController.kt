package com.zwsi.gblib

class GBController {

    companion object {

        var numberOfStars = 24
            private set
        var numberOfRaces = 3
            private set
        var smallUniverse = false
            private set
        var numberOfStarsSmall = 3
            private set
        var numberOfRacesSmall = 3
            private set

        private lateinit var currentUniverse: GBUniverse
            private set

        private lateinit var currentSmallUniverse: GBUniverse
            private set

        val universe: GBUniverse
            get() {
                if (smallUniverse) {
                    return currentSmallUniverse
                } else {
                    return currentUniverse
                }
            }

        // This is hacky, but we'll have multiple "Universes" later, as a "universe is basically an ongoing game"
        // I fear this all needs to be refactored when we implement persistency, but by then we understand things better
        fun setUniverseSmall() {
            smallUniverse = true
        }

        fun setUniverseBig() {
            smallUniverse = false
        }

        fun makeUniverse() {
            GBDebug.l1("Making Universe")
            universe
        }

        // TODO move game turns to GBUniverse as it should be there. Turns are per universe
        private var gameTurns = 0

        fun doUniverse() {
            gameTurns++
            GBDebug.l1("Runing Game Turn $gameTurns")
            universe.doUniverse()
        }

    }
}
