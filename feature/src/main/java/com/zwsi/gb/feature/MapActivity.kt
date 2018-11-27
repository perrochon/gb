package com.zwsi.gb.feature

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP

import com.zwsi.gblib.GBController

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val imageView = findViewById<SubsamplingScaleImageView>(R.id.imageViewScale)!!

        val fullResImage = ImageSource.resource(R.drawable.orion18000x)!!
        val lowResImage = ImageSource.resource(R.drawable.orion1024)!!

        fullResImage.dimensions(18000,18000)

        imageView.setImage(fullResImage, lowResImage);

        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP)
        imageView.setScaleAndCenter(2f, PointF(12000f, 12000f))

        val stars = GBController.universe.allStars
        for (s in stars) {
//            canvas.drawBitmap(bs, s!!.x.toFloat(), s.y.toFloat(), null)
//            canvas.drawText(s!!.name,s!!.x.toFloat() + 30, s!!.y.toFloat()+10, paint)
        }

    }


}
