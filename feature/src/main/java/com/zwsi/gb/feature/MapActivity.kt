package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.zwsi.gblib.GBController.Companion.universe
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBStar


class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val imageView = findViewById<MapView>(R.id.imageViewScale)!!

        val turnObserver = Observer<Int> { newTurn ->
            imageView.turn = newTurn; imageView.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        var myContext = this as Context

        // Gestures
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (imageView.isReady) {

                    val any = imageView.clickTarget(e)
                    if (any is GBPlanet) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = PlanetFragment.newInstance(any.uid.toString())
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment!!)
                        ft.commit()
                    } else if (any is GBStar) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = StarFragment.newInstance(any.uid.toString())
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment!!)
                        ft.commit()

                    } else {
                        val fragment = getSupportFragmentManager().findFragmentById(R.id.details)
                        if (fragment != null) {
                            val ft = getSupportFragmentManager().beginTransaction()
                            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            ft.remove(fragment!!)
                            ft.commit()
                        }
                    }

                }
                return true
            }
        })

//        imageView.setOnTouchListener(View.OnTouchListener {view, motionEvent ->
//            fun onTouch(view: View, motionEvent: MotionEvent) : Boolean {
//                return gestureDetector.onTouchEvent(motionEvent)
//            }
//        })

//        imageView.setOnTouchListener(object : View.OnTouchListener() {
//            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
//                return gestureDetector.onTouchEvent(motionEvent)
//            }
//        })

        imageView.setOnTouchListener { v: View, motionEvent: MotionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }


    }

    /** Called when the user taps the Do button */
    fun doUniverse(view: View) {
        GlobalButtonOnClick.doUniverse(view)
    }

    fun continuousDo(view: View) {
        GlobalButtonOnClick.toggleContinuous(view)
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

        val universe = universe
        universe.makeFactory(planet, GBViewModel.viewRaces[0])

        val message = "Ordered Factory on " + planet.name

        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

    }

    /** Called when the user taps the Go to Planets button */
    fun goToLocation(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val intent = Intent(this, PlanetsSlideActivity::class.java)

        val parent = view.parent.parent as View // TODO there must be a better (not layout dependent) way than this
        val star = parent.tag as GBStar

        Toast.makeText(view.context, "Going to planets of " + star.name, Toast.LENGTH_SHORT).show()

        val displayUID = ArrayList<Int>()
        for (planet in star.starPlanets) {
            displayUID.add(planet.uid)
        }
        intent.putExtra("planets", displayUID)
        intent.putExtra("title", "Planets of " + star.name)
        startActivity(intent)

    }

    /** Called when the user taps the Go to Planets button */
    fun goToShips(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val intent = Intent(this, ShipsSlideActivity::class.java)

        val parent = view.parent.parent as View // TODO there must be a better (not layout dependent) way than this
        val star = parent.tag as GBStar

        Toast.makeText(view.context, "Going to ships in system " + star.name, Toast.LENGTH_SHORT).show()

        val displayUID = ArrayList<Int>()
        for (ship in star.getStarShipsList()) {
            displayUID.add(ship.uid)
        }
        for (planet in star.starPlanets) {
            for (ship in planet.getOrbitShipsList()){
                displayUID.add(ship.uid)
            }
            for (ship in planet.getLandedShipsList()){
                displayUID.add(ship.uid)
            }
        }

        intent.putExtra("ships", displayUID)
        intent.putExtra("title", "Ships in " + star.name)
        startActivity(intent)

    }

}
