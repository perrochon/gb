package com.zwsi.gb.feature

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import com.zwsi.gblib.GBController
import android.widget.Toast // TODO remove toast

class RaceActivity : AppCompatActivity(), OnGestureListener {

    internal lateinit var  gestureDetector:GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)
        gestureDetector = GestureDetector(this@RaceActivity, this@RaceActivity)

        val intent = getIntent()
        val raceID = intent.getIntExtra("race", -1)

        val imageView = findViewById<ImageView>(R.id.RaceView)

        if (raceID == 0)
            imageView.setImageResource(R.drawable.xenost)
        else
            imageView.setImageResource(R.drawable.impit)


        val universe = GBController.universe
        val races = universe!!.racesArray
        val r = races[raceID]

        var stats = findViewById<TextView>(R.id.RaceStats)
        var paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append("Name : " + (r!!.name) + "\n")
        stats.append("Type : " + (r.birthrate) + "\n")
        stats.append("Size : " + (r.explore) + "\n")
        stats.append("Owner: " + (r.absorption) + "\n")

        stats = findViewById<TextView>(R.id.RaceBackground)
        paint = stats.paint
        paint.textSize = 40f

        stats.append("\n")
        stats.append(r.description)

    }

    override fun onFling(motionEvent1:MotionEvent, motionEvent2:MotionEvent, X:Float, Y:Float):Boolean {
        if (motionEvent1.getX() - motionEvent2.getX() > 50)
        {
            //Toast.makeText(this@RaceActivity, "You Swiped Left!", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RaceActivity::class.java)
            intent.putExtra("race", 1)
            startActivity(intent)

            return true
        }
        if (motionEvent2.getX() - motionEvent1.getX() > 50)
        {
            //Toast.makeText(this@RaceActivity, "You Swiped Right!", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RaceActivity::class.java)
            intent.putExtra("race", 0)
            startActivity(intent)

            return true
        }
        if (motionEvent1.getY() - motionEvent2.getY() > 50)
        {
            //Toast.makeText(this@RaceActivity, "You Swiped up!", Toast.LENGTH_LONG).show()
            return true
        }
        if (motionEvent2.getY() - motionEvent1.getY() > 50)
        {
            //Toast.makeText(this@RaceActivity, "You Swiped Down!", Toast.LENGTH_LONG).show()
            return true
        }
        else
        {
            return true
        }
    }
    override fun onLongPress(arg0:MotionEvent) {
        // TODO Auto-generated method stub

    }
    override fun onScroll(arg0:MotionEvent, arg1:MotionEvent, arg2:Float, arg3:Float):Boolean {
        // TODO Auto-generated method stub
        return false
    }
    override fun onShowPress(arg0:MotionEvent) {
        // TODO Auto-generated method stub

    }
    override fun onSingleTapUp(arg0:MotionEvent):Boolean {
        // TODO Auto-generated method stub
        return false
    }
    override fun onTouchEvent(motionEvent:MotionEvent):Boolean {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(motionEvent)
    }
    override fun onDown(arg0:MotionEvent):Boolean {
        // TODO Auto-generated method stub
        return false
    }


}
