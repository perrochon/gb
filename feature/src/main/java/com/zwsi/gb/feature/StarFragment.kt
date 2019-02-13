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
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBStar

class StarFragment : Fragment() {

    companion object {

        fun newInstance(message: String): StarFragment {

            val f = StarFragment()

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

        val view: View? = inflater.inflate(R.layout.fragment_star, container, false);

        val turnObserver = Observer<Int> { newTurn ->
            // FIXIT UPDATE Need to call setStats but don't have a star. Need to keep a copy of UID somewhere
            // Needs more thinking
            view!!.invalidate()
        }  // TODO why is newTurn nullable?
        GBViewModel.currentTurn.observe(this, turnObserver)

        // What is this fragment about, and make sure the fragment remembers
        val starID = arguments!!.getString("UID")!!.toInt()
        val st = GBViewModel.viewStars[starID]!!
        view!!.tag = st

        setStats(view, st)

        val imageView = view.findViewById<ImageView>(R.id.StarView)

        imageView.setImageResource(R.drawable.yellow)

        return view
    }

    fun setStats(view: View, st: GBStar) {
        val stats = view.findViewById<TextView>(R.id.StarStats)
        val paint = stats.paint
        paint.textSize = 40f

        stats.setText("${st.name} at (" + (st.loc.getLoc().x.toInt()) + ", " + st.loc.getLoc().y.toInt() + ")\n")
        stats.append("")

        if (viewStarPlanets[st.uid]!!.isNotEmpty()) {
            stats.append("Planets (" + viewStarPlanets[st.uid]!!.size.toString() + "): ")
            for (pl in GBViewModel.viewStarPlanets[st.uid]!!) {
                stats.append("  " + pl.name + " ")
            }
            stats.append("\n")
        }

        val ships = GBViewModel.viewStarShips[st.uid]!!
        if (ships.isNotEmpty()) {
            stats.append("Ships (${ships.size.toString()}): ")
            for (sh in ships) {
                stats.append(sh.name + " ")
            }
        }

    }
}
