package com.zwsi.ar.app

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwsi.ar.app.ARViewModel.Companion.vm
import kotlinx.android.synthetic.main.fragment_race.*

class ARRaceFragment : Fragment() {

    companion object {

        fun newInstance(message: String): ARRaceFragment {
            val f = ARRaceFragment()
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
        return inflater.inflate(R.layout.fragment_race, container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setDetails()
    }

    private fun setDetails() {
        // What is this fragment about, and make sure the fragment remembers
        val uidRace = arguments!!.getString("UID")!!.toInt()
        val r = vm.race(uidRace)
        view!!.tag = r // FIXME PERSISTENCE Don't pass race, pass race UID

        if (uidRace == 0)
            image_race.setImageResource(R.drawable.race_xenos)
        if (uidRace == 1)
            image_race.setImageResource(R.drawable.race_impi)
        if (uidRace == 2)
            image_race.setImageResource(R.drawable.race_beetle)
        if (uidRace == 3)
            image_race.setImageResource(R.drawable.race_tortoise)


        var paint = text_race_stats.paint
        paint.textSize = 40f

        text_race_stats.text = "Name      : " + (r.name) + "\n"
        text_race_stats.append("Birthrate : " + (r.birthrate) + "\n")
        text_race_stats.append("Explore   : " + (r.explore) + "\n")
        text_race_stats.append("Absorption: " + (r.absorption) + "\n")

        paint = text_race_background.paint
        paint.textSize = 40f

        text_race_background.append(r.description)
        text_race_background.append("\n")

        text_race_background.append("\n")
        text_race_background.append("refUID: " + r.uid + " | ")
        text_race_background.append("idxname: " + r.idx + "")

        paint = text_race_ships.paint
        paint.textSize = 40f

        text_race_ships.setMovementMethod(ScrollingMovementMethod())

        val ships = r.raceUidShips.map { vm.race(it) }
        if (ships.isNotEmpty()) {
            text_race_ships.text = ("Ships (${ships.size.toString()}): ")
            for (sh in ships) {
                text_race_ships.append(sh.name + " ")
            }
        }

    }
}
