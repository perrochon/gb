package com.zwsi.gb.feature

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBStar

class StarsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private var startItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Stars"
        val titleTextView = findViewById<TextView>(R.id.text_stars_title)
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
        if (intent.hasExtra("stars")) {
            displayList = intent.getIntegerArrayListExtra("stars")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allStars) {
                displayList.add(i.uid)
            }
        }

        // Adding a fregment for each item we want to display
        for (uid in displayList) {
            val fragment: StarFragment = StarFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
            if (uid == startUID)
                startItem = adapter.count-1
        }

        viewpager.adapter = adapter

        viewpager.setClipToPadding(false)
        viewpager.setPadding(50,0,50,0)

    }

    /** Called when the user taps the Go to Planets button */
    fun goToLocation(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val intent = Intent(this, PlanetsSlideActivity::class.java)

        val parent = view.parent.parent as View // TODO there must be a better (not layout dependent) way than this
        val star = parent.tag as GBStar

        Toast.makeText(view.context, "Going to planets of " + star.name, Toast.LENGTH_SHORT).show()

        val displayUID = ArrayList<Int>()
        for (planet in star.starPlanets) {
            displayUID.add(planet.uid)
        }
        intent.putExtra("planets", displayUID)
        intent.putExtra("title", "Planets of " + star.name)
        startActivity(intent)

    }

    /** Called when the user taps the Go to Planets button */
    fun goToShips(view: View) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        val intent = Intent(this, ShipsSlideActivity::class.java)

        val parent = view.parent.parent as View // TODO there must be a better (not layout dependent) way than this
        val star = parent.tag as GBStar

        Toast.makeText(view.context, "Going to ships in system " + star.name, Toast.LENGTH_SHORT).show()

        val displayUID = ArrayList<Int>()
        for (ship in star.starShips) {
            displayUID.add(ship.uid)
        }
        for (planet in star.starPlanets) {
            for (ship in planet.orbitShips){
                displayUID.add(ship.uid)
            }
            for (ship in planet.landedShips){
                displayUID.add(ship.uid)
            }
        }

        intent.putExtra("ships", displayUID)
        intent.putExtra("title", "Ships in " + star.name)
        startActivity(intent)

    }


}