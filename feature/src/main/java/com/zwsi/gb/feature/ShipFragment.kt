package com.zwsi.gb.feature

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
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBData
import com.zwsi.gblib.GBData.Companion.BATTLESTAR
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.RESEARCH
import com.zwsi.gblib.GBData.Companion.SHUTTLE
import com.zwsi.gblib.GBData.Companion.STATION


class ShipFragment : Fragment() {

    companion object {

        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): ShipFragment {
            return ShipFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_ship, container, false)!!

        setDetails(view)

        val sh = vm.ship(tag!!.toInt())

        val background = view.background as GradientDrawable
        background.mutate()
        background.setStroke(2, vm.race(sh.uidRace).getColor())

        val shipView = view.findViewById<ImageView>(R.id.ShipView)
        shipView.setImageBitmap(sh.getBitmap())
        if (sh.idxtype == STATION) {
            // TODO Animate Station in ShipDetail. Redraw the image view on every vsync postInvalidateOnAnimation()

            val anim = RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            anim.interpolator = LinearInterpolator()
            anim.repeatCount = Animation.INFINITE
            anim.duration = 10000

            // Start animating the image
            shipView.startAnimation(anim)
        }

        val turnObserver = Observer<Int> { _ ->
            setDetails(view)
            view.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            setDetails(view)
            view.invalidate()
        }
        GBViewModel.actionsTaken.observe(this, actionObserver)

        val makePodButton: Button = view.findViewById(R.id.makePod)
        makePodButton.tag = sh.uid
        makePodButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, POD)
            vm.race(uidActivePlayer).money -= GBData.shipsData[POD]!!.cost
            setDetails(view)
            view.invalidate()
        })

        // FIXME Clean up this mess
        var makeCruiserButton: Button = view.findViewById(R.id.makeCruiser)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, CRUISER)
            vm.race(uidActivePlayer).money -= GBData.shipsData[CRUISER]!!.cost
            setDetails(view)
            view.invalidate()
        })

        makeCruiserButton = view.findViewById(R.id.makeShuttle)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, SHUTTLE)
            vm.race(uidActivePlayer).money -= GBData.shipsData[SHUTTLE]!!.cost
            setDetails(view)
            view.invalidate()
        })
        makeCruiserButton = view.findViewById(R.id.makeBattlestar)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, BATTLESTAR)
            vm.race(uidActivePlayer).money -= GBData.shipsData[BATTLESTAR]!!.cost
            setDetails(view)
            view.invalidate()
        })
        makeCruiserButton = view.findViewById(R.id.makeStation)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, STATION)
            vm.race(uidActivePlayer).money -= GBData.shipsData[STATION]!!.cost
            setDetails(view)
            view.invalidate()
        })
        makeCruiserButton = view.findViewById(R.id.makeResearch)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, RESEARCH)
            vm.race(uidActivePlayer).money -= GBData.shipsData[RESEARCH]!!.cost
            setDetails(view)
            view.invalidate()
        })

        val zoomButton: Button = view.findViewById(R.id.panzoomToShip)
        zoomButton.tag = sh.uid
        zoomButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.panzoomToShip(it)
        })

        val destinationButton: Button = view.findViewById(R.id.destination)
        destinationButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this.context, DestinationActivity::class.java)
            intent.putExtra("uidShip", sh.uid)
            startActivity(intent)
        })

        return view
    }


    // FIXME. Replace this with race.has(POD) and race.canafford(POD) then shipsdata.getbutton
    private val shipbuttons = intArrayOf(
        R.id.makePod, R.id.makeCruiser, R.id.makeShuttle, R.id.makeBattlestar, R.id.makeStation, R.id.makeResearch
    )
    private val shipcost = intArrayOf(
        GBData.shipsData[POD]!!.cost,
        GBData.shipsData[CRUISER]!!.cost,
        GBData.shipsData[SHUTTLE]!!.cost,
        GBData.shipsData[BATTLESTAR]!!.cost,
        GBData.shipsData[STATION]!!.cost,
        GBData.shipsData[RESEARCH]!!.cost
    )

    private fun setDetails(view: View) {

        val sh = vm.ships[tag!!.toInt()] // Don't use ship(), as we need to handle null here.

        val stats = view.findViewById<TextView>(R.id.ShipStats)
        val paint = stats.paint
        paint.textSize = 40f

        if (sh == null) {
            stats.setText("Boom! This ship no longer exists.")
        } else {

            stats.setText("Name: " + sh.name + "\n")
            stats.append("Type: " + sh.type + "\n")
            stats.append("Race: " + sh.race.name + "\n")
            stats.append("Speed: " + sh.speed + "\n")
            if (sh.guns > 0) {
                stats.append("Weapons: " + sh.guns + " ")
                stats.append("Damage: " + sh.damage + " ")
                stats.append("Range: " + sh.range + "\n")
            }
            stats.append("Health: " + sh.health + "\n")
            stats.append("Location: " + sh.loc.getLocDesc() + "\n")

            if (sh.uidRace == uidActivePlayer && sh.dest != null) stats.append("Destination: ${sh.dest?.getLocDesc()}\n")

            val shipRaceView = view.findViewById<ImageView>(R.id.ShipRaceView)
            shipRaceView.setImageResource(sh.race.getDrawableResource())

            val destinationButton = view.findViewById<Button>(R.id.destination)!!
            if (sh.uidRace != uidActivePlayer || sh.speed == 0) {
                destinationButton.visibility = View.GONE
            } else if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < GBViewModel.MIN_ACTIONS) {
                destinationButton.isEnabled = false
                destinationButton.alpha = 0.5f
            } else {
                destinationButton.isEnabled = true
                destinationButton.alpha = 1f // See Hack above.
            }

            if (sh.idxtype == FACTORY) {
                if (sh.uidRace == uidActivePlayer) {
                    for (i in 0..5) {
                        view.findViewById<Button>(shipbuttons[i]).visibility = View.VISIBLE
                    }
                    if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < GBViewModel.MIN_ACTIONS) {
                        for (i in 0..5) {
                            view.findViewById<Button>(shipbuttons[i]).isEnabled = false
                        }
                    } else {
                        for (i in 0..5) {
                            view.findViewById<Button>(shipbuttons[i]).isEnabled =
                                (shipcost[i] <= vm.race(uidActivePlayer).money)
                        }
                    }
                }
            }
        }
    }
}


