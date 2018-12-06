package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import org.junit.Test

class SchedulerTest {

    @Test
    fun lambdaTest (): Unit {
        var universe = GBController.makeSmallUniverse()
        universe.makeStuff()
    }
}


/*
       GBLog.d("Making Stuff")

        doUniverse()
        val factory = allShips[0]
        var pod : GBShip
        if (factory.idxtype == FACTORY) {

            for (pt in p.star.starPlanets) {
                if (p != pt) {
                    makePod(factory)
                    doUniverse()
                    pod = allShips[allShips.size-1]
                    flyShipLanded(pod, pt)
                }
            }

            makePod(factory)
            doUniverse()
            pod = allShips[allShips.size-1]
            flyShipLanded(pod, allStars[1].starPlanets[0])

            makePod(factory)
            doUniverse()
            pod = allShips[allShips.size-1]
            flyShipLanded(pod, allStars[0].starPlanets[1])

            for (i in 1..5)
                doUniverse()
            flyShipLanded(pod, allStars[0].starPlanets[1])
            for (i in 1..5)
                doUniverse()
            flyShipLanded(pod, allStars[4].starPlanets[0])
            for (i in 1..4)
                doUniverse()

        }
 */