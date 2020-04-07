package com.zwsi.ar.app

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBData
import com.zwsi.gblib.GBData.Companion.BATTLESTAR
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.RESEARCH
import com.zwsi.gblib.GBData.Companion.SHUTTLE
import com.zwsi.gblib.GBData.Companion.STATION
import kotlinx.android.synthetic.main.fragment_ship.*


class ARShipFragment : Fragment() {

    // FIXME. Replace this with race.has(POD) and race.canafford(POD) then shipsdata.getbutton
    private val shipButtons = arrayListOf<Button>()

    companion object {
        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): ARShipFragment {
            return ARShipFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ship, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val sh = vm.ship(tag!!.toInt())

        val background = view.background as GradientDrawable
        background.mutate()
        background.setStroke(2, vm.race(sh.uidRace).getColor())

        image_ship.setImageBitmap(sh.getBitmap())

        if (sh.idxtype == STATION) {
            // TODO Animate Station in ShipDetail. Redraw the image view on every vsync postInvalidateOnAnimation()

            val anim = RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            anim.interpolator = LinearInterpolator()
            anim.repeatCount = Animation.INFINITE
            anim.duration = 10000

            // Start animating the image
            image_ship.startAnimation(anim)
        }

        val turnObserver = Observer<Int> { _ ->
            setDetails()
            view.invalidate()
        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            setDetails()
            view.invalidate()
        }
        ARViewModel.actionsTaken.observe(this, actionObserver)

        // FIXME Make this a loop with programmatically created buttons, instead of copy paste
        button_pod.tag = sh.uid
        shipButtons.add(button_pod)
        button_pod.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, POD)
            vm.race(uidActivePlayer).money -= GBData.shipsData[POD]!!.cost
            setDetails()
            view.invalidate()
        })

        button_cruiser.tag = sh.uid
        shipButtons.add(button_cruiser)
        button_cruiser.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, CRUISER)
            vm.race(uidActivePlayer).money -= GBData.shipsData[CRUISER]!!.cost
            setDetails()
            view.invalidate()
        })

        button_shuttle.tag = sh.uid
        shipButtons.add(button_shuttle)
        button_shuttle.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, SHUTTLE)
            vm.race(uidActivePlayer).money -= GBData.shipsData[SHUTTLE]!!.cost
            setDetails()
            view.invalidate()
        })
        button_battlestar.tag = sh.uid
        shipButtons.add(button_battlestar)
        button_battlestar.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, BATTLESTAR)
            vm.race(uidActivePlayer).money -= GBData.shipsData[BATTLESTAR]!!.cost
            setDetails()
            view.invalidate()
        })
        button_station.tag = sh.uid
        shipButtons.add(button_station)
        button_station.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, STATION)
            vm.race(uidActivePlayer).money -= GBData.shipsData[STATION]!!.cost
            setDetails()
            view.invalidate()
        })
        button_research.tag = sh.uid
        shipButtons.add(button_research)
        button_research.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, RESEARCH)
            vm.race(uidActivePlayer).money -= GBData.shipsData[RESEARCH]!!.cost
            setDetails()
            view.invalidate()
        })

        button_to_ship.tag = sh.uid
        button_to_ship.setOnClickListener(View.OnClickListener {
            GlobalStuff.panZoomToShip(it)
        })

        button_destinations.setOnClickListener(View.OnClickListener {
            val intent = Intent(this.context, ARDestinationActivity::class.java)
            intent.putExtra("uidShip", sh.uid)
            startActivity(intent)
        })

        // TODO FIX is at the end because it relies on shipButtons arraylist hack
        setDetails()

    }


    private val shipcost = intArrayOf(
        GBData.shipsData[POD]!!.cost,
        GBData.shipsData[CRUISER]!!.cost,
        GBData.shipsData[SHUTTLE]!!.cost,
        GBData.shipsData[BATTLESTAR]!!.cost,
        GBData.shipsData[STATION]!!.cost,
        GBData.shipsData[RESEARCH]!!.cost
    )

    private fun setDetails() {

        val sh = vm.ships[tag!!.toInt()] // Don't use ship(), as we need to handle null here.

        val paint = text_ship_stats.paint
        paint.textSize = 40f

        if (sh == null) {
            text_ship_stats.text="Boom! This ship no longer exists."
        } else {

            text_ship_stats.text = "Name: " + sh.name + "\n"
            text_ship_stats.append("Type: " + sh.type + "\n")
            text_ship_stats.append("Race: " + sh.race.name + "\n")
            if (sh.speed > 0) {
                text_ship_stats.append("Speed: " + sh.speed + "\n")
                text_ship_stats.append("Hyperspeed: " + sh.hyperspeed + "\n")
            }
            if (sh.guns > 0) {
                text_ship_stats.append("Weapons: " + sh.guns + " ")
                text_ship_stats.append("Damage: " + sh.damage + " ")
                text_ship_stats.append("Range: " + sh.range + "\n")
            }
            text_ship_stats.append("Health: " + sh.health + "\n")
            text_ship_stats.append("Location: " + sh.loc.getLocDesc() + "\n")

            if (sh.uidRace == uidActivePlayer && sh.dest != null) text_ship_stats.append("Destination: ${sh.dest?.getLocDesc()}\n")

            image_ship_race.setImageResource(sh.race.getDrawableResource())

            if (sh.uidRace != uidActivePlayer || sh.speed == 0) {
                button_destinations.visibility = View.GONE
            } else if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < ARViewModel.MIN_ACTIONS) {
                button_destinations.isEnabled = false
                button_destinations.alpha = 0.5f
            } else {
                button_destinations.isEnabled = true
                button_destinations.alpha = 1f // See Hack above.
            }

            // TODO FIX Disabling of buttons no longer works with ArrayList
            if (sh.idxtype == FACTORY) {
                if (sh.uidRace == uidActivePlayer) {
                    for (i in 0..5) {
                        shipButtons[i].visibility = View.VISIBLE
                    }
                    if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < ARViewModel.MIN_ACTIONS) {
                        for (i in 0..5) {
                            shipButtons[i].isEnabled = false
                        }
                    } else {
                        for (i in 0..5) {
                            shipButtons[i].isEnabled =
                                (shipcost[i] <= vm.race(uidActivePlayer).money)
                        }
                    }
                }
            }
        }
    }
}


