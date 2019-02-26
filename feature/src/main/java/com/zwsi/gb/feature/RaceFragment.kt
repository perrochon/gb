package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

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
        val r = GBViewModel.viewRaces.values.filter { it.uid == raceID }.first()
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


        val stats = view.findViewById<TextView>(R.id.RaceStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.setText("Name      : " + (r.name) + "\n")
        stats.append("Birthrate : " + (r.birthrate) + "\n")
        stats.append("Explore   : " + (r.explore) + "\n")
        stats.append("Absorption: " + (r.absorption) + "\n")

        val background = view.findViewById<TextView>(R.id.RaceBackground)
        paint = background.paint
        paint.textSize = 40f

        background.append(r.description)
        background.append("\n")

        background.append("\n")
        background.append("refUID: " + r.uid  +" | ")
        background.append("idxname: " + r.idx +"")

        val shipsTextView = view.findViewById<TextView>(R.id.Ships)
        paint = shipsTextView.paint
        paint.textSize = 40f

        shipsTextView.setMovementMethod(ScrollingMovementMethod())

        val ships = GBViewModel.viewRaceShips[r.uid]
        if ((ships != null) && (ships.isNotEmpty())) {
            shipsTextView.text = ("Ships (${ships.size.toString()}): ")
            for (sh in ships) {
                shipsTextView.append(sh.name + " ")
            }
        }











        return view
    }
}
