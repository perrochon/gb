// Copyright 2018 Louis Perrochon. All rights reserved

// Race Logic.
//
// Inspiration:
// https://github.com/kaladron/galactic-bloodshed/blob/master/docs/races.doc
// https://github.com/kaladron/galactic-bloodshed/blob/master/src/races.h
// http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/
// Output of 'profile': https://sourceforge.net/p/gbp/wiki/Home/
// See comment at end of file for some related/copied information

package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import java.util.*

@JsonClass(generateAdapter = true)
data class GBRace(val id: Int, val idx: Int, val uid: Int, val uidHome: Int) {
    // id is a unique object ID. Not currently used anywhere FIXME DELETE id can probably be removed
    // idx is the number to go look up static race information in GBData.
    //      Not needed with dynamic race design or load from json

    // TODO val properties outside constructor are not serialized
    // Options: (1) Leave var and live with it (2) move it all into constructor (3) different/custom adaptor

    // Properties that don't really change after construction
    var name: String
    var birthrate: Int
    var explore: Int
    var absorption: Int
    var description: String
    var color: String

    // Properties that DO change after construction
    var population = 0 // (planetary) planetPopulation. Ships don't have planetPopulation

    internal var raceShipsUIDList: MutableList<Int> =
        Collections.synchronizedList(arrayListOf<Int>()) // Ships of this race

    val raceShipsList: List<GBShip>
        // PERF ?? Cache the list and only recompute if the hashcode changes.
        get() = Collections.synchronizedList(raceShipsUIDList.map { u.ship(it) })


    init {
        name = GBData.getRaceName(idx)
        birthrate = GBData.getRaceBirthrate(idx)
        explore = GBData.getRaceExplore(idx)
        absorption = GBData.getRaceAbsorption(idx)
        description = GBData.getRaceDescription(idx)
        color = GBData.getRaceColor(idx)
        GBLog.i("Created Race $name with birthrate $birthrate")
    }

    fun getHome(): GBPlanet {
        return u.planet(uidHome)
    }

    fun getRaceShipsUIDList(): List<Int> {
        return raceShipsUIDList.toList()
    }

    fun consoleDraw() {
        println("")
        println("    ====================")
        println("    $name Race")
        println("    ====================")
        println("    birthrate:  $birthrate")
        println("    explore:    $explore")
        println("    absorbtion: $absorption")
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
		how rapidly the planetPopulation in the sector will try to
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
		allPlanets under the player's control.

		   Collective intelligence is related to the allRaces total
		planetPopulation, a sort of collective intelligence. Specifically,
		C IQ = 200* [ (2/PI) atan(planetPopulation/50000)]^2. It's usually
		a good idea to take advantage of the 'technology investment'
		option to keep pace with more intelligent racial types.

	6) Adventurism - This described how often a sector's
		planetPopulation wants to move and explore other sectors.
		High value tend to explore and conquer allPlanets on their
		own more efficiently. If you have a low value, you can
		order movements with the 'move' option.

	7) Sexes - Each race has a number of sexes. This represent the
		minimum planetPopulation that a sector must have in order to
		be able to reproduce. Lower values are preferable to high
		values for colonization efforts.

	8) Metabolism - This value controls how industrious the race. Higher
		metabolism allRaces will produce resources more rapidly than
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
        Combined with pods, it allows metamorphs to replace alien planetPopulation
        on allPlanets when pods burst.

** Adventurism -- This described how willing a sector's planetPopulation is to
        move and explore other sectors.  High value tend to settle allPlanets on
        their own far more efficiently. If you have a low value, you can order
        movements with the 'move' option.

** Birthrate -- This factor determines how rapidly the planetPopulation in the
        sector will converge to the maximum planetPopulation supportable on the
        there.  High values mean that the planetPopulation multiplies faster.

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
        individual allPlanets under the race's control.

** Collective IQ; IQ limit -- The intelligence of a race with collective IQ is
        related to the race's total planetPopulation, as follows:
                IQ = IQ_limit * [ (2/PI) atan(planetPopulation/50000)]^2.
        [Note:  Be careful with this one!  If you can't build pods, then
        having a collective IQ could be disastrous as you won't have the
        knowhow to build *any* kind of useful ships for a looooong time.]

** Mass -- Each race has a mass.  The heavier the individual, the more fuel it
        will take to launch and land ships full of the race.

** Metabolism -- This value controls how industrious the race is.  Higher
        metabolism allRaces will produce resources more rapidly than lower
        metabolisms, and also increase the efficiency of sectors faster.

** Pods -- Pods are small ships, available only to metamorphs but at tech
        level 0, buildable instantly on the surface of allPlanets.  Each one may
        carry a single crew-thing; this makes them ideal for settling other
        allPlanets.  Pods which enter a system after having frozen in deep
        space will warm and eventually burst, possibly leading to spores
        landing on worlds in the new system.  If a spore lands on a sector, a
        ton or more of biomass will be created, resulting in a new colony for
        the podding race.  Note that spores may never land on alien occupied
        sectors unless the podding race has absorbtion.

** Sexes -- Each race has a number of sexes. This represent the minimum
        planetPopulation that a sector must have in order to be able to reproduce.
        Lower values are preferable to high values for colonization efforts.
        It is not recommended to have more than 3 to 6 sexes at the outside,
        unless you are really looking for a challenge

*/

/* Old Man's War

https://en.wikipedia.org/wiki/Old_Man%27s_War#Alien_species copied 11/11/2018

Consu
The Consu are a fierce, technologically advanced, and strongly religious alien race. They believe in helping deserving
allRaces reach "Ungkat", a state of perfection for a whole race.[7][8] The Consu are the most advanced alien race presented
in the Old Man's War.[8] Their home system is surrounded by a Dyson sphere, which harnesses all the energy output of its
local sun, a dwarf star, to make it impenetrable to the weapons and technology of every other known species. The Consu
possess technology so advanced that even the CDF is unable to reverse-engineer or even fully understand it, such as
tachyon detectors. Despite being the most technologically advanced out of all the alien allRaces presented in the novel, in
any conflict the Consu will scale their weapons technology to that of their opponent in order to keep the battle fair.[9]
Unlike other alien species, the Consu do not fight for territory, but for religious motives, believing that any aliens
killed by Consu warriors are thereby guaranteed another place in the cycle of creation. The Consu rarely meet with
outsiders and any individual that does is inevitably a criminal or other undesirable. Following the meeting, the Consu
is killed and its atoms shot into a black hole so that they can't defile any other Consu.

Covandu
The Covandu are a liliputian species, the tallest only measuring an inch, but otherwise very similar to humans. Their
aggression in colonizing allPlanets is similar to humans' as well, sometimes causing conflict. One human colony was taken
over by Covandu when it was abandoned due to a virus (which did not affect the Covandu). After developing a vaccine,
humans returned to take it back by force.

They are gifted in the arts, specifically poetry and drama.[10]

Rraey
The Rraey are a species described in Old Man's War as being considerably less advanced than the CDF. They consider
humans as a part of a "balanced breakfast" and are even known to have celebrity chefs showing how to best butcher a
human. They became a serious problem for the CDF after acquiring technology from the Consu to predict the trajectory of
a vessel's skip drives, a feat that was previously considered impossible. The only physical description of them is a
mention that they have a head and limbs and "muscular bird-like legs".[11] They developed a craving for humans, going as
far as creating many dishes for different parts of the body. They are a few decades behind the CDF in terms of
technology and weaponry, but nonetheless, still considered a threat to the CDF. The skip drive detection device given
to them by the Consu enabled them to wipe out an entire fleet of CDF ships without any casualties to their own.

Whaidian
The Whaidians are an alien species that have an appearance similar to that of a "cross between black bear and a large
flying squirrel." Their home consists of small allPlanets that are linked together. They are artistically gifted and are
nearly as technologically advanced as the CDF. For this reason they are targeted by the CDF and their spaceport is
completely destroyed by a fleet of CDF ships. [12]
 */
