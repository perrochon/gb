package com.zwsi.gb.feature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.viewOrbitShips
import com.zwsi.gblib.GBPlanet

class PlanetFragment : Fragment() {

    lateinit var p: GBPlanet

    companion object {

        fun newInstance(message: String): PlanetFragment {

            val f = PlanetFragment()

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

        val view: View? = inflater.inflate(R.layout.fragment_planet, container, false);

        // What is this fragment about, and make sure the fragment remembers
        val planetID = arguments!!.getString("UID")!!.toInt()
        p = GBViewModel.viewPlanets[planetID]!!
        view!!.tag = p

        if (p.planetPopulation == 0) {
            val button = view.findViewById<Button>(R.id.makefactory)
            button.visibility = View.GONE
        }

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


        // Get the View to draw planet on, then draw planet
        //
        // TODO LOOKS use RoundedBitmapDrawable to make round corners?
        //
        var planetView = view.findViewById<ImageView>(R.id.ImageViewPlanet)
        val merged = Bitmap.createBitmap(p.width *50, p.height *50, d.config)
        var canvas = Canvas(merged)

        val planetStats = view.findViewById<TextView>(R.id.PlanetStats)
        val paint = planetStats.paint
        paint.textSize = 20f


        for (j in 0 until p.sectors.size) {

            canvas.drawBitmap(bitmaps[p.sectors[j].type],p.sectorX(j) * 50f,p.sectorY(j) *50f,null)

            if (p.sectors[j].population > 0) {
                canvas.drawText(
                    p.sectors[j].population.toString(),
                    p.sectorX(j) * 50f,
                    p.sectorY(j) * 50f + 40f,
                    paint
                )
                canvas.drawText(
                    p.sectors[j].sectorOwner.name.substring(0,1),
                    p.sectorX(j) * 50f + 20,
                    p.sectorY(j) * 50f + 20f,
                    paint
                )

            }

        }

        planetView.setImageBitmap(merged)

        paint.textSize = 40f

        planetStats.setText("${p.name} in ${p.star.name}\n")
        planetStats.append("Population ${p.planetPopulation} | Size ${p.size}\n")

        var ships = GBViewModel.viewLandedShips[p.uid]!!
        if (ships.isNotEmpty()) {
            planetStats.append("Ships landed (${ships.size.toString()}): ")
            for (sh in ships) {
                planetStats.append(sh.name + " ")
            }
        }

        ships = viewOrbitShips[p.uid]!!
        if (ships.isNotEmpty()) {
            planetStats.append(ships.size.toString() + " ships in orbit: ")
            for (sh in ships) {
                planetStats.append(sh.name + " ")
            }
        }

//        planetStats.append("\n\n")
//        planetStats.append("id: " + p.id +" | ")
//        planetStats.append("refUID: " + p.uid  +" | ")
//        planetStats.append("sid: " + p.sid +" | ")
//        planetStats.append("idxname: " + p.idxname +" | ")
//        planetStats.append("idtype: " + p.idxtype +"\n")

        return view
    }



}
