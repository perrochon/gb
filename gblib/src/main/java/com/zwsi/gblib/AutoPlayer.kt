package com.zwsi.gb.gblib

import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.universe

class AutoPlayer() {

    companion object {

        fun playBeetle() {

            GBLog.d("Programming Beetles in turn $universe.turn")
            // TODO Quality there must be an easier way...
            val r = universe.allRaces.values.find { it.idx == 2 }!!
            val now = universe.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                val p = r.getHome()
                GBController.universe.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1 until 10000) {
                code = {
                    val factory = r.getRaceShipsList().filter { it.idxtype == GBData.FACTORY }.firstOrNull()
                    if (factory != null) {
                        factory?.let { GBController.universe.makePod(it) }
                        GBLog.d("Ordered Pod")
                    }
                }
                GBScheduler.addInstructionAt(now + 1 + i, code)

            }
            code = {
                for (pod in r.getRaceShipsList().filter { (it.idxtype == GBData.POD) && (it.dest == null) }) {
                    pod?.let {
                        GBController.universe.flyShipLanded(
                            it,
                            universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
                        )
                    }
                }
                GBLog.d("Directed Pod")
            }
            //GBScheduler.addInstructionAlways(code)
            GBScheduler.addInstructionEvery(25, code)


        }

        fun playImpi() {

            GBLog.d("Programming Impi in turn $universe.turn")
            val r = universe.allRaces.values.find { it.idx == 1 }!!
            val now = universe.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                val p = r.getHome()
                GBController.universe.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.getRaceShipsList().find { it.idxtype == GBData.FACTORY }
                    factory?.let { GBController.universe.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                // TODO Fix: Getting all ships, not just this race...
                val cruiser = r.getRaceShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
// TODO Enable this again, don't fly to main races home star.
//                if (p.star != universe.allRaces[2].home.star) {
                    cruiser?.let { GBController.universe.flyShipOrbit(it, p) }
//                }
                GBLog.d("Directed Cruiser")
            }
            GBScheduler.addInstructionAlways(code)

        }

        fun playTortoise() {

            GBLog.d("Programming Tortoise in  turn  $universe.turn")
            val r = universe.allRaces.values.find { it.idx == 3 }!!
            val now = universe.turn + 1 // just in case we have a turn running....

            var code = {}

            code = {
                val p = r.getHome()
                GBController.universe.makeFactory(p, r)
                GBLog.d("Ordered Factory")
            }
            GBScheduler.addInstructionAt(now, code)

            for (i in 1..20) {
                code = {
                    val factory = r.getRaceShipsList().find { it.idxtype == GBData.FACTORY }
                    factory?.let { GBController.universe.makeCruiser(it) }
                    GBLog.d("Ordered Cruiser")
                }
                GBScheduler.addInstructionAt(now + 1 + i * 10, code)
            }

            code = {
                // TODO Fix: Getting all ships, not just this race...
                val cruiser = r.getRaceShipsList().find {
                    ((it.idxtype == GBData.CRUISER) && (it.loc.level == GBLocation.LANDED))
                }
                val p = universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
// TODO Enable this again, don't fly to main races home star.
//                if (p.star != universe.allRaces[2].home.star) {
                    cruiser?.let { GBController.universe.flyShipOrbit(it, p) }
//                }
                GBLog.d("Directed Cruiser")
            }
            GBScheduler.addInstructionAlways(code)
        }

    }
}