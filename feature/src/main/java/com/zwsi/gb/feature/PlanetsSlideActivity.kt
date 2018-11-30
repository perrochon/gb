package com.zwsi.gb.feature

import android.os.Bundle
import android.os.SystemClock
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBPlanet

class PlanetsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private var startItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planets_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Planets"
        val titleTextView = findViewById<TextView>(R.id.text_planets_title)
        titleTextView.setText(title)

        initViews()
        setupViewPager()

        viewpager.setCurrentItem(startItem)

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())
        val startUID = getIntent().getIntExtra("UID", -1)

        // Figure out which items to display
        val displayList: ArrayList<Int>
        if (intent.hasExtra("planets")) {
            displayList = intent.getIntegerArrayListExtra("planets")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allPlanets) {
                displayList.add(i.uid)
            }
        }

        // Adding a fragment for each item we want to display
        for (uid in displayList) {
            val fragment: PlanetFragment = PlanetFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
            if (uid == startUID)
                startItem = adapter.count-1
        }

        viewpager.adapter = adapter

        viewpager.setClipToPadding(false)
        viewpager.setPadding(50,0,50,0)
        viewpager.setPageMargin(25) // Otherwise planet views touch, as they go full screen. Can remove this if they no longer to

    }


    /** Called when the user taps the Make Pod button */
    fun makeFactory(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();


        val parent = view.parent as View
        val planet: GBPlanet = parent.tag as GBPlanet

        val universe = GBController.universe!!
        universe.makeFactory(planet)

        val message = "Ordered Factory on " + planet.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

    }

    /** Called when the user taps the Colonize button */
    fun colonize0(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val parent = view.parent as View
        val planet: GBPlanet = parent.tag as GBPlanet

        val universe = GBController.universe!!
        universe.landPopulation(planet, 0, 100)

        // Need to move drawing of overlay into OnDraw then do. But it's god mode, so not critical
        view.invalidate()

        val message = "God Level: Landing Xenos on " + planet.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    /** Called when the user taps the Colonize button */
    fun colonize1(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val parent = view.parent as View
        val planet: GBPlanet = parent.tag as GBPlanet

        val universe = GBController.universe!!
        universe.landPopulation(planet, 1, 100)

        // Need to move drawing of overlay into OnDraw then do. But it's god mode, so not critical
        // view.invalidate()

        val message = "God Level: Landing Impi on " + planet.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }
}
