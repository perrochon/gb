package com.zwsi.gb.feature

import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBStar

class GlobalButtonOnClick {

    companion object {

        fun doUniverse(view: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();


            //output.setText("") // TODO: Refactor output back in...

            val message = "Executing Orders"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            Thread(Runnable {

                // Capture output from tester in an byte array
//            val baos = ByteArrayOutputStream()
//            val ps = PrintStream(baos)
//            System.setOut(ps)

                if (GBController.universe.autoDo) { // If we are running on autok, don't add extra do's
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
                    for (s in GBController.universe.news)
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

            if (GBController.universe.autoDo) {
                GBController.universe.autoDo = false
            } else {
                GBController.universe.autoDo = true
                Thread(Runnable {

                    while (GBController.universe.autoDo) {
                        Thread.sleep(200)
                        GBController.doUniverse()

                        view.post {
                            GBViewModel.update()
                        }
                    }
                }).start()
            }
        }


        // TODO QUALITY this is currently duplicated
        /** Called when the user taps the Make Pod button */
        fun makeFactory(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();


            val parent = view.parent as View
            val planet: GBPlanet = parent.tag as GBPlanet

            val universe = GBController.universe
            universe.makeFactory(planet, GBViewModel.viewRaces[0])

            val message = "Ordered Factory on " + planet.name

            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        }

        /** Called when the user taps the Go to Planets button */
        fun goToLocation(view: View) {

            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            val intent = Intent(view.context, PlanetsSlideActivity::class.java)

            val parent = view.parent.parent as View // TODO there must be a better (not layout dependent) way than this
            val star = parent.tag as GBStar

            Toast.makeText(view.context, "Going to planets of " + star.name, Toast.LENGTH_SHORT).show()

            val displayUID = ArrayList<Int>()
            for (planet in star.starPlanets) {
                displayUID.add(planet.uid)
            }
            intent.putExtra("planets", displayUID)
            intent.putExtra("title", "Planets of " + star.name)
            view.context.startActivity(intent)

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
            for (ship in star.getStarShipsList()) {
                displayUID.add(ship.uid)
            }
            for (planet in star.starPlanets) {
                for (ship in planet.getOrbitShipsList()) {
                    displayUID.add(ship.uid)
                }
                for (ship in planet.getLandedShipsList()) {
                    displayUID.add(ship.uid)
                }
            }

            intent.putExtra("ships", displayUID)
            intent.putExtra("title", "Ships in " + star.name)
            view.context.startActivity(intent)

        }

    }
}