package com.zwsi.gb.gblib

import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.universe

class AutoPlayer() {

    companion object {

        fun playBeetle() {

            GBLog.d("Programming Beetles in turn $universe.turn")
            // TODO Quality there must be an easier way...
            var r = universe.allRaces.values.find { it.idx == 2 }!!
            var now = universe.turn + 1 // just in case we have a turn running....


            var code = {}

            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                GBController.universe.makeFactory(p, r)
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1 until 10000) {
                code = {
                    val factory = r.raceShips.filter { it.idxtype == GBData.FACTORY }.firstOrNull()
                    if (factory != null) {
                        GBLog.d("Ordered Pod")
                        factory?.let { GBController.universe.makePod(it) }
                    }
                }
                GBScheduler.addInstructionAt(now + 1 + i, code)

            }
            code = {
                GBLog.d("Directed Pod")
                for (pod in r.raceShips.filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                    pod?.let {
                        GBController.universe.flyShipLanded(
                            it,
                            universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
                        )
                    }
                }
            }
            //GBScheduler.addInstructionAlways(code)
            GBScheduler.addInstructionEvery(25, code)


        }

        fun playImpi() {

            GBLog.d("Programming Impi in turn $universe.turn")
            var r = universe.allRaces.values.find { it.idx == 1 }!!

            var now = universe.turn + 1 // just in case we have a turn running....


            var code = {}

            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                GBController.universe.makeFactory(p, r)
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.raceShips.find { it.idxtype == GBData.FACTORY }
                    GBLog.d("Ordered Cruiser")
                    factory?.let { GBController.universe.makeCruiser(it) }
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                GBLog.d("Directed Cruiser")
                // Getting all ships, not just alive ships, so even "dead" pods will start moving again. Ok for God to do.
                val cruiser = GBController.universe.getAllShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
// TODO Enable this again, don't fly to main races home star.
//                if (p.star != universe.allRaces[2].home.star) {
//                    cruiser?.let { GBController.universe.flyShipOrbit(it, p) }
//                }
            }
            GBScheduler.addInstructionAlways(code)

        }

        fun playTortoise() {

            GBLog.d("Programming Tortoise in  turn  $universe.turn")
            var r = universe.allRaces.values.find { it.idx == 3 }!!


            var now = universe.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                GBLog.d("Ordered Factory")
                var p = r.home
                GBController.universe.makeFactory(p, r)
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.raceShips.find { it.idxtype == GBData.FACTORY }
                    GBLog.d("Ordered Cruiser")
                    factory?.let { GBController.universe.makeCruiser(it) }
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                GBLog.d("Directed Cruiser")
                // Getting all ships, not just alive ships, so even "dead" pods will start moving again. Ok for God to do.
                val cruiser = GBController.universe.getAllShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
// TODO Enable this again, don't fly to main races home star.
//                if (p.star != universe.allRaces[2].home.star) {
//                    cruiser?.let { GBController.universe.flyShipOrbit(it, p) }
//                }
            }
            GBScheduler.addInstructionAlways(code)
        }

    }
}