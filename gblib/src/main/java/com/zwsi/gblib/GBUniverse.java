
package com.zwsi.gblib;

public class GBUniverse {

    // All these variables are package private, because (for now?) we trust the package
    GBData data;
    GBStar[] starsArray; // the star Systems //
    int numberOfStars; // how many star Systems in the Universe
    GBRace[] racesArray; // the star Systems //
    int numberOfRaces; // how many star Systems in the Universe

    public int getUniverseMaxX() { return GBData.Companion.getUniverseMaxX();}
    public int getUniverseMaxY() { return GBData.Companion.getUniverseMaxY();}

    GBUniverse(int numberOfRaces) {

        this.numberOfStars = GBData.Companion.getNumberOfStars();
        starsArray = new GBStar[numberOfStars];
        this.numberOfRaces = numberOfRaces;
        racesArray = new GBRace[numberOfRaces];

        data = new GBData();

        // Place Stars
        GBDebug.l3("Making Stars");
        makeStars();

        makeRaces();

        GBDebug.l3("Universe made");
    }

    void consoleDraw() {
        System.out.println("=============================================");
        System.out.println("The Universe");
        System.out.println("=============================================");
        System.out.println("The Universe contains " + numberOfStars + " star(s).\n");

        for (GBStar i : starsArray) {
            if (i != null) {
                i.consoleDraw(data);
            }
        }
        System.out.println("The Universe contains " + numberOfRaces + " race(s).\n");

        for (GBRace i : racesArray) {
            if (i != null) {
                i.consoleDraw(data);
            }
        }

    }

    private void makeStars() {
        GBDebug.l3("Making Stars");
        for (int i = 0; i < numberOfStars; i++) {
            starsArray[i] = new GBStar(data);
            starsArray[i].index = i;
        }

    }

    private void makeRaces() {
        GBDebug.l3("Making Races");

        // Temporary hack

        GBSector sector;
        racesArray[0]= new GBRace(0,"Xenos", 50, 20);
        racesArray[0].index = 0;
        sector = starsArray[0].planetsArray[0].sectors[0][0];
        sector.population = 100;
        sector.owner = racesArray[0];

        if (numberOfStars > 1) {
            racesArray[1] = new GBRace(1,"Impi", 100, 10);
            racesArray[1].index = 1;
            sector = starsArray[1].planetsArray[0].sectors[0][0];
            sector.population = 100;
            sector.owner = racesArray[1];
        }

    }

    public void landPopulation(GBPlanet p, int raceIndex){
        GBDebug.l3("Landing " + racesArray[raceIndex].name + " on " + p.name + "");
        p.sectors[0][0].population = 10;
        p.sectors[0][0].owner=racesArray[raceIndex];
    }


    void doUniverse() {
        for (GBStar s : starsArray) {
            for (GBPlanet p : s.planetsArray) {
                p.doPlanet();
            }
        }

    }

    public GBStar[] getStars() {
        return starsArray;
    } // TODO need to figure out where these live

    public GBPlanet[] getPlanets(GBStar s) {
        return s.planetsArray;
    } // TODO should this be Star? But what about getting all the planets?

    public GBSector[][] getSectors(GBPlanet p) {
        return p.sectors;
    } //TODO should this be in planet? Or Data?


}
