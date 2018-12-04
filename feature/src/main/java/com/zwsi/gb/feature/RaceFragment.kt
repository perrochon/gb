package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBController

class RaceFragment : Fragment() {

    companion object {

        fun newInstance(message: String): RaceFragment {

            val f = RaceFragment()

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

        var view: View? = inflater.inflate(R.layout.fragment_race, container, false);


        // What is this fragment about, and make sure the fragment remembers
        val raceID = arguments!!.getString("UID")!!.toInt()
        val r = GBController.universe.allRaces[raceID]
        view!!.tag = r


        val imageView = view.findViewById<ImageView>(R.id.RaceView)

        if (raceID == 0)
            imageView.setImageResource(R.drawable.xenost)
        if (raceID == 1)
            imageView.setImageResource(R.drawable.impit)
        if (raceID == 2)
            imageView.setImageResource(R.drawable.beetle)
        if (raceID == 3)
            imageView.setImageResource(R.drawable.tortoise)



        var stats = view.findViewById<TextView>(R.id.RaceStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.setText("Name      : " + (r.name) + "\n")
        stats.append("Birthrate : " + (r.birthrate) + "\n")
        stats.append("Explore   : " + (r.explore) + "\n")
        stats.append("Absorption: " + (r.absorption) + "\n")

        var ships = r.getRaceShipsList()
        if (ships.isNotEmpty()) {
            stats.append(ships.size.toString() + " ship(s): ")
            for (sh in ships) {
                stats.append(sh.name + " ")
            }
        }


        stats = view.findViewById<TextView>(R.id.RaceBackground)
        paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append(r.description)
        stats.append("\n")


        stats.append("\n")
        stats.append("id: " + r.id +" | ")
        stats.append("refUID: " + r.uid  +" | ")
        stats.append("idxname: " + r.idx +"\n")









        return view
    }
}
