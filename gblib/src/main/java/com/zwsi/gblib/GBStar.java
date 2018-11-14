// Copyright 2018 Louis Perrochon. All rights reserved

// GBStar deals with anything on the star system wide level


package com.zwsi.gblib;

public class GBStar {

    private String name; // name of this system

    public int getIndex() { return index; }
    int index; // which position in Universe's star array

    private int numberOfPlanets = 2; // how many Planets in this solar Systems


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private int x; // x coordinate
    private int y; // y coordinate

    GBPlanet[] planetsArray = new GBPlanet[numberOfPlanets]; // the solar Systems // TODO make private an return copy in getter

    GBStar(GBData data) {
        name = data.selectSystemName();
        makePlanets(data);

        GBDebug.l3("Made System " + name);

        int[] coordinates = data.getSystemCoordinates(); //TODO This is accessing a member variable from the caller. Not good
        x = coordinates[0];
        y = coordinates[1];

    }

    public String getName() {
        return name;
    }

    void consoleDraw(GBData data) {
        System.out.println("\n  " + "====================");
        System.out.println("  " + name + " System");
        System.out.println("  " + "====================");
        System.out.println("  The " + name + " system contains " + numberOfPlanets + " planet(s).");

        for (GBPlanet i : planetsArray) {
            if (i != null) {
                i.consoleDraw(data);
            }
        }

    }

    private void makePlanets(GBData data) {
        GBDebug.l3("Making Planets for star " + name);

        for (int i = 0; i < planetsArray.length; i++) {
            planetsArray[i] = new GBPlanet(data);
            planetsArray[i].index = i;
        }

    }
}
