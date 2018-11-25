package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBController
import kotlinx.android.synthetic.*

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

        val universe = GBController.universe
        val ships = universe!!.allShips
        val sh = ships[shipID]


        val imageView = view!!.findViewById<ImageView>(R.id.ShipView)

        view.findViewById<Button>(R.id.goButton).setTag(sh)
        view.findViewById<Button>(R.id.makeCruiser).setTag(sh)
        view.findViewById<Button>(R.id.makePod).setTag(sh)


        if (sh.idxtype == 0) {
            imageView.setImageResource(R.drawable.factory)

            view.findViewById<Button>(R.id.makePod).setVisibility(View.VISIBLE)
            view.findViewById<Button>(R.id.makeCruiser).setVisibility(View.VISIBLE)

        } else if (sh.idxtype == 1) {
            if (sh.owner.uid == 2) {
                imageView.setImageResource(R.drawable.beetlepod)

            } else {
                imageView.setImageResource(R.drawable.podt)
            }
        } else if (sh.idxtype == 2) {
            imageView.setImageResource(R.drawable.cruisert)
        } else
            imageView.setImageResource(R.drawable.yellow)



        var stats = view.findViewById<TextView>(R.id.ShipStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Name    : " + sh!!.name + "\n")
        stats.append("Type    : " + sh.type + "\n")
        stats.append("Speed   : " + sh.speed + "\n")
        stats.append("Owner   : " + sh.owner.name + "\n")
        stats.append("Location: " + sh.getLocation() + "\n")


        stats = view!!.findViewById<TextView>(R.id.ShipBackground)
        paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor " +
                "invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.\n")

        stats.append("\n")
        stats.append("id: " + sh.id +" | ")
        stats.append("uid: " + sh.uid  +" | ")
        stats.append("idxt: " + sh.idxtype +" | ")
        stats.append("loca: " + sh.level + "." + sh.locationuid + "\n")

        return view
    }
}
