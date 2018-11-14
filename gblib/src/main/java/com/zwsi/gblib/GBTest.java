// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib;

import java.lang.String;

public class GBTest {

    static GBUniverse universe = null;
    static int gameTurns = 0;

    public GBTest(){
    }

    public static void makeUniverse() {
        GBDebug.l1("Making Universe");
        universe = new GBUniverse( 2);
        universe.consoleDraw();
    }

    public static void doUniverse() {
        gameTurns ++;
        GBDebug.l1("Runing Game Turn " + gameTurns);
        universe.doUniverse();
        universe.consoleDraw();
    }

    public static GBUniverse getUniverse() {
        return universe;
    }


    public static void main(String[] args) {

        System.out.println("Welcome to GB Test");
        GBTest tester = new GBTest();
        tester.makeUniverse();
        tester.doUniverse();

    }
}

class GBDebug {

    private static final int DEBUG_LEVEL = 1;  // 0 quiet, 1 minimal, 2 verbose, 3 everything

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
















