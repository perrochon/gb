package com.zwsi.ar.app

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.ar.app.ARViewModel.Companion.showContButton
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBStar
import kotlinx.android.synthetic.main.activity_map.*

class ARMapActivity : AppCompatActivity() {

    private val BUNDLE_STATE = "ImageViewState"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        text_version.text = BuildConfig.VERSIONNAME

        button_do.isEnabled = !vm.secondPlayer
        button_do.setOnClickListener(View.OnClickListener {
            GlobalStuff.doUniverse(it)
        })

        if (showContButton && !vm.secondPlayer) {
            button_continuous.visibility = View.VISIBLE
            button_continuous.setOnClickListener(View.OnClickListener
            {
                GlobalStuff.toggleContinuous(it)
            })
        } else {
            button_continuous.visibility = View.GONE
        }

        button_help.setOnClickListener(View.OnClickListener {
            if (!GlobalStuff.doubleClick()) {
                val intent = Intent(this, ARHelpActivity::class.java)
                val b = Bundle()
                b.putString("url", "file:///android_asset/mapHelp.html")
                intent.putExtras(b)
                startActivity(intent)
            }
        })

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
            val imageViewState = savedInstanceState.getSerializable(BUNDLE_STATE) as ImageViewState
            map_view.setScaleAndCenter(imageViewState.scale, imageViewState.center)
        }

        val turnObserver = Observer<Int> { newTurn ->
            setActionBar()
            layout_status_bar.invalidate()
            map_view.turn = newTurn;
            map_view.shiftToPinnedPlanet()
            map_view.invalidate()
        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            setActionBar()
            layout_status_bar.invalidate()

            if (!vm.secondPlayer || vm.playerTurns[1 - uidActivePlayer] < 0) {
                button_do.isEnabled = true
            } else {
                button_do.isEnabled = false
            }

            if (showContButton) {
                button_continuous.visibility = View.VISIBLE
            } else {
                button_continuous.visibility = View.GONE
            }

        }
        ARViewModel.actionsTaken.observe(this, actionObserver)

        // Gestures
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            // Double Tap
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (map_view.isReady) {
                    val any = map_view.clickTarget(e)
                    if (any is GBPlanet) {  // FIXME Call GlobalStuff.panzomm methods with imageview...
                        map_view.pinPlanet(any.uid)
                        map_view.animateScaleAndCenter(
                            map_view.zoomLevelPlanet, PointF(
                                any.loc.getLoc().x * map_view.uToS,
                                (any.loc.getLoc().y - vm.planetOrbit) * map_view.uToS
                            )
                        )!!
                            .withDuration(1000)
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .withInterruptible(false)
                            .start()
                    } else if (any is GBStar) {
                        map_view.unpinPlanet()
                        map_view.animateScaleAndCenter(
                            map_view.zoomLevelStar, PointF( // FIXME replace this with a constant from the view
                                any.loc.getLoc().x * map_view.uToS,
                                (any.loc.getLoc().y - 17f) * map_view.uToS
                            )
                        )!!
                            .withDuration(1000)
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .withInterruptible(false)
                            .start()

                    } else if (any is GBShip) {
                        map_view.unpinPlanet()
                    } else {
                        return super.onDoubleTap(e)
                    }
                }
                return true
            }

            // Single Tap
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (map_view.isReady) {
                    val any = map_view.clickTarget(e)
                    if (any is GBPlanet) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = ARPlanetFragment.newInstance(any.uid.toString())
                        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.layout_do, fragment, any.uid.toString())
                        ft.commit()

                    } else if (any is GBStar) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = ARStarFragment.newInstance(any.uid.toString())
                        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.layout_do, fragment, any.uid.toString())
                        ft.commit()

                    } else if (any is GBShip) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = ARShipFragment.newInstance(any.uid.toString())
                        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.layout_do, fragment, any.uid.toString())
                        ft.commit()
                    } else {
                        val fragment = getSupportFragmentManager().findFragmentById(R.id.layout_do)
                        if (fragment != null) {
                            val ft = getSupportFragmentManager().beginTransaction()
                            //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            ft.remove(fragment)
                            ft.commit()
                        }
                    }
                }
                return true
            }
        })

//        map_view.setOnTouchListener(View.OnTouchListener {view, motionEvent ->
//            fun onTouch(view: View, motionEvent: MotionEvent) : Boolean {
//                return gestureDetector.onTouchEvent(motionEvent)
//            }
//        })

//        map_view.setOnTouchListener(object : View.OnTouchListener() {
//            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
//                return gestureDetector.onTouchEvent(motionEvent)
//            }
//        })

        map_view.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }

    }

    public override fun onSaveInstanceState(outState: Bundle) {

        val state = map_view.state
        if (state != null) {
            outState.putSerializable(BUNDLE_STATE, map_view.state)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setActionBar() {

        text_turns.text = "${vm.turn}"
        text_action1.text = "${vm.playerTurns[0]}"
        text_action2.text = "${vm.playerTurns[1]}"
        text_money1.text = "${vm.race(0).money}"
        text_money2.text = "${vm.races[1]?.money ?: 0}" // Mission 1 only has one race...
        if (vm.secondPlayer) {
            if (uidActivePlayer == 0) {
                action1Area.visibility = View.VISIBLE
                money1Area.visibility = View.VISIBLE
                action2Area.visibility = View.VISIBLE
                money2Area.visibility = View.INVISIBLE
            } else {
                action1Area.visibility = View.VISIBLE
                money1Area.visibility = View.INVISIBLE
                action2Area.visibility = View.VISIBLE
                money2Area.visibility = View.VISIBLE
            }
            image_player2.visibility = View.VISIBLE
            image_player1.visibility = View.VISIBLE
        } else {
            action1Area.visibility = View.INVISIBLE
            image_player1.visibility = View.VISIBLE
            money1Area.visibility = View.VISIBLE
            action2Area.visibility = View.INVISIBLE
            image_player2.visibility = View.INVISIBLE
            money2Area.visibility = View.INVISIBLE
        }
    }

    fun panzoomToShip(view: View) {
        GlobalStuff.panzoomToShip(view)
    }

}
