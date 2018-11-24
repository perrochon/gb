package com.zwsi.gblib

class GBController {

    companion object {

        val numberOfStars = 24
        val numberOfRaces = 4 // This should not be bigger than what we have in the data. Or smaller than 4 (tests will fail)

        // Small universe has some hard coded rules around how many stars and how many planets per systems
        // Do not change these values...
        var smallUniverse = false
            private set
        val numberOfStarsSmall = 5
        val numberOfRacesSmall = numberOfRaces

        // Big Universe just has a lot of stars, but is otherwise the same as regular sized
        var bigUniverse = false
            private set
        val numberOfStarsBig = 100
        val numberOfRacesBig = numberOfRaces

        private var currentUniverse: GBUniverse? = null

        val universe: GBUniverse
            get() {
                if (currentUniverse == null) {
                    if (smallUniverse) {
                        currentUniverse = GBUniverse(numberOfStarsSmall)
                    } else if (bigUniverse) {
                        currentUniverse = GBUniverse(numberOfStarsBig)
                    } else {
                        currentUniverse = GBUniverse(numberOfStars)
                    }
                }
                return currentUniverse!!
            }

        // This is hacky, but we'll have multiple "Universes" later, as a "universe is basically an ongoing game"
        // I fear this all needs to be refactored when we implement persistency, but by then we understand things better
        // Basically if you don't bother about MakeUniverse, you get a normal sized one.
        // If you specifically ask for big or small, you get that.
        // After that, if you want a normal instead, you have to use make Universe

        fun makeUniverse(): GBUniverse {
            GBDebug.l1("Making regular sized universe")
            smallUniverse = false
            bigUniverse = false
            currentUniverse = null
            return universe
        }

        fun makeSmallUniverse(): GBUniverse {
            GBDebug.l1("Making small universe")
            smallUniverse = true
            bigUniverse = false
            currentUniverse = null
            return universe
        }

        fun makeBigUniverse(): GBUniverse {
            GBDebug.l1("Making big universe")
            bigUniverse = true
            smallUniverse = false
            currentUniverse = null
            return universe
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
