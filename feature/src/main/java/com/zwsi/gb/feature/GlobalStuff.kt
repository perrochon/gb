package com.zwsi.gb.feature

import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBController.Companion.lock
import com.zwsi.gblib.GBSavedGame
import java.io.File
import kotlin.system.measureNanoTime

// TODO rename this, once we know what all it does :-)
class GlobalStuff {

    companion object {

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<GBSavedGame> = moshi.adapter(GBSavedGame::class.java).indent("  ")
        var autoDo = false

        // We need the application context to write to a file
        fun makeUniverse(context: Context) {
            Thread(Runnable {
                GBController.currentFilePath = context.filesDir // FIXME PERSISTENCE This is duplicated.
                val json = GBController.makeUniverse()  // SERVER Talk to not-remote server
                processGameInfo(json)
            }).start()
        }

        // FIXME We have two makeUniverse...
        fun makeUniverse(view: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

//            val messageBox: TextView = view.findViewById<TextView>(R.id.messageBox)!!
//            messageBox.setText("")

            val message = "Creating a new Universe"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            makeUniverse(view.context.applicationContext)
        }

        fun loadUniverse(view: View, number: Int) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

//            val messageBox: TextView = view.findViewById<TextView>(R.id.messageBox)!!
//            messageBox.setText("")

            var message = "Loading Universe ${number}"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            val json = when (number) {
                0 -> File(view.context.filesDir, "CurrentGame.json").readText()
                1 -> view.context.resources.openRawResource(R.raw.mission1).reader().readText()
                2 -> view.context.resources.openRawResource(R.raw.mission2).reader().readText()
                else -> view.context.resources.openRawResource(R.raw.mission3).reader().readText()
            }

            if (json == "") {
                message = "Universe Not Found"
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
            }
            Thread(Runnable {
                GBController.currentFilePath = view.context.filesDir
                GBController.loadUniverse(json)  // SERVER Talk to not-remote server
                processGameInfo(json)
            }).start()
        }


        // Common code once we have a JSON, from makeUniverse, do Universe, and eventually load
        fun processGameInfo(json: String) {

            // FIXME PERSISTENCE: We save in controller, because only controller finds a writable directory...

            // FYI only. This writes (on my setup) to  /data/data/com.zwsi.gb.app/files/CurrentGame.json
//            val writeFileTime = measureNanoTime {
//                File(context.filesDir, "CurrentGame.json").writeText(json)
//            }

            // We create gameinfo in the worker thread, not the UI thread
            var gameInfo: GBSavedGame? = null
            val fromJsonTime = measureNanoTime {
                gameInfo = jsonAdapter.lenient().fromJson(json)!!
            }

            // FIXME PERSISTENCE TIMING TRACKING
            Handler(Looper.getMainLooper()).post({
                GBViewModel.update(gameInfo!!, GBController.elapsedTimeLastUpdate, 12345, fromJsonTime)
            })
        }

        fun doUniverse(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            if (autoDo) { // If we are running on auto, ignore manual Do
                return
            }

            val message = "Executing Orders"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            Thread(Runnable {
                GBController.currentFilePath = view.context.filesDir
                val json = GBController.doUniverse() // SERVER Talk to not-remote server
                processGameInfo(json)
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
                        val json = GBController.doUniverse() // SERVER Talk to not-remote server
                        processGameInfo(json)
                        Thread.sleep(333) // let everything else catch up before we do another turn
                    }
                }).start()
            }


        }

        // FIXME this is currently duplicated
        /** Called when the user taps the Make Factory button */
        fun makeFactory(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            // Planets don't go away, so the below !! should be safe
            val planet = GBViewModel.viewPlanets[view.tag]!!

            // TODO Simplify (use .first) ? Or better, find Population and use planetOwner...
            GBController.makeFactory(planet.uid, GBViewModel.viewRaces.toList().component1().second.uid)

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
            val star = GBViewModel.viewStars[view.tag]!!  // FIXME direct way?

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
            val planet = GBViewModel.viewPlanets[view.tag]!!

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

            val ship = GBViewModel.viewShips[view.tag]
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

        /** Called when the user taps the make Pod button */
        fun makePod(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val factory = GBViewModel.viewShips[view.tag]
            if (factory != null) {

                val message = "Ordered Pod in Factory " + factory.name
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

                lock.lock(); // makePod
                try {
                    GBController.makePod(factory.uid)
                } finally {
                    lock.unlock()
                }
            }
        }

        /** Called when the user taps the make Cruiser button */
        fun makeCruiser(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val factory = GBViewModel.viewShips[view.tag]
            if (factory != null) {


                val message = "Ordered Cruiser in Factory " + factory.name
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

                lock.lock(); // makeCruiser
                try {
                    GBController.makeCruiser(factory.uid)
                } finally {
                    lock.unlock()
                }
            }
        }

//        /** Called when the user taps the fly  To button */
//        fun flyTo(view: View) {
//
//            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
//                return;
//            }
//            lastClickTime = SystemClock.elapsedRealtime();
//
//            // Stars don't go away, so the below !! should be safe
//            val ship = GBViewModel.viewShips[view.getTag(R.id.TAG_FLYTO_SHIP)]!!
//
//            //val ship = view.getTag(R.id.TAG_FLYTO_SHIP) as GBShip // FIXME  Hardcoded keys, what can go wrong
//
//            val spinner = view.getTag(R.id.TAG_FLYTO_SPINNER) as Spinner
//            val destination = spinner.selectedItem.toString()
//
//            val destinationUids = view.getTag(R.id.TAG_FLYTO_UIDS) as HashMap<String, Int>
//            val uidPlanet = destinationUids[destination]
//
//            val planet = viewPlanets[uidPlanet]!!
//
//            lock.lock(); // FlyTo
//            try {
//                if (ship.idxtype == GBData.POD) {
//                    GBController.flyShipLanded(ship, planet)
//                } else {
//                    GBController.flyShipOrbit(ship, planet)
//                }
//            } finally {
//                lock.unlock()
//            }
//        }


    }
}