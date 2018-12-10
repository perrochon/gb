package com.zwsi.gb.gblib

import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.universe

class AutoPlayer() {

    companion object {

        fun playBeetle() {

            GBLog.d("Programming Beetles in turn $universe.turn")
            var r = universe.allRaces[2]
            var now = universe.turn + 1 // just in case we have a turn running....


            var code = {}

            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                GBController.universe.makeFactory(p, r)
            }
            GBSchedulier.addInstruction(now, code)

            for (i in 0 until 10000) {
                code = {
                    val factory = r.raceShips.find { it.idxtype == GBData.FACTORY }
                    GBLog.d("Ordered Pod")
                    factory?.let { GBController.universe.makePod(it) }
                }
                GBSchedulier.addInstruction(now + 1 + i, code)

            }
            code = {
                GBLog.d("Directed Pod")
                // Getting all pods, not just alive pods, so even "dead" pods will start moving again. Ok for God to do.
                val pod = r.raceShips.find { (it.idxtype == GBData.POD) && (it.dest == null) }
                pod?.let {
                    GBController.universe.flyShipLanded(
                        it,
                        universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
                    )
                }
            }
            GBSchedulier.addInstruction(-1, code)
        }

        fun playImpi() {

            GBLog.d("Programming Impi in turn $universe.turn")
            var r = universe.allRaces[1]

            var now = universe.turn + 1 // just in case we have a turn running....


            var code = {}

            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                GBController.universe.makeFactory(p, r)
            }
            GBSchedulier.addInstruction(now, code)

            for (i in 0..30) {
                code = {
                    val factory = r.raceShips.find { it.idxtype == GBData.FACTORY }
                    GBLog.d("Ordered Cruiser")
                    factory?.let { GBController.universe.makeCruiser(it) }
                }
                GBSchedulier.addInstruction(now + 1 + i*10, code)
            }

            code = {
                GBLog.d("Directed Cruiser")
                // Getting all ships, not just alive ships, so even "dead" pods will start moving again. Ok for God to do.
                val cruiser = GBController.universe.getAllShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
                if (p.star != universe.allRaces[2].home.star) {
                    cruiser?.let { GBController.universe.flyShipOrbit(it, p) }
                }
            }
            GBSchedulier.addInstruction(-1, code)

        }

        fun playTortoise() {

            GBLog.d("Programming Tortoise in  turn  $universe.turn")
            var r = universe.allRaces[3]

            var now = universe.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                GBController.universe.makeFactory(p, r)
            }
            GBSchedulier.addInstruction(now, code)

            for (i in 0..30) {
                code = {
                    val factory = r.raceShips.find { it.idxtype == GBData.FACTORY }
                    GBLog.d("Ordered Cruiser")
                    factory?.let { GBController.universe.makeCruiser(it) }
                }
                GBSchedulier.addInstruction(now + 1 + i*10, code)
            }

            code = {
                GBLog.d("Directed Cruiser")
                // Getting all ships, not just alive ships, so even "dead" pods will start moving again. Ok for God to do.
                val cruiser = GBController.universe.getAllShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
                if (p.star != universe.allRaces[2].home.star) {
                    cruiser?.let { GBController.universe.flyShipOrbit(it, p) }
                }
            }
            GBSchedulier.addInstruction(-1, code)
        }

    }
}