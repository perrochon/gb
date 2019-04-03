package com.zwsi.gb.feature

import android.app.Activity
import android.graphics.LightingColorFilter
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gb.feature.GBViewModel.Companion.actionsTaken
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.*
import com.zwsi.gblib.GBData.Companion.currentGameFileName
import java.io.File
import kotlin.system.measureNanoTime

// Utility Extension Function for formatting numbers with leading space
fun Double.f(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun Float.f(digits: Int) = java.lang.String.format("%.${digits}f", this)
fun Int.f(digits: Int) = java.lang.String.format("%${digits}d", this)
fun Long.f(digits: Int) = java.lang.String.format("%${digits}d", this)

fun GBLocation.getVMLoc(): GBxy {

    if (level == GBLocation.LANDED) {
        // TODO This calculation is probably a rendering issue and belongs into MapView.
        // The constants have to be the same as the ones used to draw planet surfaces on the map
        val size = GBData.PlanetOrbit * 1.6f / this.getVMPlanet()!!.width
        return GBxy(
            vm.planet(uidRef).loc.getLoc().x - GBData.PlanetOrbit * .80f + sx.toFloat() * size + size / 2,
            vm.planet(uidRef).loc.getLoc().y - GBData.PlanetOrbit * .4f + sy.toFloat() * size + size / 2
        )
    }
    if (level == GBLocation.ORBIT) {
        return GBxy(vm.planet(uidRef).loc.getLoc().x + x, vm.planet(uidRef).loc.getLoc().y + y)
    }
    if (level == GBLocation.PATROL) {
        return GBxy(vm.patrolPoint(uidRef).loc.getLoc().x + x, vm.patrolPoint(uidRef).loc.getLoc().y + y)
    }
    if (level == GBLocation.SYSTEM) {
        return GBxy(vm.star(uidRef).loc.getLoc().x + x, vm.star(uidRef).loc.getLoc().y + y)
    } else {
        return GBxy(x, y)
    }
}

internal fun GBLocation.getVMUidStar(): Int {
    when (level) {
        GBLocation.LANDED -> return vm.planet(uidRef).uidStar
        GBLocation.ORBIT -> return vm.planet(uidRef).uidStar
        GBLocation.SYSTEM -> return uidRef
        GBLocation.PATROL -> return vm.patrolPoint(uidRef).uidStar
        else -> return -1
    }
}

internal fun GBLocation.getVMStar(): GBStar? {
    when (level) {
        GBLocation.LANDED, GBLocation.ORBIT, GBLocation.SYSTEM, GBLocation.PATROL -> return vm.star(getVMUidStar())
        else -> return null
    }
}

internal fun GBLocation.getVMPlanet(): GBPlanet? {
    when (level) {
        GBLocation.LANDED -> return vm.planet(uidRef)
        GBLocation.ORBIT -> return vm.planet(uidRef)
        else -> return null
    }
}


// TODO rename this, once we know what all it does :-)
class GlobalStuff {

    companion object {

        var lastClickTime = 0L
        val clickDelay = 300L

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<GBUniverse> = moshi.adapter(GBUniverse::class.java).indent("  ")
        var autoDo = false

        // Common code once we have a JSON, from makeUniverse, do Universe, and eventually load
        fun processGameInfo(json: String, fresh: Boolean) {

            // We create gameinfo in the worker thread, not the UI thread
            var gameInfo: GBUniverse? = null

            try {

                val fromJsonTime = measureNanoTime {
                    gameInfo = jsonAdapter.lenient().fromJson(json)!!


                }
                Handler(Looper.getMainLooper()).post({
                    GBViewModel.update(
                        gameInfo!!,
                        GBController.elapsedTimeLastUpdate,
                        GBController.elapsedTimeLastJSON,
                        GBController.elapsedTimeLastWrite,
                        GBController.elapsedTimeLastLoad,
                        fromJsonTime,
                        fresh
                    )
                })

            } catch (e: Exception) {
                // TODO We must have read a bad file, but not clear how to tell the user
            }

        }

        fun doUniverse(view: View, force: Boolean = false) {

            if (!force && SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            if (autoDo) { // If we are running on auto, ignore manual Do
                return
            }
            if (GBController.u.playerTurns[0] < 20 && GBController.u.playerTurns[1] < 20) { // FIXME use same constant as VM.
                vm.playerTurns[0]++
                vm.playerTurns[1]++
            }

            actionsTaken.value = System.currentTimeMillis().toInt()


            val message = "Executing Orders"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            Thread(Runnable {
                GBController.currentFilePath = view.context.filesDir
                val json = GBController.doAndSaveUniverse() // SERVER Talk to not-remote server
                processGameInfo(json, false)
            }).start()

        }

        fun toggleContinuous(view: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            if (autoDo) {
                autoDo = false
                val message = "God Mode: Continuous Do OFF"
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            } else {
                autoDo = true
                val message = "God Mode: Continuous Do ON"
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

                Thread(Runnable {
                    while (autoDo) {
                        val json = GBController.doAndSaveUniverse() // SERVER Talk to not-remote server
                        processGameInfo(json, false)
                        Thread.sleep(500) // let everything else catch up before we do another turn
                    }
                }).start()
            }


        }

        /** Zoom the mapview to a star (UID should be in View.tag) */
        fun panzoomToStar(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();


            val activity = view.context as Activity

            // Stars don't go away, so the below !! should be safe
            val star = vm.star(view.tag as Int)  // FIXME direct way?

            val imageView = activity.findViewById<MapView>(R.id.mapView)!!

            imageView.unpinPlanet()
            imageView.animateScaleAndCenter(
                imageView.zoomLevelStar, PointF(
                    star.loc.getLoc().x * imageView.uToS,
                    (star.loc.getLoc().y - 17f) * imageView.uToS
                )
            )!!
                .withDuration(1000)
                .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                .withInterruptible(false)
                .start()
        }

        /** Zoom the mapview to a planet (UID should be in View.tag) */
        fun panzoomToPlanet(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val activity = view.context as Activity

            // Planets don't go away, so the below !! should be safe
            val planet = vm.planet(view.tag as Int)

            val imageView = activity.findViewById<MapView>(R.id.mapView)!!  // FIXME direct way?

            imageView.pinPlanet(planet.uid)
            imageView.animateScaleAndCenter(
                imageView.zoomLevelPlanet, PointF(
                    planet.loc.getLoc().x * imageView.uToS,
                    (planet.loc.getLoc().y - 1) * imageView.uToS
                )
            )!!
                .withDuration(1000)
                .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                .withInterruptible(false)
                .start()
        }


        /** Called when the user taps the Go button */
        fun panzoomToShip(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val activity = view.context as Activity

            val ship = vm.ships[view.tag as Int] // Don't use ship(), as we need to handle null (do nothing)
            if (ship != null) {

                val imageView = activity.findViewById<MapView>(R.id.mapView)!!

                imageView.animateScaleAndCenter(
                    imageView.zoomLevelPlanet, PointF( // FEATURE Quality replace this with a constant from the view
                        ship.loc.getLoc().x * imageView.uToS,
                        (ship.loc.getLoc().y - 1f) * imageView.uToS
                    )
                )!!
                    .withDuration(100)
                    .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                    .withInterruptible(false)
                    .start()

            }
        }

        /** Called when the user taps a make Ship button */
        fun makeShip(view: View, type: Int) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val factory = vm.ships[view.tag as Int] // Don't use ship() as we need to handle null (do nothing)
            if (factory != null) {
                GBController.makeShip(factory.uid, type)
                updateActions(view)

                val message = "Ordered ship in Factory ${factory.name}."
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            }
        }

        /** Called when the user taps the Make Factory button */
        fun makeStructure(view: View, type: Int) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            // Planets don't go away, so the below !! should be safe
            val planet = vm.planet(view.tag as Int)

            // TODO Simplify (use .first) ? Or better, find Population and use planetOwner...
            GBController.makeStructure(planet.uid, uidActivePlayer, type)

            updateActions(view)

            val message = "Ordered Factory on " + planet.name
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        }


        fun updateActions(view: View) {
            if (vm.secondPlayer) {
                vm.playerTurns[uidActivePlayer]--
                actionsTaken.value = System.currentTimeMillis().toInt()

                if (vm.playerTurns[1 - uidActivePlayer] < 0) {
                    doUniverse(view, true)
                }

            } else {
                doUniverse(view, true)
            }

        }

        fun doubleClick(): Boolean {
            val elapsedTime = SystemClock.elapsedRealtime() - lastClickTime
            lastClickTime = SystemClock.elapsedRealtime();
            return (elapsedTime < clickDelay)
        }

        fun handleClickInSelectionActivity(button: View, buttons: List<Button>) {
            for (b in buttons) {
                b.background.colorFilter = LightingColorFilter(1, 0)
            }
            button.background.colorFilter = LightingColorFilter(0x55555, 0x774400)
        }


    }
}