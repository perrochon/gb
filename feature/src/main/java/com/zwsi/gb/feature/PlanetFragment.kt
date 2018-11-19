package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gblib.GBController

class PlanetFragment : Fragment() {

    companion object {

        fun newInstance(message: String): PlanetFragment {

            val f = PlanetFragment()

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

        var view: View? = inflater.inflate(R.layout.fragment_planet, container, false);



        // Get Bitmaps - TODO factor out, this code exists twice. But where to?
        val d = BitmapFactory.decodeResource(getResources(), R.drawable.desert)
        val f = BitmapFactory.decodeResource(getResources(), R.drawable.forest)
        val g = BitmapFactory.decodeResource(getResources(), R.drawable.gas)
        val i = BitmapFactory.decodeResource(getResources(), R.drawable.ice)
        val l = BitmapFactory.decodeResource(getResources(), R.drawable.land)
        val m = BitmapFactory.decodeResource(getResources(), R.drawable.mountain)
        val r = BitmapFactory.decodeResource(getResources(), R.drawable.rock)
        val w = BitmapFactory.decodeResource(getResources(), R.drawable.water)
        val bitmaps = arrayOf(w,l,g,d,m,f,i,r)

        val planetID = arguments!!.getString("uId").toInt()

        val universe = GBController.universe
        val planets = universe!!.allPlanets
        var p = planets[planetID]!!

        // Get the View to draw planet on, then draw planet
        //
        var planetView = view!!.findViewById<ImageView>(R.id.PlanetView)
        val merged = Bitmap.createBitmap(p.width *50, p.height *50, d.config)
        var canvas = Canvas(merged)

        val planetStats = view!!.findViewById<TextView>(R.id.PlanetStats)
        val paint = planetStats.paint
        paint.textSize = 20f


        for (i in 0 until p.sectors.size) {

            canvas.drawBitmap(bitmaps[p.sectors[i].type],p.sectorX(i) * 50f,p.sectorY(i) *50f,null)

            if (p.sectors[i].getPopulation() > 0) {
                canvas.drawText(
                    p.sectors[i].getPopulation().toString(),
                    p.sectorX(i) * 50f,
                    p.sectorY(i) * 50f + 40f,
                    paint
                )
                canvas.drawText(
                    p.sectors[i].getOwner()!!.name.substring(0,1),
                    p.sectorX(i) * 50f + 20,
                    p.sectorY(i) * 50f + 20f,
                    paint
                )

            }


        }

        planetView.setImageBitmap(merged)

        paint.textSize = 40f

        planetStats.append("\n")
        planetStats.append("Name  : " + p.name +"\n")
        //planetStats.append("System: " + stars[0]!!.name +"\n")
        planetStats.append("Type  : " + p.type +"\n")
        planetStats.append("Size  : " + p.size +"\n")
        planetStats.append("Owner : " + p.ownerName +"\n")







        return view
    }

}
