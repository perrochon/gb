package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP

import com.zwsi.gblib.GBController.Companion.universe
import android.text.method.Touch.onTouchEvent
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import com.zwsi.gblib.GBPlanet


class MapActivity : AppCompatActivity() {

    var lastFragment : Fragment? = null //doing this because isInitialized didn't work

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val imageView = findViewById<MapView>(R.id.imageViewScale)!!

        val turnObserver = Observer<Int> { newTurn ->
            imageView.turn = newTurn; imageView.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.curentTurn.observe(this, turnObserver)

        var myContext = this as Context

        // Gestures
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (imageView.isReady) {

                    val any = imageView.clickTarget(e)
                    if (any is GBPlanet) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        lastFragment = PlanetFragment.newInstance(any.uid.toString())
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, lastFragment!!)
                        ft.commit()

                    } else if (lastFragment != null) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.remove(lastFragment!!)
                        ft.commit()
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

        imageView.setOnTouchListener {v: View, motionEvent: MotionEvent ->
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

        if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay){
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

}
