// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import kotlin.math.max

@JsonClass(generateAdapter = true)
data class GBSector constructor(val uidPlanet: Int) {

    // Fixed Properties: type, typeSymbol, revenue
    var type = -1        // nonexisting type
    internal var typeSymbol = "?"
    internal var revenue = 0
    var maxPopulation = 0

    fun chooseType(planetIdxType: Int) {
        type = GBData.sectorTypesChance[planetIdxType][GBData.rand.nextInt(10)]
        typeSymbol = GBData.sectorTypeConsoleFromIdx(type)
        revenue = GBData.sectorMoneyFromIdx(type)
        maxPopulation = GBData.sectorMaxPopulationFromIdx(type)
    }

    // Changing Properties: owner, population
    var owner: GBRace? = null
        set(r) {
            field = r
            if (r==null){
                ownerID = -1
                ownerName = ""

            } else {
                ownerID = field!!.uid
                ownerName = field!!.name
            }
        }

    var ownerID: Int = -1
    var ownerName = ""


    // population
    // TODO BUG fix population. Right now it goes in mysterious ways
    // Three ways populations can change:
    // 1. Adjusting: Changing population in just one sector (setup, landing, killing)
    // 2. Moving: Moving from one sector to another.
    // 3. Growth: (reproduction, and reduction due to incompatibility).
    // 4. Kill: Population gets killed by external influence (war)
    // All three call changePopulationOld to change the values. This will eventually need thread safety/proper transactions

    var population = 0

    private fun changePopulation(difference: Int) {
        // TODO Launch replace assert by just returning and doing nothing
        assert(population + difference <= maxPopulation, {"Attempt to increase population beyond max"})
        assert(population + difference >= 0, {"Attempt to decrease population below 0"})
        assert(owner != null)

        population += difference
        // TODO PERSISTENCE The below requires U which is not there yet in Universe constructor when races are landed!
        //u.planet(uidPlanet).population += difference
        owner!!.population += difference
        if (population == 0) {
            owner = null
        }
    }

    internal fun adjustPopulation(r: GBRace, number: Int) {
        GBLog.d("GBSector: Landing $number of ${r.name}")
        assert(population + number <= maxPopulation)
        assert(population + number >= 0 )
        // TODO this assertion should hold but doesn't
        assert((owner == null) || (owner == r), {"$owner, $r"})

        owner = r
        changePopulation(number)

    }

    internal fun movePopulation(number: Int, to: GBSector) {
        assert(owner != null)
        assert((to.owner == owner) || (to.owner == null))
        assert(number <= population)
        assert(to.population + number <= to.maxPopulation, {"${to.population},${number},${to.maxPopulation}"})

        to.owner = owner
        to.changePopulation(number)
        changePopulation(-number)

    }

    internal fun growPopulation() {

        if (population == 0) return

        var difference =
            (population.toFloat() * (owner!!.birthrate.toFloat() / 100f) * (1f - population.toFloat() / maxPopulation.toFloat())).toInt()

        if (population + difference> maxPopulation)
            difference= (maxPopulation - population)
        if (population + difference < 0)
            difference= -population

        changePopulation(difference)
    }

    internal fun consoleDraw(): String {
        if (population == 0) {
            return " $typeSymbol "
        } else {
            //return " \u001B[7m $type_symbol \u001B[m ";
            return " $ownerID "
            //return "[$type_symbol]"

        }
    }
}

