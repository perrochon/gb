// Copyright 2018 Louis Perrochon. All rights reserved

// LibTest is running through a big Universe and a longer scenario
//

package com.zwsi.gblib

import org.junit.Test

import org.junit.Assert.*

class GBRaceTest {

    @Test
    fun basicSector() {
        val universe = GBUniverse(2,2)
        val r = universe.allRaces[0]
    }

}
