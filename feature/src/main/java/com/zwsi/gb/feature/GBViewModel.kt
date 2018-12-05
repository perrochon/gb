package com.zwsi.gb.feature

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBUniverse
import com.zwsi.gblib.GBxy
import kotlin.system.measureNanoTime

class GBViewModel {

    companion object {

        var viewStars = universe.allStars
        var viewPlanets = universe.allPlanets

        var viewShips = universe.getAllShipsList()
        var viewUniverseShips = universe.getUniverseShipsList()
        var viewStarShips: ArrayList<List<GBShip>> = ArrayList()
        var viewShipTrails: ArrayList<List<GBxy>> = ArrayList()

        var lastTurn = -1
        var updateTimeTurn = 0L

        init {
            fillViewStarShips()
            fillViewShipTrails()
        }

        fun update() {
            if (universe.turn > lastTurn) {
                updateTimeTurn = measureNanoTime {

                    lastTurn = universe.turn

                    // Not updating stars and planets as those lists don't change
                    // TODO: Deep copy of stars and planets? Then copy changed data

                    // Ships
                    viewShips = universe.getAllShipsList()
                    viewUniverseShips = universe.getUniverseShipsList()

                    fillViewStarShips()
                    fillViewShipTrails()
                }
            }

            // TODO convert all coordinates to source coordinates after updating? Saves a few multiplications

        }

        fun fillViewStarShips(){
            for (s in viewStars) {
                viewStarShips.add(s.uid, s.getStarShipsList())
            }
            assert(viewStarShips.size == universe.getNumberOfStars())
        }

        fun fillViewShipTrails() {
            for (sh in viewShips) {
                viewShipTrails.add(sh.uid, sh.getTrailList())
            }
            assert(viewShipTrails.size == universe.getUniverseShipsList().size)

        }

    }





}