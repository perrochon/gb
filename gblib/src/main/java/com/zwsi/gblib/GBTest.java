// Copyright 2018 Louis Perrochon. All rights reserved

/* GBTest runs step by step through a GB scenario using only what's in GBLib.
   It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output
 */

package com.zwsi.gblib;

import java.lang.String;

// TODO Move GBTest into Test Directory...

public class GBTest {

    public static void main(String[] args) {

        System.out.println("Welcome to GB Test");

        GBUniverse universe = new GBUniverse(1);

        GBDebug.l1("Drawing empty Universe");
        universe.consoleDraw();

        GBDebug.l1("Populating Universe");
        universe.randomPopulate();

        GBDebug.l1("Drawing populated Universe");
        universe.consoleDraw();

        //Place people

        GBDebug.l1("Intelligence arises");
        GBRace first = new GBRace("Xenos");
        GBSector firstSector = universe.systemsArray[0].planetsArray[0].sectors[0][0];
        firstSector.population = 100;
        firstSector.race = first;

        GBDebug.l1("Universe with some population on the first sector of the first planet of the first system");
        universe.consoleDraw();

        //let them spread...
        for (int i = 1; i < 5; i++) {
            universe.systemsArray[0].planetsArray[0].doPlanet();
            universe.consoleDraw();
        }

    }
}

class GBDebug {

    private static final int DEBUG_LEVEL = 3;  // 0 quiet, 1 minimal, 2 verbose, 3 everything

    static void l1(String message) {
        if (DEBUG_LEVEL >= 1) {
            System.out.println("-GBDebug: " + message);
        }
    }

    static void l2(String message) {
        if (DEBUG_LEVEL >= 2) {
            System.out.println("--GBDebug: " + message);
        }
    }

    static void l3(String message) {
        if (DEBUG_LEVEL >= 3) {
            System.out.println("---GBDebug: " + message);
        }
    }
}
















