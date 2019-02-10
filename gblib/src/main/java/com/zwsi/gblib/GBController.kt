package com.zwsi.gblib

import com.zwsi.gblib.AutoPlayer.Companion.playBeetle
import com.zwsi.gblib.AutoPlayer.Companion.playImpi
import com.zwsi.gblib.AutoPlayer.Companion.playTortoise
import java.util.concurrent.locks.ReentrantLock
import kotlin.system.measureNanoTime

class GBController {

    companion object {

        // gblib is not thread safe. lock is used to synchronize access.
        // Currently only locking for access to collections (e.g. for copying to ViewModel) which throw exceptions
        // Not locking for reading stars, ships, planets and such. Stale information is ok to read.
        // TODO QUALITY Review all the locking
        val lock = ReentrantLock()

        val numberOfStars = 24
        val numberOfRaces = 4   // <= what we have in GBData. >= 4 or tests will fail

        // Small universe has some hard coded rules around how many stars and how many planets per systems
        // Do not change these values...
        val numberOfStarsSmall = 5

        // Big Universe just has a lot of stars, but is otherwise the same as regular sized
        val numberOfStarsBig = 100

        var elapsedTimeLastUpdate = 0L

        private var _u: GBUniverse? = null

        val u: GBUniverse
            get() {
                if (_u == null) {
                    _u = makeUniverse()
                }
                return _u ?: throw AssertionError("Set to null by another thread")
            }

        fun makeUniverse(stars: Int = numberOfStars): GBUniverse {
            _u = GBUniverse(stars)
            _u!!.makeStarsAndPlanets()
            _u!!.makeRaces()
            GBLog.d("Universe made with $stars stars")
            GBScheduler.scheduledActions.clear()
            return _u!!
        }

        fun makeSmallUniverse(): GBUniverse {
            return makeUniverse(numberOfStarsSmall)
        }

        fun makeBigUniverse(): GBUniverse {
            return makeUniverse(numberOfStarsBig)
        }

        fun doUniverse() {
            GBLog.i("Runing Game Turn ${_u!!.turn}")
            lock.lock(); // lock for the game turn
            try {
                elapsedTimeLastUpdate = measureNanoTime {
                    _u!!.doUniverse()
                }
            } finally {
                lock.unlock()
            }
        }


        fun makeStuff() {
            //playBeetle()
            //playImpi()
            //playTortoise()
        }

    }
}
