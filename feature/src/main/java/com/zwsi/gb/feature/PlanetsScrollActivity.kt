package com.zwsi.gb.feature

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.support.constraint.ConstraintLayout
import android.widget.TextView
import android.support.constraint.ConstraintSet
import android.support.v4.view.ViewCompat
import com.zwsi.gb.feature.GBViewModel.Companion.viewStarPlanets


class PlanetsScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planets_scroll)

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

        // Get a View to draw planet on, then draw planet
        // Until we have a proper home planet, we draw a small planet with one sector each for testing
        var planetView = findViewById<ImageView>(R.id.homeplanet)
        var merged = Bitmap.createBitmap(200, 100, d.config)
        var canvas = Canvas(merged)

        canvas.drawBitmap(bitmaps[0], 0f, 0f, null)
        canvas.drawBitmap(bitmaps[1], 50f, 0f, null)
        canvas.drawBitmap(bitmaps[2], 100f, 0f, null)
        canvas.drawBitmap(bitmaps[3], 150f, 0f, null)
        canvas.drawBitmap(bitmaps[4], 0f, 50f, null)
        canvas.drawBitmap(bitmaps[5], 50f, 50f, null)
        canvas.drawBitmap(bitmaps[6], 100f, 50f, null)
        canvas.drawBitmap(bitmaps[7], 150f, 50f, null)

        planetView.setImageBitmap(merged)

        val stats = findViewById<TextView>(R.id.homePlanetStats)
        val color = stats.textColors
        val size = stats.textSize
        val paint = stats.paint
        paint.textSize = 20f


        // Now add more allPlanets below
        // For now we show all allPlanets, but eventually each race only sees what they can see
        val planetList = findViewById(R.id.planetsLinearLayout) as LinearLayout

        for ((_, s) in GBViewModel.viewStars) {
            for (p in viewStarPlanets[s.uid]!!) {

                val constraintLayout = ConstraintLayout(this)
                planetList.addView(constraintLayout)

                planetView = ImageView(this)
                planetView.imageAlpha = 255
                merged = Bitmap.createBitmap(p.width *50, p.height *50, d.config)
                canvas = Canvas(merged)

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
                planetView.setId(ViewCompat.generateViewId()) // Needs to have an ID. View.setId() is API level 17+
                constraintLayout.addView(planetView)

                val planetStats = TextView(this)
                planetStats.setTextColor(color) // copy colors from home planet
                planetStats.setTextSize(size)
                planetStats.setTextSize(14f)  // TODO: debug the above. It should copy from the Home Planet
                planetStats.setText(p.name + " (" + p.type + "), " + s.name + " system\n")
                planetStats.append("Size :" + p.size + "\n")
                planetStats.setId(ViewCompat.generateViewId()) // Needs to have an ID. View.setId() is API level 17+
                constraintLayout.addView(planetStats)

                val cs = ConstraintSet()
                cs.clone(constraintLayout)

                cs.constrainHeight(planetView.id,200)
                cs.constrainWidth(planetView.id,400)
                //cs.connect(planet.id,ConstraintSet.TOP,constraintLayout.id,ConstraintSet.TOP,16)
                //cs.connect(planet.id,ConstraintSet.BOTTOM,constraintLayout.id,ConstraintSet.BOTTOM,16)
                //cs.connect(planet.id,ConstraintSet.LEFT,constraintLayout.id,ConstraintSet.LEFT,16)
                //cs.connect(planet.id,ConstraintSet.RIGHT,constraintLayout.id,ConstraintSet.RIGHT,16)

                //cs.centerVertically(planet.id,planetStats.id)


                cs.connect(planetStats.getId(), ConstraintSet.LEFT, planetView.getId(), ConstraintSet.RIGHT, 16)

                cs.applyTo(constraintLayout)

                constraintLayout.setOnClickListener {
                    val intent = Intent(this, PlanetsSlideActivity::class.java)
                    intent.putExtra("UID", p.uid)
                    startActivity(intent)
                }


                val divider = ImageView(this)
                val lp = LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 5) // TODO: Why fully qualified
                lp.setMargins(10, 10, 10, 10)
                divider.layoutParams = lp
                planetList.addView(divider)

            }
        }
    }
}
