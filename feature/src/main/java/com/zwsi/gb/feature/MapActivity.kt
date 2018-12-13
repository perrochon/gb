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
import android.widget.Spinner
import android.widget.Toast
import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.universe


class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val imageView = findViewById<MapView>(R.id.mapView)!!

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
                    } else if (any is GBShip) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = ShipFragment.newInstance(any.uid.toString())
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


    fun makeFactory(view:View) {
        GlobalButtonOnClick.makeFactory(view)
    }

    fun goToLocation(view:View) {
        GlobalButtonOnClick.goToLocation(view)
    }

    fun goToShips(view:View) {
        GlobalButtonOnClick.goToShips(view)
    }

    fun goToLocationShip(view: View) {
        GlobalButtonOnClick.goToLocationShip(view)
    }

    fun makePod(view: View) {
        GlobalButtonOnClick.makePod(view)
    }

    fun makeCruiser(view: View) {
        GlobalButtonOnClick.makeCruiser(view)
    }

    /** Called when the user taps the fly  To button */
    fun flyTo(view: View) {
        GlobalButtonOnClick.flyTo(view)
    }

}
