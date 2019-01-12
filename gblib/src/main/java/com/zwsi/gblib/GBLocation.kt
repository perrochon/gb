// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBData.Companion.rand
import com.zwsi.gblib.GBLog.gbAssert
import kotlin.math.*

data class GBxy(val x: Float, val y: Float) {

    fun distance(to: GBxy): Float {
        return sqrt((to.x - x) * (to.x - x) + (to.y - y) * (to.y - y))
    }

    fun towards(to: GBxy, speed: Float): GBxy {
        val distance = this.distance(to)

        if (speed > distance) { // we are there
            return to
        } else if (this == to) {
            GBLog.w("Tyring to fly towards current location. Returning current location, but please investigate.")
            return this
        } else {
            return GBxy(x + (to.x - x) * speed / distance, y + (to.y - y) * speed / distance)
        }
    }
}

data class GBrt(val r: Float, val t: Float) {}
data class GBsxy(val sx: Int, val sy: Int) {}

data class GBVector(val from: GBxy, val to: GBxy) {}

/** Where you are in the Universe as (x,y) -  (0,0) is top left corner. x to the right, y going down.
 *  In SYSTEM, polar coordinates are available (t, t) for radian and theta (in radian, 0 to the right)
 *  Level tells you if you are landed, in orbit, in system, or deep space.
 *      Theoretically, it could be derived from x,y and/or refUID, but we don't do that. We keep track manually.
 *      We may introduce hyperspace which overlaps with both Deepspace and System so (x,y) alone may not be sufficient.
 *  LANDED: On planet (sx,sy) gives you the coordinates of the sector. (0,0) is top left, sx->right, sy->down,
 *      Used by ships
 *  ORBIT: In orbit, (t,t) gives you the relative polar coordinates to the center. (x,y) is center(x,y) + relative(x,y)
 *      Used by ships
 *  SYSTEM: In system, (x,y) are universal coordinates -- How to do planets?
 *      Used by ships and planets. Constructor requires polar coordinates!
 *  DEEPSPACE: (x,y) are universal coordiantes
 *      Used by ships and stars
 */
class GBLocation {

    // TODO Refactor fun: Location may be a case for subclassing, rather than field: Type and when()

    var level: Int = -1
        private set
    var x: Float = -1f
        private set
    var y: Float = -1f
        private set
    var refUID: Int = -1
        private set
    var t: Float = -1f
        private set
    var r: Float = -1f
        private set
    var sx: Int = -1
        private set
    var sy: Int = -1
        private set

    companion object {
        const val LANDED = 1
        const val ORBIT = 2
        const val SYSTEM = 3
        const val DEEPSPACE = 4
    }

    // make a LANDED location by giving Surface Int x and y
    constructor(planet: GBPlanet, sx: Int, sy: Int) {
        this.level = LANDED
        this.refUID = planet.uid
        this.sx = sx
        this.sy = sy
    }

    /** Make an ORBIT location by giving Float angle theta [t] and distance [r] to center.
     *  Note y is facing down, so 0<t<PI is below the center
     *  */

    constructor(planet: GBPlanet, r: Float, t: Float) {

        // These asserts may also catch mistaken use of floats
        gbAssert("Distance to planet too big", r > 3f)

        this.level = ORBIT
        this.refUID = planet.uid

        this.t = t
        this.r = r
        this.x = r * cos(t)
        this.y = r * sin(t)
    }

    /** Make a SYSTEM location from Float (r,t) radius from center and theta */
    constructor(star: GBStar, r: Float, t: Float) {

        // These asserts may also catch mistaken use of Float (x,y).
        gbAssert("Distance to star too big", r > GBData.SystemBoundary)

        this.level = SYSTEM
        this.refUID = star.uid
        this.r = r
        this.t = t
        this.x = r * cos(t)
        this.y = r * sin(t)
    }

    // TODO  figure out how to not need boolean to set flag in constructor
    // Stupid: pass a boolean to use cartesian coordinates in constructor? Could use GBxy and GBrt to distinguish,
    // or subclasses instead of when

    // This takes universal coordinates... Used when moving ships in.
    constructor(star: GBStar, x: Float, y: Float, dummy: Boolean) {
        this.level = SYSTEM
        this.refUID = star.uid
        this.x = x - star.loc.x
        this.y = y - star.loc.y
        this.r = sqrt(x * x + y * y)
        this.t = atan2(y, x)
    }

    /** Make a DEEPSPACE location from Float (x,y) */
    constructor(x: Float, y: Float) {
        this.level = DEEPSPACE
        this.x = x
        this.y = y
    }

    /** Get Universal (x,y). Returns the center of the planet for LANDED and ORBIT. Used for distance, etc.
     *
     * Use this to make sure you get some meaningful (x,y) in every case
     *
     * */
    fun getLoc(): GBxy {

        if (level == LANDED) {
            // TODO This calculation is probably a rendering issue and belongs into MapView.
            // The constants have to be the same as the ones used to draw planet surfaces on the map
            var size = 1.6f / this.getPlanet()!!.width
            return GBxy(universe.allPlanets[refUID].loc.getLoc().x - 0.80f + sx.toFloat() * size + size/2,
                universe.allPlanets[refUID].loc.getLoc().y -0.4f + sy.toFloat() * size + size/2)
        }
        if (level == ORBIT) {
            return GBxy(universe.allPlanets[refUID].loc.getLoc().x + x, universe.allPlanets[refUID].loc.getLoc().y + y)
        }
        if (level == SYSTEM) {
            return GBxy(universe.allStars[refUID].loc.getLoc().x + x, universe.allStars[refUID].loc.getLoc().y + y)
        } else {
            return GBxy(x, y)
        }
    }


    /** Get LANDED location */
    fun getLLoc(): GBsxy {
        gbAssert("This is not a landed location.", level == LANDED)
        return GBsxy(sx, sy)
    }

    /** Get (Planet) Orbit location in Polar - relative to planet*/
    fun getOLocP(): GBrt {
        gbAssert("This is not an orbit location.", level == ORBIT)
        return GBrt(r, t)
    }

    /** Get (Planet) Orbit location in Cartesian - relative to planet*/
    fun getOLocC(): GBxy {
        gbAssert("This is not an orbit location", level == ORBIT)
        return GBxy(x, y)
    }

    /** Get System location in Polar (Orbit around the star) */
    fun getSLocP(): GBrt {
        gbAssert("This is not a system location", level == SYSTEM)
        return GBrt(r, t)
    }

    /** Get System location in Cartesian (in relation to star (x,y) */
    fun getSLocC(): GBxy {
        gbAssert("This is not a system location", level == SYSTEM)
        return GBxy(x, y)
    }

    /** Get DeepSpace location */
    fun getDLoc(): GBxy {
        gbAssert("This is not a deep space location", level == DEEPSPACE)
        return GBxy(x, y)
    }

    /** Get a string representation of a location. */
    fun getLocDesc(): String {
        when (level) {
            LANDED -> {
                return "Landed on " + universe.allPlanets[refUID].name +
                        "/" + universe.allPlanets[refUID].star.name
            }
            ORBIT -> {
                return "Orbit of " + universe.allPlanets[refUID].name +
                        "/" + universe.allPlanets[refUID].star.name
            }
            SYSTEM -> {
                return "System " + universe.allStars[refUID].name
            }
            DEEPSPACE -> {
                return "Deep Space"
            }
            else -> {
                gbAssert("Location is Limbo", { false })
                return "Limbo"
            }
        }
    }

    fun getPlanet(): GBPlanet? {
        when (level) {
            LANDED -> {
                return universe.allPlanets[refUID]
            }
            ORBIT -> {
                return universe.allPlanets[refUID]
            }
            SYSTEM -> {
                gbAssert("Location is System, but asking for planet", { false })
            }
            DEEPSPACE -> {
                gbAssert("Location is Deep Space, but asking for planet", { false })
            }
            else -> {
                gbAssert("Ship dead or in limbo", { false })
            }
        }
        return null
    }

    fun getStar(): GBStar? {
        when (level) {
            LANDED -> {
                return universe.allPlanets[refUID].star
            }
            ORBIT -> {
                return universe.allPlanets[refUID].star
            }
            SYSTEM -> {
                return universe.allStars[refUID]
            }
            DEEPSPACE -> {
                gbAssert("Location is Deep Space, but asking for star", { false })
            }
            else -> {
                gbAssert("Ship dead or in limbo", { false })
            }
        }
        return null
    }
}