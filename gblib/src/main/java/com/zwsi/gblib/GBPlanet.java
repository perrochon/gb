// Copyright 2018 Louis Perrochon. All rights reserved

// GBPlanet deals with anything on the planetary level

package com.zwsi.gblib;

class GBPlanet {

    private String name; // name of this planet
    private int type; // type of this planet
    private int height;
    private int width;

    // Planets are rectangles with wrap arounds on the sides. Think Mercator.
    GBSector[][] sectors; //

    GBPlanet(GBData data) {
        name = data.selectPlanetName();
        type = data.selectPlanetType();
        sectors = data.getSectors(type);
        height = sectors.length;
        width = sectors[0].length;

        GBDebug.l3("Made Planet " + name + " of type " + data.planetTypeToString(type)
                + ". Planet size is " + height + "x" + width);

    }

    public String getName() {
        return name;
    }

    void consoleDraw(GBData data) {
        System.out.println("\n    " + name + " of type " + data.planetTypeToString(type)
                + " and size " + height + "x" + width + "\n");

        for (int h = 0; h < height; h++) {
            System.out.print("    ");
            for (int w = 0; w < width; w++) {
                System.out.print(sectors[h][w].consoleDraw(data));
            }
            System.out.println();
        }

    }

    private int west(int x) {
        if (x == 0)
            return sectors[0].length - 1;
        else
            return x - 1;
    }

    private int east(int x) {
        return (x + 1) % sectors[0].length;
    }


    // [kaladron] https://github.com/kaladron/galactic-bloodshed/blob/master/src/doplanet.cc
    // [kaladron] https://github.com/kaladron/galactic-bloodshed/blob/master/src/dosector.cc
    // [kaladron] https://github.com/kaladron/galactic-bloodshed/blob/master/src/perm.cc
    void doPlanet() {
        GBDebug.l2("Running Year on planet " + name);

        GBRace race = null; // TODO this works while there is only one race...

        //Reproduction
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sectors[h][w].population > 0) {
                    GBDebug.l3("Found population of " + sectors[h][w].population + " in sector [" + h + "][" + w + "]");
                    sectors[h][w].population = (sectors[h][w].population * (100 + sectors[h][w].owner.birthrate)) / 100;
                    GBDebug.l3("New population is " + sectors[h][w].population);
                    race = sectors[h][w].owner;
                }
            }
        }

        // Migration
        // [kaladron] did one sector at a time in random order, then moved population out into random directions.
        // In race conditions, it was random who populated a random sector
        // TODO multiple races/planet: Add sectors to a a collection (with x,y), shuffle, iterate. RIP delta
        int[][] delta = new int[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                delta[h][w] = 0;
            }
        }

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sectors[h][w].population > 0) {
                    int movers = ((sectors[h][w].population * sectors[h][w].owner.explore) / 800) * 8;
                    // get a multiple of 8, so no rounding below
                    GBDebug.l3("Moving " + movers + " out of population of " + sectors[h][w].population
                            + " in sector [" + h + "][" + w + "]");

                    // Moving
                    delta[h][w] -= movers;
                    delta[h][west(w)] += movers / 4;
                    delta[h][east(w)] += movers / 4;

                    if (h == 0) {
                        delta[h][west(w)] += movers / 8;
                        delta[h][east(w)] += movers / 8;
                    } else {
                        delta[h - 1][w] += movers / 4;
                    }

                    if (h == height - 1) {
                        delta[h][west(w)] += movers / 8;
                        delta[h][east(w)] += movers / 8;
                    } else {
                        delta[h + 1][w] += movers / 4;
                    }
                    //GBDebug.l3("New population is " + sectors[h][w].population);


                } // if

            }// for

        }
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                sectors[h][w].population += delta[h][w];
                sectors[h][w].owner = race;
            }
        }
    }


}

/*
http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/

Class M -    - These planets are usually about 60% water, 20% land, and an
               even mix of everything else.  Once in a while, you'll find
               a class M with an abnormal atmosphere (like heavy in
               methane content).

Jovian -     - These planets are 100% gaseous, and they are usually
               twice as large as the typical class M planet.  They tend
               to be very high in fertility, too, so you can easily build
               up a large population for taxation and tech purposes.
               Also, ships in orbit around Jovians add fuel to their
               holds every update (tankers are twice as efficient at this),
               so even if you're not a Jovian-type race, having one of
               these nearby can be a tremendous asset.

Water -      - These are largely water planets, and I've found that they
               are generally resource poor.  They can be quite large, ranging
               up in size to class M quality, but they are usually about 40%
               smaller. On some versions there is also mountain sectors. That
               will rise the resource deposity a lot.

Desert -     - These planets are usually 80% desert and 15% mtn/land.  They
               are _very_ nice as far as resource content is concerned, and
               their size range is much like Class M planets.

Forest -     - These planets are almost entirely covered in forest, which is
               the rarest sector type.  Resource content is fairly good.
               Size range is between Water planets and Class M's.  Fertility
               is higher than on a normal class M.

Iceball -    - These planets are generally small (1/5 the size of class M's
               or less), and they consist of around 75% ice and 25% mountain.
               Relative to their size, they are resource rich, but the
               small number of sectors means that resources will come more
               slowly.

Airless -    - These planets are mostly land (75%) with some mountain and
               ice sectors.  The atmosphere is almost always hostile, of
               course, but these planets are probably the most resource
               rich relative to their size (ie. 1/3 the res of a typical
               class M but 1/5 the size).

Asteroid -   - These are just floating rocks in space, not good for a
               heck of a lot.  They don't count as planets in victory
               conditions, and they usually have very few resources,
               and the small number of sectors make it very difficult
               for most asteroids to ever contribute to your cause,
               though having colonies on them have other advantages,
               such as morale bonus.  Sector types are random but range
               among land, desert, ice, and mountain.

*/
