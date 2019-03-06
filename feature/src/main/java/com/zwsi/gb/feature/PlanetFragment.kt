package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.uidActivePlayer
import com.zwsi.gb.feature.GBViewModel.Companion.vm

class PlanetFragment : Fragment() {

    companion object {

        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): PlanetFragment {
            return PlanetFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_planet, container, false)!!
        val p = vm.planet(tag!!.toInt())

        setDetails(view)

        val turnObserver = Observer<Int> { _ ->
            setDetails(view)
            view.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val factoryButton: Button = view.findViewById(R.id.makefactory)
        factoryButton.tag = tag!!.toInt()
        factoryButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeFactory(it)
        })
        if (uidActivePlayer == p.planetOwner.uid) {
            factoryButton.visibility = View.VISIBLE
        } else {
            factoryButton.visibility = View.INVISIBLE
        }

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

        var ships = vm.planet(p.uid).landedUidShips
        if (ships.isNotEmpty()) {
            planetStats.append("Ships landed (${ships.size.toString()}): ")
            for (uidS in ships) {
                planetStats.append(vm.ship(uidS).name + " ")
            }
            planetStats.append("\n")
        }

        ships = vm.planet(p.uid).orbitUidShips
        if (ships.isNotEmpty()) {
            planetStats.append("Ships in orbit (${ships.size.toString()}): ")
            for (uidS in ships) {
                planetStats.append(vm.ship(uidS).name + " ")
            }
            planetStats.append("\n")
        }

    }

}
