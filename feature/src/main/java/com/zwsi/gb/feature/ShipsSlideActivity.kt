package com.zwsi.gb.feature

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.zwsi.gblib.GBController
import com.zwsi.gblib.GBLocation.Companion.LANDED
import com.zwsi.gblib.GBLocation.Companion.ORBIT
import com.zwsi.gblib.GBLocation.Companion.SYSTEM
import com.zwsi.gblib.GBPlanet
import com.zwsi.gblib.GBShip
import com.zwsi.gblib.GBUniverse

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
        val startUID = getIntent().getIntExtra("UID", -1)

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
            if (uid == startUID)
                startItem = adapter.count-1
        }

        viewpager.adapter = adapter

        viewpager.setClipToPadding(false)
        viewpager.setPadding(50,0,50,0)

    }

    /** Called when the user taps the Go button */
    fun goToLocation(view: View) {

        val parent = view.parent as View
        val ship = parent.tag as GBShip

        Toast.makeText(view.context, ship.loc.getLocDesc(), Toast.LENGTH_SHORT).show()

        if ((ship.loc.level == LANDED) or (ship.loc.level == ORBIT)) {
            val intent = Intent(this, PlanetsSlideActivity::class.java)

            intent.putExtra("title", "Planet")

            val displayUID = ArrayList<Int>()
            displayUID.add(ship.loc.refUID)
            intent.putExtra("planets", displayUID)

            intent.putExtra("planetUID", ship.loc.refUID)
            startActivity(intent)
        }
        if (ship.loc.level == SYSTEM) {
            val intent = Intent(this, StarsSlideActivity::class.java)
            intent.putExtra("starUID", ship.loc.refUID)
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

    /** Called when the user taps the fly  To button */
    fun flyTo(view: View) {

        val parent = view.parent as View
        val ship: GBShip = parent.tag as GBShip

        var spinner = view.tag as Spinner
        var destination = spinner.selectedItem.toString()
        var planet : GBPlanet? = null

        for (p in GBController.universe.allPlanets) { // TODO this is wasteful. Need to refactor to locations
            if (p.name == destination)
                planet = p
        }

        Toast.makeText(view.context, "Ordered " + ship.name + " to fly to " + planet!!.name, Toast.LENGTH_LONG).show()

        GBController.universe.flyShip(ship, planet)
    }

}