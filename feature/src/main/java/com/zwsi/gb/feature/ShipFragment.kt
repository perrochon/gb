package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zwsi.gblib.GBController.Companion.u
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD

class ShipFragment : Fragment() {

    companion object {

        fun newInstance(message: String): ShipFragment {

            val f = ShipFragment()

            val bdl = Bundle(1)

            bdl.putString("UID", message)

            f.setArguments(bdl)

            return f

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View? = inflater.inflate(R.layout.fragment_ship, container, false);

        // What is this fragment about, and make sure the fragment remembers
        val uid = arguments!!.getString("UID")!!.toInt()
        val sh = GBViewModel.viewShips[uid]

        if (sh != null) {


            view!!.tag = sh

            val shipView = view.findViewById<ImageView>(R.id.ShipView)
            val shipRaceView = view.findViewById<ImageView>(R.id.ShipRaceView)

            if (sh.idxtype == FACTORY) {
                shipView.setImageResource(R.drawable.factory)

                view.findViewById<Button>(R.id.makePod).setVisibility(View.VISIBLE)
                view.findViewById<Button>(R.id.makeCruiser).setVisibility(View.VISIBLE)

            } else if (sh.idxtype == POD) {
                if (sh.race.uid == 2) {
                    shipView.setImageResource(R.drawable.beetlepod)
                } else {
                    shipView.setImageResource(R.drawable.podt)
                }
            } else if (sh.idxtype == CRUISER) {
                shipView.setImageResource(R.drawable.cruisert)
            } else
                shipView.setImageResource(R.drawable.yellow)

            when (sh.race.uid) {
                // FIXME if Ships only knew their drawable.. Fix here and in MapView.drawShip()
                0 -> shipRaceView.setImageResource((R.drawable.xenost))
                1 -> shipRaceView.setImageResource((R.drawable.impit))
                2 -> shipRaceView.setImageResource((R.drawable.beetle))
                3 -> shipRaceView.setImageResource((R.drawable.tortoise))
            }

            var stats = view.findViewById<TextView>(R.id.ShipStats)
            var paint = stats.paint
            paint.textSize = 40f

            stats.append("\n")
            stats.append("Name: " + sh.name + "\n")
            stats.append("Type: " + sh.type + "\n")
            stats.append("Speed: " + sh.speed + "\n")
            stats.append("Health: " + sh.health+ "\n")
            stats.append("Race: " + sh.race.name + "\n")
            stats.append("Location: " + sh.loc.getLocDesc() + "\n")
            if (sh.dest != null) stats.append("Destination: "+ sh.dest?.getLocDesc())

            stats = view.findViewById<TextView>(R.id.ShipBackground)
            paint = stats.paint
            paint.textSize = 40f

            stats.setText("Lorem ipsum")

            stats.append("\n")

            stats.append("UID:" + sh.uid + " | ")
            stats.append("idxt:" + sh.idxtype + " | ")
            stats.append("loca:" + sh.loc.level + "." + sh.loc.uidRef)


            if (sh.speed == 0) {
                view.findViewById<Spinner>(R.id.spinner).setVisibility(View.GONE)
                view.findViewById<Button>(R.id.flyTo).setVisibility(View.GONE)
            }

            // TODO: better selection of possible targets once we have visibility. Right now it's all insystem
            // and first planet of each system outside.
            var destinationPlanets = arrayListOf<String>()
            if (sh.getStar() != null) {
                for (p in sh.getStar()!!.starPlanetsList) {
                    destinationPlanets.add(p.name)
                }
            }
            for ((_, s) in u.allStars) {
                destinationPlanets.add(s.starPlanetsList[0].name)
            }

            // Create an ArrayAdapter
            val adapter =
                ArrayAdapter<String>(this.activity!!, android.R.layout.simple_spinner_item, destinationPlanets)

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            var spinner = view.findViewById<Spinner>(R.id.spinner)
            spinner.adapter = adapter

            val flyButton = view.findViewById<Button>(R.id.flyTo)
            flyButton.setTag(R.id.TAG_FLYTO_SPINNER,spinner)
            flyButton.setTag(R.id.TAG_FLYTO_SHIP, sh)

            // FIXME could just tag the fragment, instead of every button. Like planet and star
            view.findViewById<Button>(R.id.panzoomToShip).tag=sh
            view.findViewById<Button>(R.id.makePod).tag=sh
            view.findViewById<Button>(R.id.makeCruiser).tag=sh

        }
        return view
    }
}
