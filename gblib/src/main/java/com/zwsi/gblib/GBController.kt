package com.zwsi.gblib

import com.zwsi.gblib.AutoPlayer.Companion.playBeetle
import com.zwsi.gblib.AutoPlayer.Companion.playImpi
import com.zwsi.gblib.AutoPlayer.Companion.playTortoise
import kotlin.system.measureNanoTime

class GBController {

    companion object {

        val numberOfStars = 24
        val numberOfRaces =
            4 // This should not be bigger than what we have in the data. Or smaller than 4 (tests will fail)

        // Small universe has some hard coded rules around how many stars and how many planets per systems
        // Do not change these values...
        val numberOfStarsSmall = 5
        val numberOfRacesSmall = numberOfRaces

        // Big Universe just has a lot of stars, but is otherwise the same as regular sized
        val numberOfStarsBig = 100
        val numberOfRacesBig = numberOfRaces

        var elapsedTimeLastUpdate = 0L

        private var _u : GBUniverse? = null

        val u: GBUniverse
            get() {
                if (_u == null) {
                    _u = makeUniverse()
                }
                return _u?: throw AssertionError("Set to null by another thread")
            }

        fun makeUniverse(stars: Int = numberOfStars) : GBUniverse {
            _u  = GBUniverse(stars)
            _u!!.makeStarsAndPlanets()
            _u!!.makeRaces()
            GBLog.d("Universe made with $stars stars")
            return _u!!
        }

        fun makeSmallUniverse(): GBUniverse {
            return makeUniverse(numberOfStarsSmall)
        }

        fun makeBigUniverse(): GBUniverse {
            return makeUniverse(numberOfStarsBig)
        }

        fun doUniverse() {
            GBLog.i("Runing Game Turn ${u.turn}")
            elapsedTimeLastUpdate = measureNanoTime {
                u.doUniverse()
            }
        }

        // FIXME I think we can get rid of some of the syncs now. Commented out 2/5/2019
        //@Synchronized
        fun makeStuff() {
            playBeetle()
            playImpi()
            playTortoise()
        }

    }
}
