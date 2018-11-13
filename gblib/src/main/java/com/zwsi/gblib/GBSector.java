// Copyright 2018 Louis Perrochon. All rights reserved

// Deals with Sectors

package com.zwsi.gblib;

public class GBSector {
    int type = -1;        // nonexisting type
    String type_symbol = " ";
    int population = 0;   // nobody lives here (yet)
    GBRace owner = null;  // Controlling race. we only want object reference to the race, not a copy of the object.

    GBSector() {

    }

    String consoleDraw(GBData data) {
        if (population == 0) {
            return " " + type_symbol + " ";
        } else {
            //return " \u001B[7m" + GBData.sectorTypeToSymbol(type) + "\u001B[m ";
            //return " [" + type_symbol + "]";
            return " " + Integer.toString(owner.id) + " ";
        }
    }

}

