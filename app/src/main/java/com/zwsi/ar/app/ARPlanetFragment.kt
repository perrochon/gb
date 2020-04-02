package com.zwsi.ar.app

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import com.zwsi.gblib.GBData.Companion.FACTORY


class ARPlanetFragment : Fragment() {

    companion object {

        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): ARPlanetFragment {
            return ARPlanetFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_planet, container, false)!!

        setDetails(view)

        val p = vm.planet(tag!!.toInt())

        val planetView = view.findViewById<ImageView>(R.id.PlanetView)
        planetView.setImageBitmap(p.getBitmap())

        val turnObserver = Observer<Int> { _ ->
            setDetails(view)
            view.invalidate()
        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        val actionObserver = Observer<Int> { _ ->
            setDetails(view)
            view.invalidate()
        }
        ARViewModel.actionsTaken.observe(this, actionObserver)

        val factoryButton: Button = view.findViewById(R.id.makefactory)
        factoryButton.tag = tag!!.toInt()
        factoryButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeStructure(it, FACTORY)
        })

        val starButton: Button = view.findViewById(R.id.panzoomToSystemStar)
        starButton.tag = vm.planet(tag!!.toInt()).star.uid
        starButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.panzoomToStar(it)
        })

        val planetButton: Button = view.findViewById(R.id.panzoomToPlanet)
        planetButton.tag = tag!!.toInt()
        planetButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.panzoomToPlanet(it)
        })

        return view
    }

    private fun setDetails(view: View) {

        val p = vm.planet(tag!!.toInt())

        val factoryButton: Button = view.findViewById(R.id.makefactory)
        if (p.planetUidRaces.contains(uidActivePlayer)) {
            factoryButton.visibility = View.VISIBLE
            if (vm.secondPlayer && vm.playerTurns[uidActivePlayer] < ARViewModel.MIN_ACTIONS) {
                factoryButton.isEnabled = false
            } else {
                factoryButton.isEnabled = true
            }

        } else {
            factoryButton.visibility = View.INVISIBLE
        }


        if (p.planetPopulation == 0) {
            val button = view.findViewById<Button>(R.id.makefactory)
            button.visibility = View.GONE
        }

        val planetStats = view.findViewById<TextView>(R.id.PlanetStats)
        val paint = planetStats.paint
        paint.textSize = 20f

        paint.textSize = 40f

        planetStats.setText("${p.name} in ${p.star.name} system\n")
        planetStats.append("Size: ${p.size} | Population: ${p.planetPopulation.f(6)} \n")

        if (p.planetUidRaces.isNotEmpty()) {
            planetStats.append("Races present: ")
            for (uidR in p.planetUidRaces) {
                planetStats.append(vm.race(uidR).name + " ")
            }
            planetStats.append("\n")
        }

        if (p.landedUidShips.isNotEmpty()) {
            planetStats.append("Ships landed (${p.landedUidShips.size.toString()}): ")
            for (uidS in p.landedUidShips) {
                planetStats.append(vm.ship(uidS).name + " ")
            }
            planetStats.append("\n")
        }

        if (p.orbitUidShips.isNotEmpty()) {
            planetStats.append("Ships in orbit (${p.orbitUidShips.size.toString()}): ")
            for (uidS in p.orbitUidShips) {
                planetStats.append(vm.ship(uidS).name + " ")
            }
            planetStats.append("\n")
        }
    }
}
