// Copyright 2018 Louis Perrochon. All rights reserved

// GBPlanet deals with anything on the planetary level

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.rand
import java.util.*
import kotlin.math.PI

@JsonClass(generateAdapter = true)
data class GBPlanet(val uid: Int, val sid: Int, val star: GBStar) {
    // sid is the "starID" (aka orbit), which planet of the parent star is this 0..

    val id: Int
    val idxtype: Int // idxtype of this planet

    val name: String
    val type: String

    var planetOwner: GBRace? = null;

    var loc: GBLocation

    val ownerName: String
        get() = planetOwner?.name ?: "<not owned>"

    // Planets are rectangles with wrap arounds on the sides. Think Mercator.
    // Sector are stored in a straight array, which makes some things easier (other's not so)
    var sectors: Array<GBSector>
    var height: Int
    var width: Int

    val size: Int
        get() = width * height

    var planetPopulation = 0;

    // TODO PERSISTENCE Save these, or rebuild on loading?
    // If they are ships, as opposed to UIDs, need to rebuild, as the old objects will be gone...
    @Transient
    internal val landedShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf()) // the ships on ground of this planet
    @Transient
    internal val orbitShips: MutableList<GBShip> =
        Collections.synchronizedList(arrayListOf()) // the ships in orbit of this planet
    @Transient
    internal var lastLandedShipsUpdate = -1
    @Transient
    internal var landedShipsList = landedShips.toList()
    @Transient
    internal var lastOrbitShipsUpdate = -1
    @Transient
    internal var orbitShipsList = orbitShips.toList()

    init {
        id = GBData.getNextGlobalId()
        name = GBData.planetNameFromIdx(GBData.selectPlanetNameIdx())

        idxtype = GBData.selectPlanetTypeIdx()
        type = GBData.planetTypeFromIdx(idxtype)

        val orbitDist: Float = GBData.MaxPlanetOrbit.toFloat() / star.numberOfPlanets.toFloat()

        loc = GBLocation(star, (sid + 1f) * orbitDist, rand.nextFloat() * 2f * PI.toFloat())

        GBLog.d("Planet $name location is Polar ( ${loc.r} , ${loc.t} )")
        GBLog.d("Planet $name location is Cartesian( ${loc.x} , ${loc.y} )")


        // Make Sectors
        // Get random width and corresponding height within type appropriate bounds (e.g. jovians are bigger
        height = GBData.selectPlanetHeight(idxtype)
        width = GBData.selectPlanetWidth(height)

        sectors = Array(width * height) { GBSector(uid) }

        for (i in 0 until width * height) {
            sectors[i].chooseType(idxtype)

        }

        GBLog.d(
            "Made Planet " + name + " of idxtype " + type
                    + ". Planet size is " + height + "x" + width
        )
    }

    fun getLandedShipsList(): List<GBShip> {
        if (u.turn > lastLandedShipsUpdate) {
            landedShipsList = landedShips.toList().filter { it.health > 0 }
            lastLandedShipsUpdate = u.turn
        }
        return landedShipsList
    }

    fun getOrbitShipsList(): List<GBShip> {
        if (u.turn > lastOrbitShipsUpdate) {
            orbitShipsList = orbitShips.toList().filter { it.health > 0 }
            lastOrbitShipsUpdate = u.turn
        }
        return orbitShipsList
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

    fun sectorEmpty(x: Int, y: Int): Boolean {
        return landedShips.filter { (it.loc.sx == x) && (it.loc.sy == y) }.isEmpty()
    }

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

        val rt = loc.getSLocP()
        val speed = 1 / (rt.r + 10)  // was 10 for a long time. Changing to 20 with orbiting ships
        loc = GBLocation(star, rt.r, rt.t - speed) // y points down, anti-clockwise is negative angles...
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

                val movers = sectors[from].population * sectors[from].owner!!.explore / 100 / 4

                when (GBData.rand.nextInt(4)) {
                    0 -> migratePopulation(movers, from, east(from))
                    1 -> migratePopulation(movers, from, west(from))
                    2 -> migratePopulation(movers, from, north(from))
                    3 -> migratePopulation(movers, from, south(from))
                }

            }
        }
    }

    fun landPopulationOnEmptySector(r: GBRace, number: Int) {
        GBLog.d("GBPlanet: Landing $number of ${r.name}")
        val target = sectors.toList().shuffled().firstOrNull({ it.population == 0 })!!
        adjustPopulation(target, r, number) // If no empty sector, no planetPopulation is landed
    }

    fun migratePopulation(number: Int, from: Int, to: Int) {
        // attempt to migrate planetPopulation

        GBLog.d("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}]")

        if (from == to) return
        if (number == 0) return
        //assert(to < sectors.size) // TODO: This should never happen, but does in some cases.
        if (to >= sectors.size) return

        if (sectors[to].owner == null) {
            //moving into an empty sector
            var movers = number
            if (sectors[to].population + number > sectors[to].maxPopulation) {
                // Not enough room for all
                movers = (sectors[to].maxPopulation - sectors[to].population) / 2
            }

            GBLog.d("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Explore $movers move")
            movePopulation(sectors[from], movers, sectors[to])

        } else if (sectors[to].owner == sectors[from].owner) {
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
        assert(sector.population + difference <= sector.maxPopulation, {"Attempt to increase planetPopulation beyond max"})
        assert(sector.population + difference >= 0, {"Attempt to decrease planetPopulation below 0"})
        assert(sector.owner != null)

        sector.population += difference
        this.planetPopulation += difference
        sector.owner!!.population += difference

        if (sector.population == 0) {
            sector.owner = null
        }

        if (this.planetPopulation == 0) {
            this.planetOwner = null
        }

    }

    internal fun adjustPopulation(sector: GBSector, r: GBRace, number: Int) {
        GBLog.d("GBSector: Landing $number of ${r.name}")
        assert(sector.population + number <= sector.maxPopulation)
        assert(sector.population + number >= 0 )
        // TODO this assertion should hold but doesn't
        assert((sector.owner == null) || (sector.owner == r), {"$planetOwner, $r"})

        sector.owner = r
        changePopulation(sector, number)

    }

    internal fun movePopulation(from: GBSector, number: Int, to: GBSector) {
        assert(from.owner != null)
        assert((to.owner == from.owner) || (to.owner == null))
        assert(number <= from.population)
        assert(to.population + number <= to.maxPopulation, {"${to.population},${number},${to.maxPopulation}"})

        to.owner = from.owner
        changePopulation(to, number)
        changePopulation(from, -number)

    }

    internal fun growPopulation(sector: GBSector) {

        if (sector.population == 0) return

        var difference =
            (sector.population.toFloat() * (sector.owner!!.birthrate.toFloat() / 100f) * (1f - sector.population.toFloat() / sector.maxPopulation.toFloat())).toInt()

        if (sector.population + difference> sector.maxPopulation)
            difference= (sector.maxPopulation - sector.population)
        if (sector.population + difference < 0)
            difference= -sector.population

        changePopulation(sector, difference)
    }





}

/*
http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/

Class M -    - These allPlanets are usually about 60% water, 20% land, and an
               even mix of everything else.  Once in a while, you'll find
               a class M with an abnormal atmosphere (like heavy in
               methane content).

Jovian -     - These allPlanets are 100% gaseous, and they are usually
               twice as large as the typical class M planet.  They tend
               to be very high in fertility, too, so you can easily build
               up a large planetPopulation for taxation and tech purposes.
               Also, ships in orbit around Jovians add fuel to their
               holds every update (tankers are twice as efficient at this),
               so even if you're not a Jovian-typeIdx race, having one of
               these nearby can be a tremendous asset.

Water -      - These are largely water allPlanets, and I've found that they
               are generally resource poor.  They can be quite large, ranging
               up in size to class M quality, but they are usually about 40%
               smaller. On some versions there is also mountain sectors. That
               will rise the resource deposity a lot.

Desert -     - These allPlanets are usually 80% desert and 15% mtn/land.  They
               are _very_ nice as far as resource content is concerned, and
               their size range is much like Class M allPlanets.

Forest -     - These allPlanets are almost entirely covered in forest, which is
               the rarest sector typeIdx.  Resource content is fairly good.
               Size range is between Water allPlanets and Class M's.  Fertility
               is higher than on a normal class M.

Iceball -    - These allPlanets are generally small (1/5 the size of class M's
               or less), and they consist of around 75% ice and 25% mountain.
               Relative to their size, they are resource rich, but the
               small number of sectors means that resources will come more
               slowly.

Airless -    - These allPlanets are mostly land (75%) with some mountain and
               ice sectors.  The atmosphere is almost always hostile, of
               course, but these allPlanets are probably the most resource
               rich relative to their size (ie. 1/3 the res of a typical
               class M but 1/5 the size).

Asteroid -   - These are just floating rocks in space, not good for a
               heck of a lot.  They don't count as allPlanets in victory
               conditions, and they usually have very few resources,
               and the small number of sectors make it very difficult
               for most asteroids to ever contribute to your cause,
               though having colonies on them have other advantages,
               such as morale bonus.  Sector types are random but range
               among land, desert, ice, and mountain.

*/
