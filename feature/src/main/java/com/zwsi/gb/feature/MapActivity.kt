package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gblib.GBData
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBStar


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

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (imageView.isReady) {
                    val any = imageView.clickTarget(e)
                    if (any is GBPlanet) {
                        imageView.animateScaleAndCenter(
                            imageView.zoomLevelPlanet, PointF(
                                any.loc.getLoc().x * imageView.uToS,
                                (any.loc.getLoc().y - 1f ) * imageView.uToS
                            )
                        )!!
                            .withDuration(500)
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .withInterruptible(false)
                            .start()
                    } else if (any is GBStar) {
                        imageView.animateScaleAndCenter(
                            imageView.zoomLevelStar, PointF( // TODO Quality replace this with a constant from the view
                                any.loc.getLoc().x * imageView.uToS,
                                (any.loc.getLoc().y - 17f ) * imageView.uToS
                            )
                        )!!
                            .withDuration(500)
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .withInterruptible(false)
                            .start()


                    } else if (any is GBShip) {
                    } else {
                        return super.onDoubleTap(e)
                    }
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (imageView.isReady) {
                    val any = imageView.clickTarget(e)
                    if (any is GBPlanet) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = PlanetFragment.newInstance(any.uid.toString())
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment)
                        ft.commit()

                    } else if (any is GBStar) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = StarFragment.newInstance(any.uid.toString())
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment)
                        ft.commit()

                    } else if (any is GBShip) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = ShipFragment.newInstance(any.uid.toString())
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment)
                        ft.commit()
                    } else {
                        val fragment = getSupportFragmentManager().findFragmentById(R.id.details)
                        if (fragment != null) {
                            val ft = getSupportFragmentManager().beginTransaction()
                            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            ft.remove(fragment)
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


    fun makeFactory(view: View) {
        GlobalButtonOnClick.makeFactory(view)
    }

    fun panzoomToStar(view: View) {
        GlobalButtonOnClick.panzoomToStar(view)
    }

    fun panzoomToPlanet(view: View) {
        GlobalButtonOnClick.panzoomToPlanet(view)
    }

    fun panzoomToSystemStar(view: View) {
        GlobalButtonOnClick.panzoomToSystemStar(view)
    }

    fun panzoomToShip(view: View) {
        GlobalButtonOnClick.panzoomToShip(view)
    }

//    fun goToLocationShip(view: View) {
//        GlobalButtonOnClick.goToLocationShip(view)
//    }

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
