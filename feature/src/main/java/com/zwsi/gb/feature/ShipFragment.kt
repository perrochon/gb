package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zwsi.gb.feature.GBViewModel.Companion.viewStars
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.distance

class ShipFragment : Fragment() {

    companion object {

        fun newInstance(message: String): ShipFragment {
            return ShipFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_ship, container, false)!!

        setDetails(view)

        val sh = GBViewModel.viewShips[tag!!.toInt()]!!

        val turnObserver = Observer<Int> { _ ->
            setDetails(view)
            view.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val makePodButton: Button = view.findViewById(R.id.makePod)
        makePodButton.tag = sh.uid
        makePodButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makePod(it)
        })

        val makeCruiserButton: Button = view.findViewById(R.id.makeCruiser)
        makeCruiserButton.tag = sh.uid
        makeCruiserButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeCruiser(it)
        })

        val zoomButton: Button = view.findViewById(R.id.panzoomToShip)
        zoomButton.tag = sh.uid
        zoomButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.panzoomToShip(it)
        })

        val flyToButton: Button = view.findViewById(R.id.flyTo)
        flyToButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.flyTo(it)
        })

        setSpinner(view)

        return view
    }

    private fun setDetails(view: View) {

        val sh = GBViewModel.viewShips[tag!!.toInt()]

        val stats = view.findViewById<TextView>(R.id.ShipStats)
        val paint = stats.paint
        paint.textSize = 40f

        if (sh == null) {
            stats.setText("Boom! This ship no longer exists.")
        } else {
            stats.setText("Name: " + sh.name + "\n")
            stats.append("Type: " + sh.type + "\n")
            stats.append("Speed: " + sh.speed + "\n")
            stats.append("Health: " + sh.health + "\n")
            stats.append("Race: " + sh.race.name + "\n")
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

            val shipView = view.findViewById<ImageView>(R.id.ShipView)

            if (sh.idxtype == FACTORY) {
                shipView.setImageResource(R.drawable.factory)
                view.findViewById<Button>(R.id.makePod).setVisibility(View.VISIBLE)
                view.findViewById<Button>(R.id.makeCruiser).setVisibility(View.VISIBLE)
            } else if (sh.idxtype == POD) {
                if (sh.race.idx == 2) {
                    shipView.setImageResource(R.drawable.beetlepod)
                } else {
                    shipView.setImageResource(R.drawable.podt)
                }
            } else if (sh.idxtype == CRUISER) {
                shipView.setImageResource(R.drawable.cruisert)
            } else {
                shipView.setImageResource(R.drawable.yellow)
            }
        }

    }

    private fun setSpinner(view: View) {

        val sh = GBViewModel.viewShips[tag!!.toInt()]

        if (sh == null) {
            return
        } else {
            if (sh.speed == 0) {
                view.findViewById<Spinner>(R.id.spinner).setVisibility(View.GONE)
                view.findViewById<Button>(R.id.flyTo).setVisibility(View.GONE)
            } else {
                // TODO: better selection of possible targets once we have visibility.
                // Right now it's all insystem and first planet of each system outside.
                val destinationPlanets = arrayListOf<String>()

                if (sh.getStar() != null) {
                    for (p in GBViewModel.viewStarPlanets[sh.getStar()!!.uid]!!) {
                        destinationPlanets.add(p.name)
                    }
                }
                val sortedStars =
                    viewStars.toList().sortedBy { (_, s) ->
                        s.loc.getLoc().distance(sh.loc.getLoc())
                    }.take(6).drop(1)

                for ((_, s) in sortedStars) {
                    destinationPlanets.add(GBViewModel.viewStarPlanets[s.uid]!![0].name)
                }

                // Create an ArrayAdapter
                val adapter =
                    ArrayAdapter<String>(this.activity!!, android.R.layout.simple_spinner_item, destinationPlanets)

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Apply the adapter to the spinner
                val spinner = view.findViewById<Spinner>(R.id.spinner)
                spinner.adapter = adapter

                val flyButton = view.findViewById<Button>(R.id.flyTo)
                flyButton.setTag(R.id.TAG_FLYTO_SPINNER, spinner)
                flyButton.setTag(R.id.TAG_FLYTO_SHIP, sh.uid)

            }
        }

    }
}
