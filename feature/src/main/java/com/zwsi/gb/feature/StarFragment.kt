package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBController

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

        var view: View? = inflater.inflate(R.layout.fragment_star, container, false);

        // What is this fragment about, and make sure the fragment remembers
        val starID = arguments!!.getString("UID")!!.toInt()
        val st = GBController.universe.allStars[starID]
        view!!.tag = st

        val imageView = view.findViewById<ImageView>(R.id.StarView)

        imageView.setImageResource(R.drawable.yellow)


        var stats = view.findViewById<TextView>(R.id.StarStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Name     : " + (st.name) + "\n")
        stats.append("Position : (" + (st.loc.x) + ", " + st.loc.y + ")\n")

        if (st.starPlanets.isNotEmpty()) {
            stats.append(st.starPlanets.size.toString() + " planets:\n")
            for (pl in st.starPlanets) {
                stats.append("  " + pl.name + " ")
            }
            stats.append("\n")
        }

        if (st.getStarShipsList().isNotEmpty()) {
            stats.append(st.getStarShipsList().size.toString() + " ships present:\n")
            for (sh in st.getStarShipsList()) {
                stats.append("           " + sh.name + " (" + sh.race.name + ")\n")
            }
        }

        stats = view.findViewById<TextView>(R.id.StarBackground)
        paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Lorem ipsum dolor sit amet\n")

        stats.append("\n")
        stats.append("id: " + st.id + " | ")
        stats.append("refUID: " + st.uid + " | ")
        stats.append("idxname: " + st.idxname + "\n")

        return view
    }
}
