package com.zwsi.gb.feature

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.zwsi.gb.feature.GBViewModel.Companion.viewShips
import com.zwsi.gblib.GBController

class ShipsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private var startItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ships_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Ships"
        val titleTextView = findViewById<TextView>(R.id.text_ships_title)
        titleTextView.setText(title)

        initViews()
        setupViewPager()
        viewpager.setCurrentItem(startItem)

        if (GBViewModel.viewShips.size == 0) {
            val hintText = this.findViewById<TextView>(R.id.hintTextView)
            hintText.visibility = (TextView.VISIBLE)
            hintText.setText("There are no ships here.\n\n ")
        }

    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())
        val startUID = getIntent().getIntExtra("UID", -1)

        // Figure out which items to display
        val displayList: ArrayList<Int>
        if (intent.hasExtra("ships")) {
            displayList = intent.getIntegerArrayListExtra("ships")
        } else {
            displayList = ArrayList<Int>()
            for ((uid, _) in viewShips.filterValues { it.health > 0 }) {
                displayList.add(uid)
            }
        }

        // Adding a fregment for each item we want to display
        for (uid in displayList) {
            val fragment: ShipFragment = ShipFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
            if (uid == startUID)
                startItem = adapter.count - 1
        }

        viewpager.adapter = adapter

        viewpager.setClipToPadding(false)
        viewpager.setPadding(50, 0, 50, 0)

    }

    fun goToLocationShip( @Suppress("UNUSED_PARAMETER")view: View) {
        // TODO deprecated
//        GlobalButtonOnClick.goToLocationShip(view)
    }

    fun makePod(view: View) {
        GlobalButtonOnClick.makePod(view)
    }

    fun makeCruiser(view: View) {
        GlobalButtonOnClick.makeCruiser(view)
    }

    /** Called when the user taps the fly  To button */
    fun flyTo(view: View) {
        GlobalButtonOnClick.flyTo(view)
    }
}
