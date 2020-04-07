package com.zwsi.ar.app

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwsi.ar.app.ARViewModel.Companion.superSensors
import com.zwsi.ar.app.ARViewModel.Companion.uidActivePlayer
import com.zwsi.ar.app.ARViewModel.Companion.vm
import kotlinx.android.synthetic.main.fragment_star.*

class ARStarFragment : Fragment() {

    companion object {

        fun newInstance(@Suppress("UNUSED_PARAMETER") message: String): ARStarFragment {
            return ARStarFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_star, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setDetails()

        val turnObserver = Observer<Int> { _ ->
            setDetails()
            view.invalidate()
        }  // TODO why is newTurn nullable?
        ARViewModel.currentTurn.observe(this, turnObserver)

        button_to_star.tag = tag!!.toInt()
        button_to_star.setOnClickListener {
            GlobalStuff.panZoomToStar(it)
        }
    }

    private fun setDetails() {

        // Stars don't go away, so the below !! should be safe
        val star = vm.star(tag!!.toInt())

        text_star_stats.text =
            "${star.name} at (" + (star.loc.getLoc().x.toInt()) + ", " + star.loc.getLoc().y.toInt() + ")\n"

        if (superSensors || vm.race(uidActivePlayer).raceVisibleStars.contains(star.uid)) {
            val planets = star.starUidPlanets.map { vm.planet(it) }
            if (planets.isNotEmpty()) {
                text_star_stats.append("Planets (${planets.size}): ")
                for (p in planets) {
                    text_star_stats.append("  " + p.name + " ")
                }
                text_star_stats.append("\n")
            }

            val uidShips = star.starUidShips
            for (uidPP in star.starUidPatrolPoints) {
                uidShips += vm.patrolPoint(uidPP).orbitUidShips
            }
            if (uidShips.isNotEmpty()) {
                text_star_stats.append("Ships (${uidShips.size}): ")
                for (s in uidShips) {
                    text_star_stats.append(vm.ship(s).name + " ")
                }
            }
        }
    }
}
