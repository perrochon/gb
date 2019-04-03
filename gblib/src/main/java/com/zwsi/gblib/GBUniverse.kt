package com.zwsi.gblib

import com.squareup.moshi.JsonClass
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.HEADQUARTERS
import com.zwsi.gblib.GBData.Companion.MissionRandom
import java.util.*
import kotlin.math.PI
import kotlin.math.max

@JsonClass(generateAdapter = true)
data class GBUniverse(
    // Constant Fields
    var id : String = "unknown",
    var description: String = "Unitialized universe",
    val universeMaxX: Int = -1,
    val universeMaxY: Int = -1,
    val starMaxOrbit: Float = -1f,
    val planetOrbit: Float = -1f,
    val numberOfStars: Int = -1,
    val numberOfRaces: Int = -1,
    // Variable Fields
    var nextGlobalID: Int = -1,
    var secondPlayer: Boolean = false,
    val playerTurns: IntArray = intArrayOf(5, 5), // uidActive Players currently is 0 or 1, so can use uid

    var turn: Int = -1,
    // FIXME PERSISTENCE SavedGameTest reassign these lists, instead of creating a new Universe. Once fixed, can use val
    // Will val persist them in JSON, though?
    var stars: MutableMap<Int, GBStar> = hashMapOf<Int, GBStar>(),
    var planets: MutableMap<Int, GBPlanet> = hashMapOf<Int, GBPlanet>(),
    var patrolPoints: MutableMap<Int, GBPatrolPoint> = hashMapOf<Int, GBPatrolPoint>(),
    var races: MutableMap<Int, GBRace> = hashMapOf<Int, GBRace>(),
    var ships: MutableMap<Int, GBShip> = hashMapOf<Int, GBShip>(),
    val shots: MutableList<GBVector> = arrayListOf<GBVector>(),
    val news: MutableList<String> = arrayListOf<String>()
    // orders not needed while we only save/restore at beginning of turn
) {

    //numberOfRaces <= what we have in GBData. >= 4 or tests will fail
    constructor(_numberOfStars: Int, _numberOfRaces: Int) : this(
        MissionRandom,
        "A random universe.",
        GBData.UniverseMaxX,
        GBData.UniverseMaxY,
        GBData.starMaxOrbit,
        GBData.PlanetOrbit,
        _numberOfStars,
        _numberOfRaces,
        1000,
        false,
        intArrayOf(5, 5),
        1
    ) {
    }

    var deepSpaceUidShips: MutableSet<Int> = HashSet<Int>() // UID of shipsData.

    // FIXME PERSISTENCE dead shipsData in the Universe. Keep here so they don't get garbage collected too early. Keep one turn
    // dead shipsData not needed in save, etc.
    @Transient
    var deadShips: MutableMap<Int, GBShip> = hashMapOf()

    @Transient
    val orders: MutableList<GBOrder> = arrayListOf<GBOrder>()

    fun getNextGlobalId(): Int {
        return nextGlobalID++
    }

    // Helper functions with null check for map access. Use e.g. stars(uid) instead of stars[uid]!! when null is an error
    // Don't use these when null is a possible value, e.g. in a view that has a uidShip but ship may be dead by now.
    fun star(uid: Int): GBStar {
        return stars[uid]!!
    }

    fun planet(uid: Int): GBPlanet {
        return planets[uid]!!
    }

    fun patrolPoint(uid: Int): GBPatrolPoint {
        return patrolPoints[uid]!!
    }

    fun race(uid: Int): GBRace {
        return races[uid]!!
    }

    fun ship(uid: Int): GBShip {
        return ships[uid]!!
    }

    internal fun consoleDraw() {
        println("=============================================")
        println("The Universe")
        println("=============================================")
        println("The Universe contains $numberOfStars star(s).\n")

        for ((_, star) in stars) {
            star.consoleDraw()
        }
        println("The Universe contains $numberOfRaces race(s).\n")

        for ((_, race) in races) {
            race.consoleDraw()
        }

        println("News:")
        for (s in news) {
            println(s)
        }
    }

    fun makeStarsAndPlanets() {
        GBLog.i("Making stars and planets")
        areas.clear()
        var uidPlanet = 0
        var uidPatrolPoint = 0
        for (uidStar in 0 until numberOfStars) {
            val numberOfPlanets =
                GBData.rand.nextInt(GBData.MaxNumberOfPlanets - GBData.MinNumberOfPlanets) + GBData.MinNumberOfPlanets
            val coordinates = getStarCoordinates()
            stars[uidStar] =
                GBStar(uidStar, numberOfPlanets, GBLocation(coordinates.first.toFloat(), coordinates.second.toFloat()))

            val orbitDist: Float = GBData.MaxSystemOrbit.toFloat() / numberOfPlanets.toFloat()

            for (sidPlanet in 0 until numberOfPlanets) {
                val loc =
                    GBLocation(
                        stars[uidStar]!!,
                        (sidPlanet + 1f) * orbitDist,
                        GBData.rand.nextFloat() * 2f * PI.toFloat()
                    )

                planets[uidPlanet] = GBPlanet(uidPlanet, sidPlanet, uidStar, loc)
                star(uidStar).starUidPlanets.add(uidPlanet)
                uidPlanet++;
            }

            var t = GBData.rand.nextFloat() * 2f * PI.toFloat()
            for (sidPatrolPoint in 0..1) { // 2 is hardwired. Need to update increment to t if we go beyond 2
                val loc =
                    GBLocation(
                        stars[uidStar]!!,
                        starMaxOrbit * 0.4f,
                        t
                    )
                patrolPoints[uidPatrolPoint] = GBPatrolPoint(uidPatrolPoint, sidPatrolPoint, uidStar, loc)
                star(uidStar).starUidPatrolPoints.add(uidPatrolPoint)
                uidPatrolPoint++;
                t = t + PI.toFloat()
            }
        }
    }

    // Get random, but universally distributed coordinates for stars
    // Approach: break up the universe into n areas of equal size, and put one star in each area
    // where n is the smallest square number bigger than numberOfStars. Then shuffle the areas as some will remain
    // empty.

    @Transient
    var areas: ArrayList<Int> = ArrayList() // we fill it up on first call to GetStarCoordinates

    // Knowing areas (and keeping them) at a higher level, if they are made hierarchical
    // (Say 4 areas each with 5 sub-areas, then place stars into sub area) then place one race in each area.
    // Of course for 17 races, this would lead to three levels with 64 sub-sub-areas. Quadratic may be better.

    private fun getStarCoordinates(): Pair<Int, Int> {

        val nos = numberOfStars.toDouble() // TODO we don't have u yet....
        val dim = java.lang.Math.ceil(java.lang.Math.sqrt(nos)).toInt()

        if (areas.isEmpty()) {

            // TODO Does this work? areas = IntArray(dim * dim) { i -> i-1}

            for (i in 0 until dim * dim) {
                areas.add(i, i)
            }
            areas.shuffle()
        }

        val coordinates = IntArray(size = 2)
        val area = areas.removeAt(0)

        val areaX = area % dim   // coordinates of the chosen area
        val areaY = area / dim   // coordinates of the chosen area
        val areaWidth = GBData.UniverseMaxX / dim
        val areaHeight = GBData.UniverseMaxY / dim
        val marginX = areaWidth / 10 // no star in the edge of the area
        val marginY = areaHeight / 10 // no star in the edge of the area

        GBLog.d("Adding Star to area " + area + "[" + areaX + "][" + areaY + "] (" + areaWidth + "x" + areaHeight + "){" + marginX + ", " + marginY + "}")

        coordinates[0] = GBData.rand.nextInt(areaWidth - 2 * marginX) + areaX * areaWidth + marginX
        coordinates[1] = GBData.rand.nextInt(areaHeight - 2 * marginY) + areaY * areaHeight + marginY

        return Pair(coordinates[0], coordinates[1])
    }

    internal fun makeRaces() {
        GBLog.i("Making and landing Races")
        for (i in 0 until numberOfRaces)
            makeRace(i, i)
    }

    private fun makeRace(idxRace: Int, uidStar: Int) {
        val homePlanet = star(uidStar).starPlanets.first()
        val race = GBRace(idxRace, idxRace, homePlanet.uid, 50)
        races[idxRace] = race
        race.raceVisibleStars.add(homePlanet.uidStar)

        homePlanet.landPopulationOnEmptySector(race, 100)
        val sector = homePlanet.emptySector()!!
        val loc = GBLocation(homePlanet, sector.x, sector.y)
        val headquarters = GBShip(u.getNextGlobalId(), HEADQUARTERS, idxRace, loc)
        headquarters.initializeShip()
        race.uidHeadquarters = headquarters.uid

    }

    internal fun doUniverse() {
        GBLog.i("Runing Game Turn ${turn}")

        news.clear()

        // FEATURE only do this if other races are playing. Need to persist which races are playing
        // For now, always play Beetle and Tortoise
//        GBAutoPlayer.playXenos()
//        GBAutoPlayer.playImpi()
        GBAutoPlayer.playBeetle()
        GBAutoPlayer.playTortoise()
        GBAutoPlayer.play5()
        GBAutoPlayer.play6()

        for (o in orders) {
            o.execute()
        }
        orders.clear()

        for ((_, race) in races) {
            race.raceVisibleStars.clear()
            race.raceVisibleStars.add(race.getHome().star.uid)
            if (race.uidHeadquarters == -1 || ship(race.uidHeadquarters).health <= 0) {
                // Race got eliminated... Kill all their ships. TODO What to do on race elimination. Deal with winning.
                for (ship in race.raceShips) {
                    ship.health = 0
                }
            }
            race.money += 50
        }

        for ((_, star) in stars) {
            for (p in star.starPlanets) {
                p.doPlanet()
            }
            for (pp in star.starPatrolPoints) {
                pp.doPatrolPoint()
            }
        }

        for ((_, sh) in ships.filter { (_, ship) -> ship.health <= 0 }) {
            sh.killShip()
        }

        // Review all ships in orbit and see if we can spread them out a bit
        for ((_, p) in planets) {
            if (p.orbitShips.size > 1) {
                val targetR = 2 * PI / p.orbitShips.size
                var previous: GBShip? = null
                for (ship in p.orbitShips.sortedBy { it.loc.t }) {
                    if (previous != null) {
                        if (ship.loc.t - previous.loc.t < targetR) {
                            // regular orbit speed is 0.2. This moves an extra 50%.
                            ship.loc =
                                GBLocation(ship.loc.getPlanet()!!, ship.loc.getOLocP().r, ship.loc.getOLocP().t + 0.1f)
                        }
                    }
                    previous = ship
                }
            }
        }
        // Review all ships in patrol point orbits and see if we can spread them out a bit
        for ((_, pp) in patrolPoints) {
            if (pp.orbitShips.size > 1) {
                val targetR = 2 * PI / pp.orbitShips.size
                var previous: GBShip? = null
                for (ship in pp.orbitShips.sortedBy { it.loc.t }) {
                    if (previous != null) {
                        if (ship.loc.t - previous.loc.t < targetR) {
                            ship.loc =
                                GBLocation(
                                    ship.loc.getPatrolPoint()!!,
                                    ship.loc.getOLocP().r,
                                    ship.loc.getOLocP().t + 0.05f
                                )
                        }
                    }
                    previous = ship
                }
            }
        }

        for ((_, sh) in ships.filter { (_, ship) -> ship.health > 0 }) {
            sh.doShip()
        }

        fireShots()

        // last thing we do...
        turn++

    }

    private fun fireShots() {
        shots.clear()

        // PERF Create one list of all insystem ships, then find shots
        for ((_, star) in stars) {

            var systemShips = star.starShips +
                    star.starPatrolPoints[0].orbitShips +
                    star.starPatrolPoints[1].orbitShips

            // TODO right now we allow in-system to shoot at landed ships. We may want to limit this to orbit ships.

            for (p in star.starPlanets) {
                systemShips += p.orbitShips
                systemShips += p.landedShips
            }


            fireShips@ for (sh1 in systemShips.shuffled()) {
                if (sh1.guns > 0 && sh1.health > 0) {
                    for (sh2 in systemShips.shuffled()) {
                        if (sh1.guns > 0 && sh2.health > 0 && sh1.uidRace != sh2.uidRace) {
                            if (sh1.loc.getLoc().distance(sh2.loc.getLoc()) < sh1.range) {
                                fireOneShot(sh1, sh2)
                                sh1.guns--
                                if (sh1.guns <=0) {
                                    continue@fireShips
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fireOneShot(sh1: GBShip, sh2: GBShip) {
        shots.add(GBVector(sh1.loc.getLoc(), sh2.loc.getLoc(), sh1.race.uid))
        GBLog.d("Firing shot from ${sh1.name} to ${sh2.name} in ${sh1.loc.getLocDesc()}")
        sh2.health = max(0, sh2.health - sh1.damage)
        u.news.add("${sh1.name} fired at ${sh2.name}.\n")
    }
}
