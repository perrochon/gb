// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Inspiration
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/exam.dat
// https://github.com/kaladron/galactic-bloodshed/blob/master/data/ship.dat

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBDebug.gbAssert
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

class GBShip(val idxtype: Int, val race: GBRace, var loc: GBLocation) {

    val id: Int
    val uid: Int
    val rid: Int
    val name: String
    val type: String
    val speed: Int

    var dest: GBLocation? = null
    var trail = arrayListOf<GBxy>()

    init {
        id = GBData.getNextGlobalId()
        universe.allShips.add(this)
        uid = universe.allShips.indexOf(this)

        race.raceShips.add(this)
        rid = race.raceShips.indexOf(this)

        when (loc.level) {
            LANDED -> {
                loc.getPlanet()!!.landedShips.add(this)
            }
            ORBIT -> {
                loc.getPlanet()!!.orbitShips.add(this)
            }
            SYSTEM -> {
                loc.getStar()!!.starShips.add(this)
            }
            DEEPSPACE -> {
                universe.universeShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }

        }

        type = GBData.getShipType(idxtype)
        name = type + " " + race.raceShips.indexOf(this)
        speed = GBData.getShipSpeed(idxtype)
    }

    fun moveShip(loc: GBLocation) {

        GBDebug.l3("Moving " + name + " from " + this.loc.getLocDesc() + " to " + loc.getLocDesc())

        // TODO no need to always do these whens, e.g. if things are the same, no need to remove and add
        when (this.loc.level) {
            LANDED -> {
                this.loc.getPlanet()!!.landedShips.remove(this)
            }
            ORBIT -> {
                this.loc.getPlanet()!!.orbitShips.remove(this)
            }
            SYSTEM -> {
                this.loc.getStar()!!.starShips.remove(this)
            }
            DEEPSPACE -> {
                universe.universeShips.remove(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship removement $loc", { false })
            }
        }
        when (loc.level) {
            LANDED -> {
                loc.getPlanet()!!.landedShips.add(this)
            }
            ORBIT -> {
                loc.getPlanet()!!.orbitShips.add(this)
            }
            SYSTEM -> {
                loc.getStar()!!.starShips.add(this)
            }
            DEEPSPACE -> {
                universe.universeShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }
        }
        this.loc = loc

    }

    fun doShip() {

        if (dest == null) {
            if (trail.isNotEmpty())
                trail.remove(trail.last())
            return
        }
        val dest = this.dest!!
        val dxy = dest.getLoc()       // use getLoc to get universal (x,y)
        val sxy = this.loc.getLoc()   // center of planet for landed and orbit

        GBDebug.l3("Flying " + name + " from " + this.loc.getLocDesc() + " to " + dest.getLocDesc())


        if (loc.level == LANDED) { // We are landed

            GBDebug.l3(name + " is landed")

            if ((dest.level != loc.level) || (dest.refUID != loc.refUID)) { // we need to get to orbit
                var next = GBLocation(loc.getPlanet()!!, 1f, 1f)
                moveShip(next)

                GBDebug.l3(name + " is launched")
                universe.news.add("Launched $name to ${loc.getLocDesc()}.\n\n")
            }
            // here we will deal with surface to surface of same planet moves...
            return

        } else if ((loc.level == ORBIT) && (loc.refUID == dest.refUID)) {
            // if in orbit at destination, land

            moveShip(dest)
            universe.news.add("$name landed on ${loc.getLocDesc()}. ( ${loc.x} , ${loc.y} )\n\n")
            this.dest = null
            return


        } else {
            // we are in orbit, in system, or in space

            // Distance from to can be factored out...

            var dx = dxy.x - sxy.x
            var dy = dxy.y - sxy.y

            GBDebug.l3("(dx,dy) = ($dx, $dy)\n")

            var togo = sqrt(dx * dx + dy * dy)

            if (togo < speed) { // we will arrive this turn . Arrival must be a planet

                var next = GBLocation(dest.getPlanet()!!, 1f, 1f)
                moveShip(next)
                universe.news.add("$name arrived in Orbit at ${loc.getLocDesc()}. ( ${loc.x} , ${loc.y} )\n\n")

                if (dest.level == ORBIT) {
                    this.dest = null

                }
                return
            }

            GBDebug.l3("togo = $togo\n")

            var factorX = speed / togo
            var factorY = speed / togo

            GBDebug.l3("(fx,fy) = ($factorX, $factorY)\n")

            var rawX = dx * factorX * speed
            var rawY = dy * factorY * speed

            GBDebug.l3("(rx,ry) = ($rawX, $rawY)\n")

            var offsetX = min(abs(dx), abs(rawX)) * sign(dx)
            var offsetY = min(abs(dy), abs(rawY)) * sign(dy)

            GBDebug.l3("Flying from (${sxy.x}, ${sxy.y}) direction (${dxy.x}, ${dxy.y}) for ($offsetX, $offsetY) at speed $speed\n")

            trail.add(sxy)

            if (loc.level == DEEPSPACE) {
                // TODO Test if we arrived at destination

                var next = GBLocation(sxy.x + offsetX, sxy.y + offsetY)
                moveShip(next)
                universe.news.add("$name moved in ${loc.getLocDesc()}. ( ${loc.x} , ${loc.y} )\n\n")
                return

            } else {
                // TODO Test if we arrived at destination

                var next = GBLocation(loc.getStar()!!, sxy.x + offsetX, sxy.y + offsetY, true)
                moveShip(next)

                universe.news.add("$name moved in ${loc.getLocDesc()}. ( ${loc.x} , ${loc.y} )\n\n")
                return
            }
        }
    }


    fun getStar(): GBStar? {
        return loc.getStar()
    }

}

