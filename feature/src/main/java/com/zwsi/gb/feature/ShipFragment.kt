package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.*
import com.zwsi.gblib.GBController.Companion.flyShipLanded
import com.zwsi.gblib.GBController.Companion.flyShipOrbit
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.POD
import kotlinx.android.synthetic.main.activity_map.*
import kotlin.math.PI


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

        val actionObserver = Observer<Int> { _->
            setDetails(view)
            view.invalidate()
        }
        GBViewModel.actionsTaken.observe(this,actionObserver)


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

        setSpinner(view, this)

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
                if (sh.uidRace == uidActivePlayer) {
                    view.findViewById<Button>(R.id.makePod).setVisibility(View.VISIBLE)
                    view.findViewById<Button>(R.id.makeCruiser).setVisibility(View.VISIBLE)
                }
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

    private fun setSpinner(view: View, fragment: ShipFragment) {

        // FIXME Setting Destination after update/DO doesn't work

        val sh = vm.ships[tag!!.toInt()] // Don't use ship() as we need to handle null (do nothing)

        if (sh == null) {
            return
        } else {
            if (sh.uidRace != uidActivePlayer || sh.speed == 0) {
                view.findViewById<Spinner>(R.id.spinner).setVisibility(View.INVISIBLE)
            } else {
                // TODO: better selection of possible targets once we have visibility.
                // Right now it's all insystem and first planet of each system outside.
                // TODO: If we have fly to a star system update the lists here
                val destinationStrings = arrayListOf<String>()
                val destinationUids = HashMap<String, Int>()

                var currentUidDestination: Int? = null

                // Put current destination first, and avoid listing it again.
                if (sh.dest != null) {
                    // TODO Needs to change for locations other than planets
                    val key = "${sh.dest!!.getPlanet()!!.name} (current)"
                    destinationStrings.add(key)
                    currentUidDestination = sh.dest!!.getPlanet()!!.uid
                    destinationUids[key] = currentUidDestination
                } else {
                    val key = "Set Destination"
                    destinationStrings.add(key)
                }

                val star = sh.getStar()
                if (star != null) {
                    for (p in star.starUidPlanets.map { vm.planet(it) }) {
                        if (p.uid != currentUidDestination) {
                            val key = "${p.name} (insystem)"
                            destinationStrings.add(key)
                            destinationUids[key] = p.uid
                        }
                    }
                }
                val sortedStars =
                    vm.stars.toList().sortedBy { (_, s) ->
                        s.loc.getLoc().distance(sh.loc.getLoc())
                    }.take(6).drop(1)  // FIXME Don't drop the first if in DEEP SPACE, otherwise can't go back.

                for ((_, s) in sortedStars) {
                    val key = "${s.name} system"
                    destinationStrings.add(key)
                    destinationUids[key] = s.starUidPlanets.first()
                }

                // Create an ArrayAdapter
                val adapter =
                //ArrayAdapter<String>(this.activity!!, android.R.layout.simple_spinner_item, destinationStrings)
                    ArrayAdapter<String>(this.activity!!, R.layout.spinner_item, destinationStrings)

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Apply the adapter to the spinner
                val spinner = view.findViewById<Spinner>(R.id.spinner)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, spinnerView: View?, position: Int, id: Long) {

                        val destination = parent!!.getItemAtPosition(position).toString()

                        if (destination.equals("Set Destination")) { // FIXME make this a constant
                            return
                        }

                        val uidPlanet = destinationUids[destination]!!
                        val planet = vm.planet(uidPlanet)

                        if (sh.idxtype == GBData.POD) {
                            flyShipLanded(sh.uid, planet.uid) // update server side
                            sh.dest = GBLocation(planet, 0, 0) // update vm
                        } else {
                            flyShipOrbit(sh.uid, planet.uid)// update server side
                            sh.dest = GBLocation(planet, GBData.PlanetOrbit, 0f)
                        }

                        GlobalStuff.checkDo(view)

                        Toast.makeText(
                            view.context,
                            "Ordered " + sh.name + " to fly to " + planet.name,
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        fragment.setDetails(view)
                    }

                }

            }
        }

    }
}
