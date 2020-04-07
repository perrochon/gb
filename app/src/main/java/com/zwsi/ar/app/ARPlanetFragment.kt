package com.zwsi.ar.app

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBData.Companion.FACTORY
import kotlinx.android.synthetic.main.fragment_planet.*


class ARPlanetFragment : Fragment() {

    companion object {
        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): ARPlanetFragment {
            return ARPlanetFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_planet, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setDetails()

        val p = vm.planet(tag!!.toInt())

        image_planet.setImageBitmap(p.getBitmap())

        val turnObserver = Observer<Int> {
            setDetails()
            view.invalidate()
        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> {
            setDetails()
            view.invalidate()
        }
        ARViewModel.actionsTaken.observe(this, actionObserver)

        button_factory.tag = tag!!.toInt()
        button_factory.setOnClickListener {
            GlobalStuff.makeStructure(it, FACTORY)
        }

        button_to_system_star.tag = vm.planet(tag!!.toInt()).star.uid
        button_to_system_star.setOnClickListener {
            GlobalStuff.panZoomToStar(it)
        }

        button_to_planet.tag = tag!!.toInt()
        button_to_planet.setOnClickListener {
            GlobalStuff.panZoomToPlanet(it)
        }

    }

    private fun setDetails() {

        // Planets don't go away, so the below !! should be safe
        val p = vm.planet(tag!!.toInt())

        if (p.planetUidRaces.contains(uidActivePlayer)) {
            button_factory.visibility = View.VISIBLE
            button_factory.isEnabled =
                !(vm.secondPlayer && vm.playerTurns[uidActivePlayer] < ARViewModel.MIN_ACTIONS)

        } else {
            button_factory.visibility = View.INVISIBLE
        }


        if (p.planetPopulation == 0) {
            button_factory.visibility = View.GONE
        }

        val paint = text_planet_stats.paint

        text_planet_stats.text = "${p.name} in ${p.star.name} system\n"
        text_planet_stats.append("Size: ${p.size} | Population: ${p.planetPopulation.f(6)} \n")

        if (p.planetUidRaces.isNotEmpty()) {
            text_planet_stats.append("Races present: ")
            for (uidR in p.planetUidRaces) {
                text_planet_stats.append(vm.race(uidR).name + " ")
            }
            text_planet_stats.append("\n")
        }

        if (p.landedUidShips.isNotEmpty()) {
            text_planet_stats.append("Ships landed (${p.landedUidShips.size}): ")
            for (uidS in p.landedUidShips) {
                text_planet_stats.append(vm.ship(uidS).name + " ")
            }
            text_planet_stats.append("\n")
        }

        if (p.orbitUidShips.isNotEmpty()) {
            text_planet_stats.append("Ships in orbit (${p.orbitUidShips.size}): ")
            for (uidS in p.orbitUidShips) {
                text_planet_stats.append(vm.ship(uidS).name + " ")
            }
            text_planet_stats.append("\n")
        }
    }
}
