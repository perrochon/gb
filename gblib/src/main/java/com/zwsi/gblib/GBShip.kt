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
import kotlin.math.sqrt

class GBShip(val idxtype: Int, val race: GBRace, var loc: GBLocation) {

    val id: Int
    val uid: Int
    val rid: Int
    val name: String
    val type: String
    val speed: Int

    var dest: GBLocation? = null

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

        GBDebug.l3("Moving Ship from" + this.loc.getLocDesc() + "to" + loc.getLocDesc())


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
            return
        }
        val dest = this.dest!!

        if (loc.level == LANDED) { // We are landed
            if ((dest.level != loc.level) || (dest.refUID != loc.refUID)) { // we need to get to orbit
                var next = GBLocation(loc.getPlanet()!!, 1f, 1f)
                moveShip(next)
                universe.news.add("Launched $name to ${loc.getLocDesc()}.\n\n")
            }
            // here deal with surface to surface of same planet moves...
            return
        } else { // we are in orbit, in system, or in space
            // Note GBLocation returns planet's (x,y) for ORBIT, so the ship starts from the center of the planet
            var dx = dest.x - loc.x
            var dy = dest.y - loc.y
            var togo = sqrt(dx * dx + dy * dy)
            var mx = 0f
            var my = 0f
            if (speed > togo) {
                mx = dx
                my = dy
            } else {
                mx = dx / togo * speed
                my = dy / togo * speed
            }
            // TODO the next thing won't work for DEEPSPACE. Need to check if we reached a system
            var next = GBLocation(loc.getStar()!!, loc.x + mx, loc.y + my)
            moveShip(next)
            universe.news.add("$name moved to ${loc.getLocDesc()}.\n\n")
            return
        }
    }


    fun getStar(): GBStar? {
        return loc.getStar()
    }

}

