package com.zwsi.gb.feature

import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.os.SystemClock
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gb.feature.GBViewModel.Companion.viewPlanets
import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.lock
import com.zwsi.gblib.GBController.Companion.u

class GlobalButtonOnClick {

    companion object {

        fun doUniverse(view: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();


            //output.setText("") // TODO Refactor output back in...

            val message = "Executing Orders"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            Thread(Runnable {

                // Capture output from tester in an byte array
//            val baos = ByteArrayOutputStream()
//            val ps = PrintStream(baos)
//            System.setOut(ps)

                if (GBController.u.autoDo) { // If we are running on autok, don't add extra do's
                    return@Runnable
                }

                GBController.doUniverse()

                view.post {
                    GBViewModel.update()
                }

//            System.out.flush()

//            view.post { // This is going to the button's UI thread, which is the same as the ScrollView
//                // output.append(baos.toString())
//            }

                view.post {
                    // Worth making a string in this thread and post just result?
                    for (s in GBController.u.news)
                    //output.append(s)

                        MissionController.checkMissionStatus()
                    //output.append(MissionController.getCurrentMission(this))
                }

            }).start()

        }

        fun toggleContinuous(view: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val message = "God Mode: Continuous Do"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            if (GBController.u.autoDo) {
                GBController.u.autoDo = false
            } else {
                GBController.u.autoDo = true
                Thread(Runnable {

                    while (GBController.u.autoDo) {
                        Thread.sleep(333)
                        GBController.doUniverse()

                        view.post {
                            GBViewModel.update()
                        }
                    }
                }).start()
            }
        }


        // FIXME this is currently duplicated
        /** Called when the user taps the Make Pod button */
        fun makeFactory(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();


            val activity = view.context as Activity
            val planetFragment = activity.findViewById<View>(R.id.PlanetFragment)
            val planet: GBPlanet = planetFragment.tag as GBPlanet

            val universe = GBController.u
            universe.makeFactory(
                planet,
                GBViewModel.viewRaces.toList().component1().second
            ) // TODO Find Population and use planetOwner...

            val message = "Ordered Factory on " + planet.name

            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        }

        /** Called when the user taps the (+) button on Star Fragment */
        fun panzoomToStar(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            //val intent = Intent(view.context, PlanetsSlideActivity::class.java)

            val activity = view.context as Activity
            val starFragment = activity.findViewById<View>(R.id.StarFragment)
            val star = starFragment.tag as GBStar

            val imageView = activity.findViewById<MapView>(R.id.mapView)!!

            imageView.animateScaleAndCenter(
                imageView.zoomLevelStar, PointF( // FIXME replace this with a constant from the view
                    star.loc.getLoc().x * imageView.uToS,
                    (star.loc.getLoc().y - 17f) * imageView.uToS
                )
            )!!
                .withDuration(500)
                .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                .withInterruptible(false)
                .start()
        }

        /** Called when the user taps the (+) button on Star Fragment */
        fun panzoomToPlanet(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            //val intent = Intent(view.context, PlanetsSlideActivity::class.java)

            val activity = view.context as Activity
            val planetFragment = activity.findViewById<View>(R.id.PlanetFragment)
            val planet = planetFragment.tag as GBPlanet

            val imageView = activity.findViewById<MapView>(R.id.mapView)!!

            imageView.animateScaleAndCenter(
                imageView.zoomLevelPlanet, PointF(
                    planet.loc.getLoc().x * imageView.uToS,
                    (planet.loc.getLoc().y - 1) * imageView.uToS
                )
            )!!
                .withDuration(500)
                .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                .withInterruptible(false)
                .start()
        }

        /** Called when the user taps the * button on Star Fragment */
        fun panzoomToSystemStar(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val activity = view.context as Activity
            val planetFragment = activity.findViewById<View>(R.id.PlanetFragment)
            val planet = planetFragment.tag as GBPlanet

            val imageView = activity.findViewById<MapView>(R.id.mapView)!!

            imageView.animateScaleAndCenter(
                imageView.zoomLevelStar, PointF( // FIXME replace this with a constant from the view
                    u.star(planet.uidStar).loc.getLoc().x * imageView.uToS,
                    (u.star(planet.uidStar).loc.getLoc().y - 17f) * imageView.uToS
                )
            )!!
                .withDuration(500)
                .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                .withInterruptible(false)
                .start()


        }


        /** Called when the user taps the Go to Planets button */
        fun goToShips(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val intent = Intent(view.context, ShipsSlideActivity::class.java)

            val parent = view.parent.parent as View // TODO there must be a better (not layout dependent) way than this
            val star = parent.tag as GBStar

            Toast.makeText(view.context, "Going to ships in system " + star.name, Toast.LENGTH_SHORT).show()

            val displayUID = ArrayList<Int>()
            for (ship in star.starShipList) {
                displayUID.add(ship.uid)
            }
            for (planet in star.starPlanetsList) {
                for (ship in planet.orbitShips) {
                    displayUID.add(ship.uid)
                }
                for (ship in planet.landedShips) {
                    displayUID.add(ship.uid)
                }
            }

            intent.putExtra("ships", displayUID)
            intent.putExtra("title", "Ships in " + star.name)
            view.context.startActivity(intent)

        }

        /** Called when the user taps the Go button */
        fun panzoomToShip(view: View) {

            val ship = view.tag as GBShip

            val activity = view.context as Activity
//            val planetFragment = activity.findViewById<View>(R.id.PlanetFragment)
//            val planet = planetFragment.tag as GBPlanet

            val imageView = activity.findViewById<MapView>(R.id.mapView)!!

            imageView.animateScaleAndCenter(
                imageView.zoomLevelPlanet, PointF( // FEATURE Quality replace this with a constant from the view
                    ship.loc.getLoc().x * imageView.uToS,
                    (ship.loc.getLoc().y - 1f) * imageView.uToS
                )
            )!!
                .withDuration(500)
                .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                .withInterruptible(false)
                .start()


        }

        /** Called when the user taps the make Pod button */
        fun makePod(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val ship = view.tag as GBShip

            val message = "Ordered Pod in Factory " + ship.name
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            lock.lock(); // makePod
            try {
                GBController.u.makePod(ship)
            } finally {
                lock.unlock()
            }

        }

        /** Called when the user taps the make Cruiser button */
        fun makeCruiser(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val ship = view.tag as GBShip

            val message = "Ordered Cruiser in Factory " + ship.name
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            lock.lock(); // makeCruiser
            try {
                GBController.u.makeCruiser(ship)
            } finally {
                lock.unlock()
            }

        }

        /** Called when the user taps the fly  To button */
        fun flyTo(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val ship = view.getTag(R.id.TAG_FLYTO_SHIP) as GBShip // TODO Hardcoded keys, what can go wrong

            val spinner = view.getTag(R.id.TAG_FLYTO_SPINNER) as Spinner
            val destination = spinner.selectedItem.toString()
            var planet: GBPlanet? = null

            for ((_, p) in viewPlanets) { // TODO this is wasteful. Need to refactor to locations
                if (p.name == destination)
                    planet = p
            }

            Toast.makeText(view.context, "Ordered " + ship.name + " to fly to " + planet!!.name, Toast.LENGTH_SHORT)
                .show()

            lock.lock(); // FlyTo
            try {
                if (ship.idxtype == GBData.POD) {
                    GBController.u.flyShipLanded(ship, planet)
                } else {
                    GBController.u.flyShipOrbit(ship, planet)
                }
            } finally {
                lock.unlock()
            }
        }


    }
}