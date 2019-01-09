// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib

import kotlin.math.floor

class GBSector constructor(val planet: GBPlanet) {

    // Fixed Properties: type, typeSymbol, revenue
    internal var type = -1        // nonexisting type
        private set
    internal var typeSymbol = "?"
        private set
    internal var revenue = 0
        private set
    internal var maxPopulation = 0
        private set

    fun chooseType(planetIdxType: Int) {
        type = GBData.sectorTypesChance[planetIdxType][GBData.rand.nextInt(10)]
        typeSymbol = GBData.sectorTypeConsoleFromIdx(type)
        revenue = GBData.sectorMoneyFromIdx(type)
        maxPopulation = GBData.sectorMaxPopulationFromIdx(type)
    }

    // Changing Properties: owner, population
    internal var owner: GBRace? = null
        set(r) {
            field = r!!
            ownerID = field!!.uid
            ownerName = field!!.name
        }

    private var ownerID: Int = -1
        private set
    private var ownerName = ""

    // population
    internal var population = 0
        private set

    fun assignPopulation(r: GBRace, number: Int) {
        changePopulation(number)
        owner = r
    }

    fun changePopulation(number: Int) {
        population += number
        planet.population += number
    }

    fun growPopulation() {
        val populationChange =  population.toFloat() * (getBirthrate().toFloat() / 100f) * (1f - population.toFloat() / maxPopulation.toFloat())
        changePopulation(populationChange.toInt())
    }

    fun landPopulation(r: GBRace, number: Int) {
        GBLog.d("GBSector: Landing $number of ${r.name}")
        if (owner != null) {
            // Can't land on populated sector. They all die...
            return
        }
        assignPopulation(r, number)
    }

    fun getBirthrate(): Int {
        return owner?.birthrate ?: 0
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

