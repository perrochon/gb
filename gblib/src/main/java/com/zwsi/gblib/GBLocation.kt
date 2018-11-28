// Copyright 2018 Louis Perrochon. All rights reserved
//
// // Location. Can be planet surface, planet orbit, system space, or deep space. All have different coordinates, etc.

package com.zwsi.gblib

import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBDebug.gbAssert

data class GBxy(val x: Float, val y: Float) {}
data class GBrd(val r: Float, val d: Float) {}
data class GBsxy(val sx: Int, val sy: Int) {}

/** Where you are in the Universe as (x,y) -  (0,0) is top left corner. x to the right, y going down.
 *  Level tells you if you are landed, in orbit, in system, or deep space.
 *      Theoretically, it could be derived from x,y and/or refUID, but we don't do that. We keep track manually.
 *      We may introduce hyperspace which overlaps with both Deepspace and System so (x,y) alone may not be sufficient.
 *  LANDED: On planet (sx,sy) gives you the coordinates of the sector. (0,0) is top left, sx->right, sy->down,
 *      Used by ships
 *  ORBIT: In orbit, (r,d) gives you the relative polar coordinates to the center. (x,y) is center(x,y) + relative(x,y)
 *      Used by ships Future: put plants in orbits and move them around stars
 *  SYSTEM: In system, (x,y) are universal coordinates -- How to do planets?
 *      Used by ships
 *  DEEPSPACE: (x,y) are universal coordiantes
 *      Used by ships and stars
 */
class GBLocation {

    var level: Int = -1
        private set
    var x: Float = -1f
        private set
    var y: Float = -1f
        private set
    var refUID: Int = -1
        private set
    var r: Float = -1f
        private set
    var d: Float = -1f
        private set
    var sx: Int = -1
        private set
    var sy: Int = -1
        private set

    companion object {
        const val LANDED = 0
        const val ORBIT = 1
        const val SYSTEM = 2
        const val DEEPSPACE = 3
    }

    // landed
    constructor(planet: GBPlanet, sx: Int, sy: Int) {
        this.level = LANDED
        this.refUID = planet.uid
        this.sx = sx
        this.sy = sy
    }

    // orbit
    constructor(planet: GBPlanet, r: Float, d: Float) {
        this.level = ORBIT
        this.refUID = planet.uid
        this.r = r
        this.d = d
        // Future: showing ships in x,y space, so we need to convert
    }

    // system
    constructor(star: GBStar, x: Float, y: Float) {
        this.level = SYSTEM
        this.refUID = star.uid
        this.x = x
        this.y = y
        // Future: Planets orbiting stars, so there is r/d involved here
    }

    // deep space
    constructor(x: Float, y: Float) {
        this.level = DEEPSPACE
        this.x = x
        this.y = y
    }

    fun getLLoc(): GBsxy {
        gbAssert("This is not a landed location", level != LANDED)
        return GBsxy(sx, sy)
    }

    fun getLoc(): GBxy {
        gbAssert("This is a landed location", level == LANDED)
        return GBxy(x, y)
    }

    fun getOLocP(): GBrd {
        gbAssert("This is not an orbit location", level != ORBIT)
        return GBrd(r, d)
    }

    fun getOLocC(): GBxy {
        gbAssert("This is not an orbit location", level != ORBIT)
        return GBxy(x, y)
    }

    fun getSLoc(): GBxy {
        gbAssert("This is not a system location", level != SYSTEM)
        return GBxy(x, y)
    }

    fun getDLoc(): GBxy {
        gbAssert("This is not a deep space location", level != DEEPSPACE)
        return GBxy(x, y)
    }

    fun getLocDesc(): String {
        when (level) {
            LANDED -> {
                return "Landed on" + universe.allPlanets[refUID].name +
                        " in system " + universe.allPlanets[refUID].star.name
            }
            ORBIT -> {
                return "Orbit of " + universe.allPlanets[refUID].name +
                        " in system " + universe.allPlanets[refUID].star.name
            }
            SYSTEM -> {
                return "System" + universe.allStars[refUID].name
            }
            DEEPSPACE -> {
                return "Deep Space"
            }
            else -> {
                gbAssert("Location is Limbo", { true })
                return "Limbo"
            }
        }
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
                return null
            }
            else -> {
                gbAssert("Ship in Limbo", { true })
                return null
            }
        }
    }
}