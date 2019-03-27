// Copyright 2018-2019 Louis Perrochon. All rights reserved

// GBPlanet deals with planetary level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBPlanet(val uid: Int, val sid: Int, val uidStar: Int, var loc: GBLocation) {
    // TODO DELETE sid can probably be removed.
    // sid is the "Star ID" (aka orbit), where in the order of planets of the parent star is this 0..n

    var name = GBData.planetNameFromIdx(GBData.selectPlanetNameIdx())
    var idxtype = GBData.selectPlanetTypeIdx() // idxtype of this planet
    var type = GBData.planetTypeFromIdx(idxtype)
    val hxw = GBData.selectPlanetSize(idxtype) // Transient, so can be val
    var height = hxw.first
    var width = hxw.second

    val star: GBStar
        get() = u.star(uidStar) // FIXME PERSISTENCE These return universe objects, not vm objects...  only return UID

    val size: Int
        get() = width * height

    var planetPopulation = 0;
    var planetUidRaces: MutableSet<Int> = HashSet()

    // Planets are rectangles with wrap arounds on the sides. Think Mercator.
    // Sector are stored in a straight array, which makes some things easier (other's not so)
    var sectors: Array<GBSector>

    // Landed Ships
    var landedUidShips: MutableSet<Int> = HashSet() // UID of shipsData. Persistent

    internal val landedShips: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes. How often is this used...
        get() = landedUidShips.map { u.ship(it) }

    // Orbit Ships
    var orbitUidShips: MutableSet<Int> = HashSet() // UID of shipsData. Persistent

    internal val orbitShips: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes. How often is this used....
        get() = orbitUidShips.map { u.ship(it) }

    init {
        // Make Sectors
        sectors = Array(width * height) { GBSector(uid) }

        for (i in 0 until width * height) {
            sectors[i].chooseType(idxtype)
            sectors[i].init(i, sectorX(i), sectorY(i))
        }

        GBLog.d("Made Planet $name of idxtype $type. Planet size is ${height}x$width")
        GBLog.d("  Planet $name location is Polar ( ${loc.r} , ${loc.t} )")
        GBLog.d("  Planet $name location is Cartesian( ${loc.x} , ${loc.y} )")
    }

    fun sectorEmptyOfShips(sector: GBSector): Boolean {
        return landedShips.filter { (it.loc.sx == sector.x) && (it.loc.sy == sector.y) }.isEmpty()
    }

    fun emptySector(): GBSector? {
        return sectors.sortedBy { -it.population }.filter { sectorEmptyOfShips(it) }.firstOrNull()
    }

    // TODO Inline these in init(), and replace with access to member fields everywhere else
    fun sectorX(i: Int): Int {
        return i % width
    }

    fun sectorY(i: Int): Int {
        return i / width
    }

    fun west(x: Int): Int {
        return if (x % width == 0)
            x + width - 1
        else
            x - 1
    }

    fun east(x: Int): Int {
        return if (x % width == width - 1)
            x + 1 - width
        else
            x + 1
    }

    fun north(x: Int): Int {
        return if (x < width)  // first row
            x // north of north pole loops back to itself
        else
            x - width
    }

    fun south(x: Int): Int {
        return if (x > (height - 1) * width) // last row
            x   // south of south pole loops back to itself
        else
            x + width
    }


    fun movePlanet() {
        // TODO Use Keplers law for planet movement along ellipses
        // e.g. http://www.sjsu.edu/faculty/watkins/orbital.htm
        // Need to make sure that no planet goes faster than pods, or pods never catch up...
        // Speed of pods is 1, so angular speed cannot be faster than 1/r

        loc = computePlanetPositions(1)
    }

    fun computePlanetPositions(turns: Int): GBLocation {
        val rt = loc.getSLocP()
        val speed = 1 / (rt.r + 10)  // was 10 for a long time. Changing to 20 with orbiting shipsData
        return GBLocation(
            u.star(uidStar),
            rt.r,
            rt.t - speed * turns
        ) // y points down, anti-clockwise is negative angles...
    }


    fun doPlanet() {

        GBLog.d("Running Year on planet $name")

        movePlanet()

        //Reproduction
        for (i in 0 until width * height) {

            if (sectors[i].population > 0) {

                GBLog.d("Found planetPopulation of ${sectors[i].population} in sector [${sectorX(i)}][${sectorY(i)}] - growing")

                growPopulation(sectors[i])

                GBLog.d("New Population is ${sectors[i].population}")

            }
        }

        val temps = IntArray(width * height) { i -> i - 0 }
        val temps2 = temps.toCollection(ArrayList())
        temps2.shuffle()

        for (i in 0 until width * height) {

            val from = temps2[i]

            if (sectors[from].population > 5) {

                GBLog.d(
                    "Found planetPopulation of ${sectors[from].population} in sector [${sectorX(from)}][${sectorY(
                        from
                    )}] - migrating"
                )

                val movers = sectors[from].population * sectors[from].sectorOwner.explore / 100 / 4

                when (GBData.rand.nextInt(4)) {
                    0 -> migratePopulation(movers, from, east(from))
                    1 -> migratePopulation(movers, from, west(from))
                    2 -> migratePopulation(movers, from, north(from))
                    3 -> migratePopulation(movers, from, south(from))
                }

            }
        }
    }

    // planetPopulation
    // TODO BUG fix planetPopulation. Right now it goes in mysterious ways
    // TODO Instead of keeping track of planet population, just re-count on do?
    // TODO Test population and owner management
    // Three ways populations can change:
    // 1. Adjusting: Changing planetPopulation in just one sector (setup, landing, killing)
    // 2. Moving: Moving from one sector to another.
    // 3. Growth: (reproduction, and reduction due to incompatibility).
    // 4. Kill: Population gets killed by external influence (war)
    // All three call changePopulationOld to change the values. This will eventually need thread safety/proper transactions

    fun landPopulationOnEmptySector(r: GBRace, number: Int) {
        GBLog.d("GBPlanet: Landing $number of ${r.name}")
        val target = sectors.toList().shuffled().firstOrNull({ it.population == 0 })
        if (target != null) {
            adjustPopulation(target, r, number) // If no empty sector, no planetPopulation is landed
            planetUidRaces.add(r.uid)
        }
    }

    private fun migratePopulation(number: Int, from: Int, to: Int) {
        // attempt to migrate planetPopulation

        GBLog.d("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}]")

        if (from == to) return
        if (number == 0) return
        //assert(to < sectors.size) // TODO: This should never happen, but does in some cases.
        if (to >= sectors.size) return

        if (sectors[to].uidSectorOwner == -1) {
            //moving into an empty sector
            var movers = number
            if (sectors[to].population + number > sectors[to].maxPopulation) {
                // Not enough room for all
                movers = (sectors[to].maxPopulation - sectors[to].population) / 2
            }

            GBLog.d("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Explore $movers move")
            movePopulation(sectors[from], movers, sectors[to])

        } else if (sectors[to].uidSectorOwner == sectors[from].uidSectorOwner) {
            //moving to a friendly sector
            var movers = number
            if (sectors[to].population + number > sectors[to].maxPopulation) {
                // Not enough room for all
                movers = (sectors[to].maxPopulation - sectors[to].population) / 2
            }
            GBLog.d("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Reloc! $movers move")
            movePopulation(sectors[from], movers, sectors[to])

        } else {
            // moving to an enemy sector
            // We have a very simple form of war: They don't move... GBSector doesn't support war yet.
            //GBLog.d("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Attack! $number die")
        }
    }

    private fun changePopulation(sector: GBSector, difference: Int) {
        // TODO Launch replace assert by just returning and doing nothing
        assert(
            sector.population + difference <= sector.maxPopulation,
            { "Attempt to increase planetPopulation beyond max" })
        assert(sector.population + difference >= 0, { "Attempt to decrease planetPopulation below 0" })
        assert(sector.uidSectorOwner != -1)

        sector.population += difference
        this.planetPopulation += difference
        // TODO Bug/Regression Owner Population not being updated right now.
        // With only uidRace in Sector, we don't have access to u.race() during u construction.
        // Need to refactor that first, or move all this code into GBController.
        //sector.sectorOwner.population += difference

        if (sector.population == 0) {
            sector.uidSectorOwner = -1
        }

        if (this.planetPopulation == 0) {
            // TODO Need to remove races from the list somewhere (else)
        }

    }

    internal fun adjustPopulation(sector: GBSector, r: GBRace, number: Int) {
        GBLog.d("GBSector: Landing $number of ${r.name}")
        assert(sector.population + number <= sector.maxPopulation)
        assert(sector.population + number >= 0)
        // TODO this assertion should hold but doesn't
        assert((sector.uidSectorOwner == -1) || (sector.uidSectorOwner == r.uid), { "FIXME" }) // FIXME DELETE ?

        sector.uidSectorOwner = r.uid
        changePopulation(sector, number)

    }

    internal fun movePopulation(from: GBSector, number: Int, to: GBSector) {
        assert(from.uidSectorOwner != -1)
        assert((to.uidSectorOwner == from.uidSectorOwner) || (to.uidSectorOwner == -1))
        assert(number <= from.population)
        assert(to.population + number <= to.maxPopulation, { "${to.population},${number},${to.maxPopulation}" })

        to.uidSectorOwner = from.uidSectorOwner
        changePopulation(to, number)
        changePopulation(from, -number)

    }

    internal fun growPopulation(sector: GBSector) {

        if (sector.population == 0) return

        var difference =
            (sector.population.toFloat() * (sector.sectorOwner.birthrate.toFloat() / 100f) * (1f - sector.population.toFloat() / sector.maxPopulation.toFloat())).toInt()

        if (sector.population + difference > sector.maxPopulation)
            difference = (sector.maxPopulation - sector.population)
        if (sector.population + difference < 0)
            difference = -sector.population

        changePopulation(sector, difference)
    }

    fun consoleDraw() {
        println(
            "\n    " + name + " of idxtype " + type
                    + " and size " + height + "x" + width + "\n"
        )

        for (i in 0 until width * height) {
            if (sectorX(i) == 0)
                print("    ")

            print(sectors[i].consoleDraw())

            if (sectorX(i) == width - 1)
                println()
        }

    }
}
