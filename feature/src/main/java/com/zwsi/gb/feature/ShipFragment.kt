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
import android.widget.*
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm
import com.zwsi.gblib.GBController.Companion.flyShipLanded
import com.zwsi.gblib.GBController.Companion.flyShipPlanetOrbit
import com.zwsi.gblib.GBController.Companion.flyShipStarPatrol
import com.zwsi.gblib.GBData
import com.zwsi.gblib.GBData.Companion.BATTLESTAR
import com.zwsi.gblib.GBData.Companion.CRUISER
import com.zwsi.gblib.GBData.Companion.FACTORY
import com.zwsi.gblib.GBData.Companion.HEADQUARTER
import com.zwsi.gblib.GBData.Companion.POD
import com.zwsi.gblib.GBData.Companion.RESEARCH
import com.zwsi.gblib.GBData.Companion.SHUTTLE
import com.zwsi.gblib.GBData.Companion.STATION
import com.zwsi.gblib.GBLocation
import com.zwsi.gblib.GBLocation.Companion.DEEPSPACE
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.PATROL
import com.zwsi.gblib.GBPatrolPoint
import com.zwsi.gblib.distance


class ShipFragment : Fragment() {

    companion object {

        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): ShipFragment {
            return ShipFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_ship, container, false)!!

        setSpinner(view, this)

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
            val spinner = view.findViewById<Spinner>(R.id.spinner)

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
//            GlobalStuff.panzoomToShip(it)

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
            stats.append("Speed: " + sh.speed + "\n")
            stats.append("Health: " + sh.health + "\n")
            stats.append("Weapons: " + sh.guns + "\n")
            stats.append("Damage: " + sh.damage + "\n")
            stats.append("Range: " + sh.range+ "\n")
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

            val spinner = view.findViewById<Spinner>(R.id.spinner)
            if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < GBViewModel.MIN_ACTIONS) {
                spinner.isEnabled = false

                // Hack to make spinner look disabled. getSelectedView.isEnabled=false didn't work
                // We may replace this with a custom spinner, at which point there may be better options
                spinner.alpha = 0.5f
            } else {
                spinner.isEnabled = true
                spinner.alpha = 1f // See Hack above.
            }
        }
    }

    private fun setSpinner(view: View, fragment: ShipFragment) {

        // FIXME Setting Destination after update/DO doesn't work

        val sh = vm.ships[tag!!.toInt()] // Don't use ship() as we need to handle null (do nothing)

        if (sh == null || sh.loc.level == DEEPSPACE) {
            return
        } else {
            if (sh.uidRace != uidActivePlayer || sh.speed == 0) {
                view.findViewById<Spinner>(R.id.spinner).setVisibility(View.INVISIBLE)
            } else {
                // TODO: better selection of possible targets once we have visibility.
                // Right now it's all insystem and first planet of each system outside.
                // TODO: If we have fly to a star system update the lists here
                val destinationStrings = arrayListOf<String>()
                val destinationUids =
                    HashMap<String, Int>() // FIXME Use getItemAtPosition(position) and get it out of item.


                var currentUidDestination = 1000

                // Put current destination first, and avoid listing it again.
                if (sh.dest != null) {

                    val key: String

                    when (sh.dest!!.level) {
                        ORBIT, LANDED -> {
                            key = "${sh.dest!!.getPlanet()!!.name} (current)"
                            currentUidDestination = sh.dest!!.getPlanet()!!.uid
                            destinationStrings.add(key)
                            destinationUids[key] = currentUidDestination
                        }
                        PATROL -> {
                            key = "${sh.dest!!.getPatrolPoint()!!.star.name} (current)"
                            currentUidDestination = -sh.dest!!.getPatrolPoint()!!.star.uid
                            destinationStrings.add(key)
                            destinationUids[key] = currentUidDestination
                        }
                        else -> {
                        }
                    }
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
                    }.take(6)

                for ((_, star) in sortedStars) {
                    if (star.uid != -currentUidDestination) {
                        val key = "${star.name} system"
                        destinationStrings.add(key)
                        destinationUids[key] = -star.uid
                    }
                }

                // Create an ArrayAdapter
                val adapter = ArrayAdapter<String>(this.activity!!, R.layout.spinner_item, destinationStrings)

                // Specify the layout to use when the list of choices appears
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                // Apply the adapter to the spinner
                val spinner = view.findViewById<Spinner>(R.id.spinner)
                spinner.adapter = adapter
                spinner.setSelection(1, false) // Hack ? To prevent being called twice

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, spinnerView: View?, position: Int, id: Long) {

                        // Hack: onItemSelected is called twice. But the first time, spinnerView seems to be null
                        if (spinnerView != null) {
                            val destination = parent!!.getItemAtPosition(position).toString()

                            if (destination == "Set Destination") { // FIXME make this a constant
                                return
                            }

                            var uid = destinationUids[destination]!!

                            if (uid > 0) { // Planet
                                val planet = vm.planet(uid)

                                if (sh.idxtype == GBData.POD) {
                                    flyShipLanded(sh.uid, planet.uid) // update server side
                                    sh.dest = GBLocation(planet, 0, 0) // update vm
                                } else {
                                    flyShipPlanetOrbit(sh.uid, planet.uid)// update server side
                                    sh.dest = GBLocation(planet, GBData.PlanetOrbit, 0f) // update vm
                                }
                            } else { // Star
                                uid = -uid
                                val star = vm.star(uid)
                                val patrolPoint: GBPatrolPoint
                                if (GBData.rand.nextBoolean()) {
                                    patrolPoint = vm.patrolPoint(star.starUidPatrolPoints.first())
                                } else {
                                    patrolPoint = vm.patrolPoint(star.starUidPatrolPoints.drop(1).first())
                                }
                                flyShipStarPatrol(sh.uid, patrolPoint.uid)// update server side
                                sh.dest = GBLocation(patrolPoint, 0f, 0f) // update vm // FIXME select patrol point
                            }
                            GlobalStuff.checkDo(view)

                            Toast.makeText(
                                view.context,
                                "Ordered " + sh.name + " to fly to " + sh.dest,
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
}

