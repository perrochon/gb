package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBController

class ShipFragment : Fragment() {

    companion object {

        fun newInstance(message: String): ShipFragment {

            val f = ShipFragment()

            val bdl = Bundle(1)

            bdl.putString("uId", message)

            f.setArguments(bdl)

            return f

        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view: View? = inflater.inflate(R.layout.fragment_ship, container, false);


        val shipID = arguments!!.getString("uId").toInt()

        val imageView = view!!.findViewById<ImageView>(R.id.ShipView)

        if (shipID == 0)
            imageView.setImageResource(R.drawable.pod)
        else
            imageView.setImageResource(R.drawable.pod)

        val universe = GBController.universe
        val ships = universe!!.allShips
        val sh = ships[shipID]

        var stats = view.findViewById<TextView>(R.id.ShipStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Name : " + (sh!!.name) + "\n")
        stats.append("Type : " + (sh.type) + "\n")
        stats.append("Speed : " + (sh.speed) + "\n")
        stats.append("Owner: " + (sh.owner.name) + "\n")
        stats.append("Position: " + (sh.position.name) + " sytem \n")


        stats = view!!.findViewById<TextView>(R.id.ShipBackground)
        paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor " +
                "invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et " +
                "justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum " +
                "dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr,  sed diam nonumy eirmod " +
                "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
                "At vero eos et accusam et justo duo dolores et ea rebum.")

        return view
    }
}
