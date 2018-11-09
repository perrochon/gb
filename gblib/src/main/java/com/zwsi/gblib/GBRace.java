// Copyright 2018 Louis Perrochon. All rights reserved

package com.zwsi.gblib;

class GBRace {

    String name;
    int absorption;
    int explore;    // adventurism 00..100 (originally percent)
    int birthrate; // 00...100 (originally 0..1?)

    // Add more as code uses them...

    GBRace(String name) {
        this.name = name;
        this.birthrate = 50;
        this.explore = 20;
        GBDebug.l2("Created Race " + name + " with birthrate " + birthrate);
    }

}
// Races



/*
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

