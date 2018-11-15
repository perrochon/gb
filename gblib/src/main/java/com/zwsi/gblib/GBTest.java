// Copyright 2018 Louis Perrochon. All rights reserved

// GBTest runs step by step through a GB scenario using only what's in GBLib.
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//
//   TODO: Move GBTest into a test directory

package com.zwsi.gblib;

public class GBTest {

    private static GBUniverse universe = null;
    private static int gameTurns = 0;

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