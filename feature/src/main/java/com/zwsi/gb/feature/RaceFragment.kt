package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.vm

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

        val view: View = inflater.inflate(R.layout.fragment_race, container, false)!!

        setDetails(view)

        return view
    }

    private fun setDetails(view: View) {
        // What is this fragment about, and make sure the fragment remembers
        val uidRace = arguments!!.getString("UID")!!.toInt()
        val r = vm.race(uidRace)
        view.tag = r // FIXME PERSISTENCE Don't pass race, pass race UID


        val imageView = view.findViewById<ImageView>(R.id.RaceView)

        if (uidRace == 0)
            imageView.setImageResource(R.drawable.race_xenos)
        if (uidRace == 1)
            imageView.setImageResource(R.drawable.race_impi)
        if (uidRace == 2)
            imageView.setImageResource(R.drawable.race_beetle)
        if (uidRace == 3)
            imageView.setImageResource(R.drawable.race_tortoise)


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
        background.append("refUID: " + r.uid + " | ")
        background.append("idxname: " + r.idx + "")

        val shipsTextView = view.findViewById<TextView>(R.id.Ships)
        paint = shipsTextView.paint
        paint.textSize = 40f

        shipsTextView.setMovementMethod(ScrollingMovementMethod())

        val ships = r.raceUidShips.map { vm.race(it) }
        if (ships.isNotEmpty()) {
            shipsTextView.text = ("Ships (${ships.size.toString()}): ")
            for (sh in ships) {
                shipsTextView.append(sh.name + " ")
            }
        }

    }
}
