// Copyright 2018 Louis Perrochon. All rights reserved

// Race Logic.
//
// Inspiration:
// https://github.com/kaladron/galactic-bloodshed/blob/master/docs/races.doc
// https://github.com/kaladron/galactic-bloodshed/blob/master/src/races.h
// http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/
// Output of 'profile': https://sourceforge.net/p/gbp/wiki/Home/
// See comment at end of file for some related/copied information

package com.zwsi.gblib;

class GBRace {

    String name;
    int absorption;
    int birthrate; // 00...100 (originally 0..1?)
    int explore;    // adventurism 00..100 (originally percent)

    GBPlanet[] planets;

    // Add more as code uses them...

    GBRace(String name, int birthrate, int explore) {
        this.name = name;
        this.birthrate = birthrate;
        this.explore = explore;
        GBDebug.l2("Created Race " + name + " with birthrate " + birthrate);
    }

    void consoleDraw(GBData data) {

        System.out.println("");
        System.out.println("    ====================");
        System.out.println("    " + name + " Race");
        System.out.println("    ====================");
        System.out.println("    absorbtion: "+ absorption);
        System.out.println("    birthrate:  "+ birthrate);
        System.out.println("    explore:    "+ explore);
    }

}

// Races

/*
https://github.com/kaladron/galactic-bloodshed/blob/master/docs/races.doc

RACES			Galactic Bloodshed			RACES



CONCEPTS

   There are bunch of programmed racial characteristics which offers
players a large variety in capabilities. Of course, no one race
is intended to be ideal over another, and players should learn how
to optimize their particular choice. There are 9 basic characteristics
of each race:
	1) Mesomorph - A player can request to be a 'mesomorphic'
		race. They have a advantage of a only 1 sex.
		They can also take over the bodies of their victims. A
		well run mesomorphic player can be a formidable foe.

	2) Mass - Each race has a mass. The heavier the individual, the
		more fuel it usually takes to launch and land them.
		Heavy creatures may require more fuel to move around than
		light creatures.

	3) Birthrate - This factor (infection rate for mesos) controls
		how rapidly the population in the sector will try to
		adjust toi the maximum support of the sector. High
		values mean that the populations multiply faster.

	4) Fighting Ability - Higher values mean that the race is more
		likely to kill an alien than a lower value. Races
		with lower values will usually need to have larger
		forces to win in direct combat over a high fighting
		ability. It is important to note that this effects only
		land combat and boarding strengths, and not ship to
		ship fighting.

	5) Intelligence - The raw growth rate of technology is goverened
		by the race's intelligence. The technology increase per
		update is IQ/100. Additional technology boosts for a race
		is controlled by technology investments by the individual
		planets under the player's control.

		   Collective intelligence is related to the races total
		population, a sort of collective intelligence. Specifically,
		C IQ = 200* [ (2/PI) atan(population/50000)]^2. It's usually
		a good idea to take advantage of the 'technology investment'
		option to keep pace with more intelligent racial types.

	6) Adventurism - This described how often a sector's
		population wants to move and explore other sectors.
		High value tend to explore and conquer planets on their
		own more efficiently. If you have a low value, you can
		order movements with the 'move' option.

	7) Sexes - Each race has a number of sexes. This represent the
		minimum population that a sector must have in order to
		be able to reproduce. Lower values are preferable to high
		values for colonization efforts.

	8) Metabolism - This value controls how industrious the race. Higher
		metabolism races will produce resources more rapidly than
		lower metabolisms.

        9) Fertilize - This attribute represents the percentage chance that
		a race will increase by one percent the fertility of any
		sector it owns.  Any race may take advantage of space plows
		for a similar effect, once it gets to tech level 5.


http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/
Output of profile: https://sourceforge.net/p/gbp/wiki/Home/

* Attributes -- Attributes are quantizations of a race's abilities.  Currently
there are 11 basic attributes for each race:

** Absorbtion -- Only metamorphs may have this attribute, which allows them to
        absorb enemy troops and civilians in combat if the morph wins.
        Combined with pods, it allows metamorphs to replace alien population
        on planets when pods burst.

** Adventurism -- This described how willing a sector's population is to
        move and explore other sectors.  High value tend to settle planets on
        their own far more efficiently. If you have a low value, you can order
        movements with the 'move' option.

** Birthrate -- This factor determines how rapidly the population in the
        sector will converge to the maximum population supportable on the
        there.  High values mean that the population multiplies faster.

** Fertilize -- This attribute represents the percentage chance that a race
        will increase by one percent the fertility of any sector it owns.  Any
        race may take advantage of space plows for a similar effect, once it
        gets to tech level 5.

** Fighting Ability - Higher values mean that the race is more likely to kill
        an alien than a lower value.  Races with lower values will usually need
        to have larger forces to win in direct combat over a high fighting
        ability.  It is important to note that this effects only land combat
        and ship boarding strengths, and not ship to ship fighting.

** IQ -- The raw growth rate of a race's technology is governed by the race's
        intelligence.  The technology increase per update is IQ/100.  Addi-
        tional technology gain is available by technology investments on the
        individual planets under the race's control.

** Collective IQ; IQ limit -- The intelligence of a race with collective IQ is
        related to the race's total population, as follows:
                IQ = IQ_limit * [ (2/PI) atan(population/50000)]^2.
        [Note:  Be careful with this one!  If you can't build pods, then
        having a collective IQ could be disastrous as you won't have the
        knowhow to build *any* kind of useful ships for a looooong time.]

** Mass -- Each race has a mass.  The heavier the individual, the more fuel it
        will take to launch and land ships full of the race.

** Metabolism -- This value controls how industrious the race is.  Higher
        metabolism races will produce resources more rapidly than lower
        metabolisms, and also increase the efficiency of sectors faster.

** Pods -- Pods are small ships, available only to metamorphs but at tech
        level 0, buildable instantly on the surface of planets.  Each one may
        carry a single crew-thing; this makes them ideal for settling other
        planets.  Pods which enter a system after having frozen in deep
        space will warm and eventually burst, possibly leading to spores
        landing on worlds in the new system.  If a spore lands on a sector, a
        ton or more of biomass will be created, resulting in a new colony for
        the podding race.  Note that spores may never land on alien occupied
        sectors unless the podding race has absorbtion.

** Sexes -- Each race has a number of sexes. This represent the minimum
        population that a sector must have in order to be able to reproduce.
        Lower values are preferable to high values for colonization efforts.
        It is not recommended to have more than 3 to 6 sexes at the outside,
        unless you are really looking for a challenge

*/

