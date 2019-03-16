package com.zwsi.gb.feature

import android.app.Activity
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gb.feature.GBViewModel.Companion.actionsTaken
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.currentGameFileName
import com.zwsi.gblib.GBUniverse
import java.io.File
import kotlin.system.measureNanoTime

// TODO rename this, once we know what all it does :-)
class GlobalStuff {

    companion object {

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<GBUniverse> = moshi.adapter(GBUniverse::class.java).indent("  ")
        var autoDo = false

        fun makeUniverse(@Suppress("UNUSED_PARAMETER") view: View, secondPlayer: Boolean) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            Toast.makeText(view.context, "Creating a new Universe", Toast.LENGTH_SHORT).show()

            Thread(Runnable {
                val json = GBController.makeAndSaveUniverse(secondPlayer)
                processGameInfo(json, true)
            }).start()
        }

        fun loadUniverse(view: View, number: Int) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            Toast.makeText(view.context, "Loading Universe ${number}", Toast.LENGTH_SHORT).show()

            val json = when (number) {
                11 -> view.context.resources.openRawResource(R.raw.mission1).reader().readText()
                12 -> view.context.resources.openRawResource(R.raw.mission2).reader().readText()
                13 -> view.context.resources.openRawResource(R.raw.mission3).reader().readText()
                21 -> view.context.resources.openRawResource(R.raw.map1).reader().readText()
                22 -> view.context.resources.openRawResource(R.raw.map2).reader().readText()
                23 -> view.context.resources.openRawResource(R.raw.map3).reader().readText()
                else -> File(view.context.filesDir, currentGameFileName).readText()
            }

            Thread(Runnable {
                GBController.loadUniverseFromJSON(json)  // SERVER Talk to not-remote server
                processGameInfo(json, true)
                // todo refresh main activity, because number of players may have changed
                // But Not needed as long as we set secondPlayer from the LoadActivity
            }).start()
        }


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

            actionsTaken.value = vm.playerTurns[0] + vm.playerTurns[1]

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

        // FIXME this is currently duplicated
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

        fun updateActions(view: View) {
            if (vm.secondPlayer) {
                vm.playerTurns[uidActivePlayer]--
                actionsTaken.value = vm.playerTurns[0] + vm.playerTurns[1]

                if (vm.playerTurns[1 - uidActivePlayer] < 0) {
                    doUniverse(view, true)
                }

            } else {
                doUniverse(view, true)
            }

        }

    }
}