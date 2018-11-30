// Copyright 2018 Louis Perrochon. All rights reserved

// GBPlanet deals with anything on the planetary level

package com.zwsi.gblib

import com.zwsi.gblib.GBData.Companion.rand
import kotlin.math.PI

class GBPlanet(val sid: Int, val star: GBStar) {
    // sid is the "starID" (aka orbit), which planet of the parent star is this 0..

    // Set at creation
    val id: Int
    val uid: Int
    val idxname: Int
    val idxtype: Int // idxtype of this planet

    val name: String
    val type: String

    val owner: GBRace? = null;

    var loc : GBLocation

    val ownerName: String
        get() = owner?.name ?: "<not owned>"

    // Planets are rectangles with wrap arounds on the sides. Think Mercator.
    // Sector are stored in a straight array, which makes some things easier (other's not so)
    var sectors: Array<GBSector>
    var height: Int
    var width: Int

    val size: Int
        get() = width * height

    var population = 0;

    var landedShips: MutableList<GBShip> = arrayListOf() // the ships on ground of this planet
    var orbitShips: MutableList<GBShip> = arrayListOf() // the ships in orbit of this planet


    init {
        id = GBData.getNextGlobalId()
        star.universe.allPlanets.add(this)
        uid = star.universe.allPlanets.indexOf(this)

        idxname = GBData.selectPlanetNameIdx()
        name = GBData.planetNameFromIdx(idxname)

        idxtype = GBData.selectPlanetTypeIdx()
        type = GBData.planetTypeFromIdx(idxtype)

        val orbitDist = 13f / star.numberOfPlanets // TODO Move constant out. Depends on overall sizes

        loc = GBLocation(star, (sid+1f)*orbitDist, rand.nextFloat() * 2f * PI.toFloat())

        GBDebug.l3("Planet $name location is Polar ( ${loc.r} , ${loc.t} )")
        GBDebug.l3("Planet $name location is Cartesian( ${loc.x} , ${loc.y} )")


        // Make Sectors
        // Get random width and corresponding height within type appropriate bounds (e.g. jovians are bigger
        height = GBData.selectPlanetHeight(idxtype)
        width = GBData.selectPlanetWidth(height)

        sectors = Array(width * height) { GBSector(this) }

        for (i in 0 until width * height) {
            sectors[i].type = GBData.sectorTypesChance[idxtype][GBData.rand.nextInt(10)]
        }

        GBDebug.l3(
            "Made Planet " + name + " of idxtype " + type
                    + ". Planet size is " + height + "x" + width
        )
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
        // TODO Use Keplers law for planet movement (or some form reduced to circles)
        // e.g. http://www.sjsu.edu/faculty/watkins/orbital.htm
        // Need to make sure that no planet goes faster than pods, or pods never catch up...
        // Speed of pds is 1, so angular speed cannot be faster than 1/r

        val rt = loc.getSLocP()
        val speed = 1/(rt.r+10)
        loc = GBLocation(star, rt.r, rt.t + speed)
    }

    // [kaladron] https://github.com/kaladron/galactic-bloodshed/blob/master/src/doplanet.cc
    // [kaladron] https://github.com/kaladron/galactic-bloodshed/blob/master/src/dosector.cc
    // [kaladron] https://github.com/kaladron/galactic-bloodshed/blob/master/src/perm.cc
    fun doPlanet() {

        GBDebug.l2("Running Year on planet $name")

        movePlanet()

        var race: GBRace? = null // TODO this works while there is only one race...

        //Reproduction
        for (i in 0 until width * height) {

            if (sectors[i].getPopulation() > 0) {

                GBDebug.l3("Found population of ${sectors[i].getPopulation()} in sector [${sectorX(i)}][${sectorY(i)}] - growing")

                sectors[i].growPopulation()

                GBDebug.l3("New Population is ${sectors[i].getPopulation()}")

            }
        }

        val temps = IntArray(width * height) { i -> i - 0 }
        val temps2 = temps.toCollection(ArrayList())
        temps2.shuffle()

        for (i in 0 until width * height) {

            var from = temps2[i]

            if (sectors[from].getPopulation() > 0) {

                GBDebug.l3(
                    "Found population of ${sectors[from].getPopulation()} in sector [${sectorX(from)}][${sectorY(
                        from
                    )}] - migrating"
                )

                val movers = sectors[from].getPopulation() * sectors[from].getOwner()!!.explore / 100

                when (GBData.rand.nextInt(4)) {
                    0 -> migratePopulation(movers, from, east(from))
                    1 -> migratePopulation(movers, from, west(from))
                    2 -> migratePopulation(movers, from, north(from))
                    3 -> migratePopulation(movers, from, south(from))
                }

            }
        }
    }

    fun migratePopulation(number: Int, from: Int, to: Int) {
        // attempt to migrate population

        GBDebug.l3("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}]")

        if (from == to) return
        if (number == 0) return
        if (to >= sectors.size) return // TODO: This should never happen, but does in some cases. Need to debu

        if (sectors[to].getOwner() == null) {
            //moving into an empty sector
            GBDebug.l3("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Explore $number move")
            sectors[from].changePopulation(-number)
            sectors[to].setPopulation(sectors[from].getOwner()!!, number)

        } else if (sectors[to].getOwner() == sectors[from].getOwner()) {
            //moving to a friendly sector
            GBDebug.l3("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Reloc! $number move")
            sectors[from].changePopulation(-number)
            sectors[to].changePopulation(+number)

        } else {
            // moving to an enemy sector
            // We have a very simple form of war: They all die
            GBDebug.l3("$number from [${sectorX(from)}][${sectorY(from)}]->[${sectorX(to)}][${sectorY(to)}] Attack! $number die")
            sectors[from].changePopulation(-number)
        }
    }

    fun landPopulation(r: GBRace, number: Int) {
        GBDebug.l3("GBPlanet: Landing $number of ${r.name}")
        sectors[GBData.rand.nextInt(width * height)].landPopulation(r, number)
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
               up a large population for taxation and tech purposes.
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
