package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.viewLandedShips
import com.zwsi.gb.feature.GBViewModel.Companion.viewOrbitShips
import com.zwsi.gb.feature.GBViewModel.Companion.viewPlanets
import com.zwsi.gblib.GBPlanet

class PlanetFragment : Fragment() {

    companion object {

        fun newInstance(message: String): PlanetFragment {
            return PlanetFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_planet, container, false)!!

        setDetails(view)

        val turnObserver = Observer<Int> { _->
            setDetails(view)
            view.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        val factoryButton: Button = view.findViewById(R.id.makefactory)
        factoryButton.tag = tag!!.toInt()
        factoryButton.setOnClickListener(View.OnClickListener {
            GlobalStuff.makeFactory(it)
        })

        val starButton: Button = view.findViewById(R.id.panzoomToSystemStar)
        starButton.tag = GBViewModel.viewPlanets[tag!!.toInt()]!!.star.uid
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

        // Planets don't go away, so the below !! should be safe
        val p = GBViewModel.viewPlanets[tag!!.toInt()]!!

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

        var ships = viewLandedShips[p.uid]!!
        if (ships.isNotEmpty()) {
            planetStats.append("Ships landed (${ships.size.toString()}): ")
            for (sh in ships) {
                planetStats.append(sh.name + " ")
            }
            planetStats.append("\n")
        }

        ships = viewOrbitShips[p.uid]!!
        if (ships.isNotEmpty()) {
            planetStats.append("Ships in orbit (${ships.size.toString()}): ")
            for (sh in ships) {
                planetStats.append(sh.name + " ")
            }
            planetStats.append("\n")
        }

    }

}
