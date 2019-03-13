package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBData.Companion.BATTLESTAR
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.HEADQUARTER
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
        background.setStroke(2, Color.parseColor(vm.race(sh.uidRace).color))

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
        })

        // FIXME Clean up this mess
        var makeCruiserButton: Button = view.findViewById(R.id.makeCruiser)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, CRUISER)
        })

        makeCruiserButton = view.findViewById(R.id.makeShuttle)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, SHUTTLE)
        })
        makeCruiserButton = view.findViewById(R.id.makeBattlestar)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, BATTLESTAR)
        })
        makeCruiserButton = view.findViewById(R.id.makeStation)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeShip(it, STATION)
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
            if (sh.dest != null) stats.append("Destination: ${sh.dest?.getLocDesc()}\n")

            val shipRaceView = view.findViewById<ImageView>(R.id.ShipRaceView)

            when (sh.race.uid) {
                // FIXME if Ships only knew their drawable.. Fix here and in MapView.drawShip()
                0 -> shipRaceView.setImageResource((R.drawable.xenost))
                1 -> shipRaceView.setImageResource((R.drawable.impit))
                2 -> shipRaceView.setImageResource((R.drawable.beetle))
                3 -> shipRaceView.setImageResource((R.drawable.tortoise))
            }

            if (sh.uidRace != uidActivePlayer || sh.speed == 0) {
                view.findViewById<Button>(R.id.destination).setVisibility(View.GONE)
            }


            val shipView = view.findViewById<ImageView>(R.id.ShipView)

            if (sh.idxtype == FACTORY) {
                shipView.setImageResource(R.drawable.factory)
                if (sh.uidRace == uidActivePlayer) {
                    view.findViewById<Button>(R.id.makePod).setVisibility(View.VISIBLE)
                    view.findViewById<Button>(R.id.makeCruiser).setVisibility(View.VISIBLE)
                    view.findViewById<Button>(R.id.makeShuttle).setVisibility(View.VISIBLE)
                    view.findViewById<Button>(R.id.makeBattlestar).setVisibility(View.VISIBLE)
                    view.findViewById<Button>(R.id.makeStation).setVisibility(View.VISIBLE)
                    if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < GBViewModel.MIN_ACTIONS) {
                        view.findViewById<Button>(R.id.makePod).isEnabled = false
                        view.findViewById<Button>(R.id.makeCruiser).isEnabled = false
                        view.findViewById<Button>(R.id.makeShuttle).isEnabled = false
                        view.findViewById<Button>(R.id.makeBattlestar).isEnabled = false
                        view.findViewById<Button>(R.id.makeStation).isEnabled = false
                    } else {
                        view.findViewById<Button>(R.id.makePod).isEnabled = true
                        view.findViewById<Button>(R.id.makeCruiser).isEnabled = true
                        view.findViewById<Button>(R.id.makeShuttle).isEnabled = true
                        view.findViewById<Button>(R.id.makeBattlestar).isEnabled = true
                        view.findViewById<Button>(R.id.makeStation).isEnabled = true
                    }
                }
            } else if (sh.idxtype == POD) {
                if (sh.race.idx == 2) {
                    shipView.setImageResource(R.drawable.beetlepod)
                } else {
                    shipView.setImageResource(R.drawable.podt)
                }
            } else if (sh.idxtype == CRUISER) {
                shipView.setImageResource(R.drawable.cruisert)
            } else if (sh.idxtype == HEADQUARTER) {
                shipView.setImageResource(R.drawable.hq)
            } else if (sh.idxtype == RESEARCH) {
                shipView.setImageResource(R.drawable.research)
            } else if (sh.idxtype == SHUTTLE) {
                shipView.setImageResource(R.drawable.shuttle)
            } else if (sh.idxtype == BATTLESTAR) {
                shipView.setImageResource(R.drawable.battlestar)
            } else if (sh.idxtype == STATION) {
                shipView.setImageResource(R.drawable.wheel) // TODO Animate Bitmaps in Ship Fragment.
            } else {
                shipView.setImageResource(R.drawable.yellow)
            }

        }
    }
}

