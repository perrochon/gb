package com.zwsi.gb.feature

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.viewStarPlanets

class StarFragment : Fragment() {

    companion object {

        fun newInstance(message: String): StarFragment {
            return StarFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_star, container, false)!!

        setDetails(view)

        val turnObserver = Observer<Int> { newTurn->
            setDetails(view)
            view.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        //val imageView = view.findViewById<ImageView>(R.id.StarView)

        //Fixme DELETE Why not in XML?
        //imageView.setImageResource(R.drawable.yellow)

        return view
    }

    private fun setDetails(view: View) {

        // Stars don't go away, so the below !! should be safe
        val star = GBViewModel.viewStars[tag!!.toInt()]!!

        val stats = view.findViewById<TextView>(R.id.StarStats)
        val paint = stats.paint
        paint.textSize = 40f

        stats.setText("${star.name} at (" + (star.loc.getLoc().x.toInt()) + ", " + star.loc.getLoc().y.toInt() + ")\n")

        if (viewStarPlanets[star.uid]!!.isNotEmpty()) {
            stats.append("Planets (" + viewStarPlanets[star.uid]!!.size.toString() + "): ")
            for (pl in GBViewModel.viewStarPlanets[star.uid]!!) {
                stats.append("  " + pl.name + " ")
            }
            stats.append("\n")
        }

        val ships = GBViewModel.viewStarShips[star.uid]!!
        if (ships.isNotEmpty()) {
            stats.append("Ships (${ships.size.toString()}): ")
            for (sh in ships) {
                stats.append(sh.name + " ")
            }
        }

    }
}
