package com.zwsi.gb.feature

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBShip

class ShipsSlideActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ships_slide)

        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else "Ships"
        val titleTextView = findViewById<TextView>(R.id.text_ships_title)
        titleTextView.setText(title)

        initViews()
        setupViewPager()

        val shipUID = getIntent().getIntExtra("shipUID", -1)
        if (shipUID > 0) {
            viewpager.setCurrentItem(shipUID)
        }

        if (GBController.universe.allShips.size == 0) {
            val hintText = this.findViewById<TextView>(R.id.hintTextView)
            hintText.visibility = (TextView.VISIBLE)
            hintText.setText(
                "You haven't built any ships yet.\n\n " +
                        "Start making ships by creating a Factory on your home planet. " +
                        "That Factory will be your first ship. Then order the Factory to make other ships. " +
                        "Remember that you have to give the order to build the factory first, then click on " +
                        "[Do] so your minions can execute your orders."
            )
        }


    }

    private fun initViews() {
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = MyFragmentPagerAdapter(getSupportFragmentManager())

        val universe = GBController.universe

        // Figure out which items to display
        val displayList: ArrayList<Int>
        if (intent.hasExtra("ships")) {
            displayList = intent.getIntegerArrayListExtra("ships")
        } else {
            displayList = ArrayList<Int>()
            for (i in GBController.universe.allShips) {
                displayList.add(i.uid)
            }
        }

        // Adding a fregment for each item we want to display
        for (uid in displayList) {
            val fragment: ShipFragment = ShipFragment.newInstance(uid.toString())
            adapter.addFragment(fragment, uid.toString())
        }


        viewpager.adapter = adapter

    }

    /** Called when the user taps the Go button */
    fun goToLocation(view: View) {

        val parent = view.parent as View
        val ship = parent.tag as GBShip

        Toast.makeText(view.context, ship.getLocation(), Toast.LENGTH_SHORT).show()


        if ((ship.level == 1) or (ship.level == 2)) {
            val intent = Intent(this, PlanetsSlideActivity::class.java)

            intent.putExtra("title", "Planet")

            val displayUID = ArrayList<Int>()
            displayUID.add(ship.locationuid)
            intent.putExtra("planets", displayUID)

            intent.putExtra("planetUID", ship.locationuid)
            startActivity(intent)
        }
        if (ship.level == 3) {
            val intent = Intent(this, StarsSlideActivity::class.java)
            intent.putExtra("starUID", ship.locationuid)
            startActivity(intent)
        }


    }

    /** Called when the user taps the make Pod button */
    fun makePod(view: View) {

        val parent = view.parent as View
        val ship: GBShip = parent.tag as GBShip

        val message = "Ordered Pod in Factory " + ship.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

        val universe = GBController.universe
        universe.makePod(ship)

    }

    /** Called when the user taps the make Cruiser button */
    fun makeCruiser(view: View) {

        val parent = view.parent as View
        val ship: GBShip = parent.tag as GBShip

        val message = "Ordered Cruiser in Factory " + ship.name
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()


    }

}