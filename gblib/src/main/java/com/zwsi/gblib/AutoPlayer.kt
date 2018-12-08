package com.zwsi.gb.gblib

import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.universe

class AutoPlayer() {

    companion object {

        fun makeStuff1() {

            GBLog.d("Making Stuff in turn $universe.turn")

            var now = universe.turn + 1 // just in case we have a turn running....

            var code = {}

            for (r in universe.allRaces) {
                code = {
                    GBLog.d("Ordered Factory")
                    var p = r.home
                    GBController.universe.makeFactory(p, r)
                }
                universe.scheduledActions.add(GBUniverse.GBInstruction(now, code))
            }

            for (i in 0 until 1000) {
                code = {
                    val factory = universe.allRaces[2].raceShips.find { it.idxtype == GBData.FACTORY }
                    GBLog.d("Ordered Pod")
                    factory?.let { GBController.universe.makePod(it) }
                }
                universe.scheduledActions.add(GBUniverse.GBInstruction(now + 1 + i, code))

            }
            code = {
                GBLog.d("Directed Pod")
                // Getting all pods, not just alive pods, so even "dead" pods will start moving again. Ok for God to do.
                val pod = universe.allRaces[2].raceShips.find { (it.idxtype == GBData.POD) && (it.dest == null) }
                pod?.let {
                    GBController.universe.flyShipLanded(
                        it,
                        universe.allPlanets[GBData.rand.nextInt(universe.allPlanets.size)]
                    )
                }
            }
            universe.scheduledActions.add(GBUniverse.GBInstruction(-1, code))

            for (j in arrayOf(0, 1, 3)) {
                for (i in 0..20) {
                    code = {
                        val factory = universe.allRaces[j].raceShips.find { it.idxtype == GBData.FACTORY }
                        GBLog.d("Ordered Cruiser")
                        factory?.let { GBController.universe.makeCruiser(it) }
                    }
                    universe.scheduledActions.add(GBUniverse.GBInstruction(now + 1 + i * 10, code))
                }
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
            universe.scheduledActions.add(GBUniverse.GBInstruction(-1, code))
        }

    }
}