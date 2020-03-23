package com.zwsi.gb.app

import android.arch.lifecycle.Observer
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.text.HtmlCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.zwsi.gb.app.GBViewModel.Companion.showContButton
import com.zwsi.gb.app.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.app.GBViewModel.Companion.vm
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBStar
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity() {

    private val BUNDLE_STATE = "ImageViewState"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Version TextView
        val version = findViewById<TextView>(R.id.version)
        version.setText(BuildConfig.VERSIONNAME)

        val doButton: Button = findViewById(R.id.DoButton)
        doButton.isEnabled = !vm.secondPlayer
        doButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.doUniverse(it)
        })

        val contButton: Button = findViewById(R.id.ContinuousButton)
        if (showContButton && !vm.secondPlayer) {
            contButton.visibility = View.VISIBLE
            contButton.setOnClickListener(View.OnClickListener
            {
                GlobalStuff.toggleContinuous(it)
            })
        } else {
            contButton.visibility = View.GONE
        }


        val helpButton: Button = findViewById(R.id.HelpButtonMap)
        helpButton.setOnClickListener(View.OnClickListener
        {
            val helpText = HtmlCompat.fromHtml(getString(R.string.maphelp), HtmlCompat.FROM_HTML_MODE_LEGACY)
            val helpView = TextView(this)
            helpView.setText(helpText)
            helpView.setMovementMethod(LinkMovementMethod.getInstance());
            helpView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
            val scroller = ScrollView(this)
            scroller.setPadding(10, 10, 10, 10)
            scroller.addView(helpView)

            val builder = AlertDialog.Builder(this, R.style.TutorialStyle)
            builder.setView(scroller)
                .setNeutralButton("OK", null)
                .show()
        })

        // Set up the action bar
        val actionBar = findViewById<LinearLayout>(R.id.actionBar)
//        val action1 = findViewById<TextView>(R.id.action1)
//        val action2 = findViewById<TextView>(R.id.action2)
//        val money1 = findViewById<TextView>(R.id.money1)
//        val money2 = findViewById<TextView>(R.id.money2)

        val imageView: MapView = findViewById<MapView>(R.id.mapView)!!

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
            val imageViewState = savedInstanceState.getSerializable(BUNDLE_STATE) as ImageViewState
            imageView.setScaleAndCenter(imageViewState.scale, imageViewState.center)
        }

        val turnObserver = Observer<Int> { newTurn ->
            setActionBar()
            actionBar.invalidate()
            imageView.turn = newTurn;
            imageView.shiftToPinnedPlanet()
            imageView.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            setActionBar()
            actionBar.invalidate()

            if (!vm.secondPlayer || vm.playerTurns[1 - uidActivePlayer] < 0) {
                DoButton.isEnabled = true
            } else {
                DoButton.isEnabled = false
            }

            if (showContButton) {
                contButton.visibility = View.VISIBLE
            } else {
                contButton.visibility = View.GONE
            }

        }
        GBViewModel.actionsTaken.observe(this, actionObserver)

        // Gestures
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            // Double Tap
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (imageView.isReady) {
                    val any = imageView.clickTarget(e)
                    if (any is GBPlanet) {  // FIXME Call GlobalStuff.panzomm methods with imageview...
                        imageView.pinPlanet(any.uid)
                        imageView.animateScaleAndCenter(
                            imageView.zoomLevelPlanet, PointF(
                                any.loc.getLoc().x * imageView.uToS,
                                (any.loc.getLoc().y - vm.planetOrbit) * imageView.uToS
                            )
                        )!!
                            .withDuration(1000)
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .withInterruptible(false)
                            .start()
                    } else if (any is GBStar) {
                        imageView.unpinPlanet()
                        imageView.animateScaleAndCenter(
                            imageView.zoomLevelStar, PointF( // FIXME replace this with a constant from the view
                                any.loc.getLoc().x * imageView.uToS,
                                (any.loc.getLoc().y - 17f) * imageView.uToS
                            )
                        )!!
                            .withDuration(1000)
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .withInterruptible(false)
                            .start()

                    } else if (any is GBShip) {
                        imageView.unpinPlanet()
                    } else {
                        return super.onDoubleTap(e)
                    }
                }
                return true
            }

            // Single Tap
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (imageView.isReady) {
                    val any = imageView.clickTarget(e)
                    if (any is GBPlanet) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = PlanetFragment.newInstance(any.uid.toString())
                        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment, any.uid.toString())
                        ft.commit()

                    } else if (any is GBStar) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = StarFragment.newInstance(any.uid.toString())
                        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment, any.uid.toString())
                        ft.commit()

                    } else if (any is GBShip) {
                        val ft = getSupportFragmentManager().beginTransaction()
                        val fragment = ShipFragment.newInstance(any.uid.toString())
                        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        ft.replace(R.id.details, fragment, any.uid.toString())
                        ft.commit()
                    } else {
                        val fragment = getSupportFragmentManager().findFragmentById(R.id.details)
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

        imageView.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }

    }

    public override fun onSaveInstanceState(outState: Bundle) {

        val imageView = findViewById<MapView>(R.id.mapView)!!
        val state = imageView.state
        if (state != null) {
            outState.putSerializable(BUNDLE_STATE, imageView.state)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setActionBar() {

        turns.text = "${vm.turn}"
        action1.text = "${vm.playerTurns[0]}"
        action2.text = "${vm.playerTurns[1]}"
        money1.text = "${vm.race(0).money}"
        money2.text = "${vm.races[1]?.money ?: 0}" // Mission 1 only has one race...
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
            image2.visibility = View.VISIBLE
            image1.visibility = View.VISIBLE
        } else {
            action1Area.visibility = View.INVISIBLE
            image1.visibility = View.VISIBLE
            money1Area.visibility = View.VISIBLE
            action2Area.visibility = View.INVISIBLE
            image2.visibility = View.INVISIBLE
            money2Area.visibility = View.INVISIBLE
        }
    }

    fun panzoomToShip(view: View) {
        GlobalStuff.panzoomToShip(view)
    }


}
