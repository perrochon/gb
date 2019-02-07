// Copyright 2018 Louis Perrochon. All rights reserved

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.PlanetaryOrbit
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import com.zwsi.gblib.GBLog.gbAssert
import java.util.*
import kotlin.math.atan2

@JsonClass(generateAdapter = true)
class GBShip(val id: Int, val idxtype: Int, val uidRace: Int, var loc: GBLocation) {

    // FIXME Use uidRace instead of Race above to remove duplication in JSON.

    // id is a unique object ID. Not currently used anywhere TODO QUALITY id can probably be removed

    // properties that don't change over live time of ship
    var uid: Int    // id in universe wide list
    var name: String // name, first letters of race and type, then id
    var type: String // type in printable form
    var speed: Int   // speed of ship // TODO Feature: insystem and hyperspeed.

    // properties that change over lifetime of ship
    var health: Int  // health of ship. Goes down when shot at. Not going up (as of now)
    var dest: GBLocation? = null

    @Transient
    internal val trails: MutableList<GBxy> = Collections.synchronizedList(arrayListOf<GBxy>())
    @Transient
    internal var lastTrailsUpdate = -1
    @Transient
    internal var trailsList = trails.toList()

    val race: GBRace
        get() = u.race(uidRace)

    fun getTrailList(): List<GBxy> {
        if (u.turn > lastTrailsUpdate) {
            trailsList = trails.toList()
        }
        return trailsList
    }

    init {
        this.uid = u.getNextGlobalId()
        u.allShips[uid]=this
        u.race(uidRace).raceShipsUIDList.add(this.uid)

        when (loc.level) {
            LANDED -> {
                loc.getPlanet()!!.landedUidShips.add(this.uid)
            }
            ORBIT -> {
                loc.getPlanet()!!.orbitUidShips.add(this.uid)
            }
            SYSTEM -> {
                loc.getStar()!!.starUidShipList.add(this.uid)
            }
            DEEPSPACE -> {
                u.deepSpaceShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }
        }

        type = GBData.getShipType(idxtype)
        name =
            u.race(uidRace).name.first().toString() + type.first().toString() + uid // TODO Feature, increment per race only
        speed = GBData.getShipSpeed(idxtype)
        health = GBData.getShipHealth(idxtype)
    }


    fun changeShipLocation(loc: GBLocation) {

        GBLog.d("Changing " + name + " from " + this.loc.getLocDesc() + " to " + loc.getLocDesc())

        // PERF no need to always do these whens, e.g. if things are the same, no need to remove and add
        when (this.loc.level) {
            LANDED -> {
                this.loc.getPlanet()!!.landedUidShips.remove(this.uid)
            }
            ORBIT -> {
                this.loc.getPlanet()!!.orbitUidShips.remove(this.uid)
            }
            SYSTEM -> {
                this.loc.getStar()!!.starUidShipList.remove(this.uid)
            }
            DEEPSPACE -> {
                u.deepSpaceShips.remove(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship removement $loc", { false })
            }
        }
        when (loc.level) {
            LANDED -> {
                if (idxtype == POD) {
                    // TODO We should really handle this somewhere else. If pod were a subtype of ship, it could overwrite
                    // This is a pod, they populate, then destroy.  We set health to 0, and clean up elsewhere
                    loc.getPlanet()!!.landedUidShips.add(this.uid)
                    this.health = 0
                    u.landPopulation(this.loc.getPlanet()!!, uidRace, 1)
                } else {
                    loc.getPlanet()!!.landedUidShips.add(this.uid)
                }
            }
            ORBIT -> {
                loc.getPlanet()!!.orbitUidShips.add(this.uid)
            }
            SYSTEM -> {
                loc.getStar()!!.starUidShipList.add(this.uid)
            }
            DEEPSPACE -> {
                u.deepSpaceShips.add(this)
            }
            else -> {
                gbAssert("Bad Parameters for ship placement $loc", { false })
            }
        }
        this.loc = loc

    }

    fun doShip() {

        if (health > 0) { // for now don't update dead ships...
            moveShip()
            moveOrbitShip()
            trails.add(loc.getLoc())
            while (trails.size > 5) {
                trails.removeAt(0)
            }
        }
    }

    fun killShip() {

        if (health <= 0) {

            GBLog.d("Removing dead ship $name from " + this.loc.getLocDesc())

            // TODO factor out ship death (and call it from where pods explode, or set health to 0 there)
            when (this.loc.level) {
                LANDED -> {
                    this.loc.getPlanet()!!.landedUidShips.remove(this.uid)
                }
                ORBIT -> {
                    this.loc.getPlanet()!!.orbitUidShips.remove(this.uid)
                }
                SYSTEM -> {
                    this.loc.getStar()!!.starUidShipList.remove(this.uid)
                }
                DEEPSPACE -> {
                    u.deepSpaceShips.remove(this)
                }
                else -> {
                    gbAssert("Bad Parameters for ship removement $loc", { false })
                }
            }
            u.race(uidRace).raceShipsUIDList.remove(this.uid)
            u.allShips.remove(this.uid)
            u.deadShips.add(this)
        }
    }

    fun moveOrbitShip() {
        if ((this.dest == null) && (this.loc.level == ORBIT)) {
            this.loc = GBLocation(this.loc.getPlanet()!!, this.loc.getOLocP().r, this.loc.getOLocP().t + 0.2f)
        }
    }


    fun moveShip() {

        if (dest == null) {
            return
        }

        val dest = this.dest!!
        val dxy = dest.getLoc()       // use getLoc to get universal (x,y)
        val sxy = loc.getLoc()        // TODO no longer true  ? : center of planet for landed and orbit

        GBLog.d("Moving $name from ${this.loc.getLocDesc()} to ${dest.getLocDesc()}.")

        if (loc.level == LANDED) { // We are landed

            GBLog.d(name + " is landed")

            if ((dest.level != loc.level) || (dest.uidRef != loc.uidRef)) { // landed and we need to get to orbit

                //What direction are we heading
                val t = atan2(dxy.y - sxy.y, dxy.x - sxy.x)

                val next = GBLocation(loc.getPlanet()!!, PlanetaryOrbit, t)
                changeShipLocation(next)
                u.news.add("Launched $name to ${loc.getLocDesc()}.\n")

                return
            } else {
                // here we will deal with surface to surface moves on the same planet
                // for now, any ship that tries is set to have arrived (so the makeStuff script can give it a new location)

                this.dest = null

            }


            return

        } else if ((loc.level == ORBIT) && (loc.uidRef == dest.uidRef)) {

            // We arrived at the planet of destination

            if (dest.level == ORBIT) { // We arrived in Orbit
                this.dest = null

                return

            } else {
                //dest.level == LANDED
                GBLog.d("$name is in orbit at destination. Landing.")

                // in orbit at destination so we need to land
                changeShipLocation(dest)
                u.news.add("$name ${loc.getLocDesc()}.\n")

                return

            }
        } else {
            // we are not LANDED, so either in ORBIT, in SYTEM, or in DEEPSPACE

            val distance = sxy.distance(dxy)

            if ((distance) < speed + PlanetaryOrbit) { // we will arrive at a planet (i.e. in Orbit) this turn. Can only fly to planets (right now)

                //What direction are we coming from
                val t = atan2(sxy.y - dxy.y, sxy.x - dxy.x)

                val next = GBLocation(dest.getPlanet()!!, PlanetaryOrbit, t)
                changeShipLocation(next)
                u.news.add("$name arrived in ${loc.getLocDesc()}.\n")

                if (dest.level == ORBIT) {
                    this.dest = null

                }
                return
            }

            val nxy = sxy.towards(dxy, speed.toFloat())


            GBLog.d("Flying from (${sxy.x}, ${sxy.y}) direction (${dxy.x}, ${dxy.y}) until (${nxy.x}, ${nxy.y}) at speed $speed\n")


            if (loc.level == DEEPSPACE) {

                val distanceToStar = sxy.distance(dest.getStar()!!.loc.getLoc())


                if (distanceToStar < GBData.SystemBoundary) { // we arrived at destination System

                    // TODO check if destination is the system, in which case we would just stop here.
                    // We can't fly to a system yet, so not a bug just yet.

                    val next = GBLocation(dest.getStar()!!, nxy.x, nxy.y, true)

                    changeShipLocation(next)

                    GBLog.d(" Arrived in System")

                    u.news.add("$name arrived in ${loc.getLocDesc()}. ( ${loc.x.toInt()} , ${loc.y.toInt()} )\n")
                    return

                } else {
                    val next = GBLocation(nxy.x, nxy.y)
                    changeShipLocation(next)

                    GBLog.d(" Flying Deep Space")

                    //universe.news.add("$name moved in ${loc.getLocDesc()}. ( ${loc.x} , ${loc.y} )\n")
                    return

                }


            } else {

                val distanceToStar = sxy.distance(loc.getStar()!!.loc.getLoc())

                if (distanceToStar > GBData.SystemBoundary) {  // we left the system

                    val next = GBLocation(nxy.x, nxy.y)
                    changeShipLocation(next)

                    GBLog.d("Left System")

                    u.news.add("$name entered ${loc.getLocDesc()}. ( ${loc.x.toInt()} , ${loc.y.toInt()} )\n")
                    return
                } else {

                    val next = GBLocation(loc.getStar()!!, nxy.x, nxy.y, true)
                    changeShipLocation(next)

                    GBLog.d("Flying insystem ")

                    //universe.news.add("$name moved in ${loc.getLocDesc()}. ( ${loc.x.toInt()} , ${loc.y.toInt()} )\n")
                    return

                }
            }
        }
    }


    fun getStar(): GBStar? {
        return loc.getStar()
    }

}

