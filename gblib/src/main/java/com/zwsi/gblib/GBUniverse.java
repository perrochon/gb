
package com.zwsi.gblib;

public class GBUniverse {

    // All these variables are package private, because (for now?) we trust the package
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

        // Place Stars
        GBDebug.INSTANCE.l3("Making Stars");
        makeStars();

        makeRaces();

        GBDebug.INSTANCE.l3("Universe made");
    }

    void consoleDraw() {
        System.out.println("=============================================");
        System.out.println("The Universe");
        System.out.println("=============================================");
        System.out.println("The Universe contains " + numberOfStars + " star(s).\n");

        for (GBStar i : starsArray) {
            if (i != null) {
                i.consoleDraw();
            }
        }
        System.out.println("The Universe contains " + numberOfRaces + " race(s).\n");

        for (GBRace i : racesArray) {
            if (i != null) {
                i.consoleDraw();
            }
        }

    }

    private void makeStars() {
        GBDebug.INSTANCE.l3("Making Stars");
        for (int i = 0; i < numberOfStars; i++) {
            starsArray[i] = new GBStar(i);
        }

    }

    private void makeRaces() {
        GBDebug.INSTANCE.l3("Making Races");

        // Temporary hack

        GBSector sector;
        racesArray[0]= new GBRace(0, 0);
        sector = starsArray[0].getPlanetsArray()[0].getSectors()[0][0];
        sector.setPopulation(100);
        sector.setOwner(racesArray[0]);

        if (numberOfStars > 1) {
            racesArray[1] = new GBRace(1,1);
            sector = starsArray[1].getPlanetsArray()[0].getSectors()[0][0];
            sector.setPopulation(100);
            sector.setOwner(racesArray[1]);
        }

    }

    public void landPopulation(GBPlanet p, int raceIndex){
        GBDebug.INSTANCE.l3("Landing " + racesArray[raceIndex].getName() + " on " + p.getName() + "");
        p.getSectors()[0][0].setPopulation(10);
        p.getSectors()[0][0].setOwner(racesArray[raceIndex]);
    }


    void doUniverse() {
        for (GBStar s : starsArray) {
            for (GBPlanet p : s.getPlanetsArray()) {
                p.doPlanet();
            }
        }

    }

    public GBStar[] getStars() {
        return starsArray;
    } // TODO need to figure out where these live

    public GBPlanet[] getPlanets(GBStar s) {
        return s.getPlanetsArray();
    } // TODO should this be Star? But what about getting all the planets?

    public GBSector[][] getSectors(GBPlanet p) {
        return p.getSectors();
    } //TODO should this be in planet? Or Data?


}
