package com.zwsi.gb.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.system.measureNanoTime

class ARBitmaps {
    companion object {

        var ready = false;

        private val surfaceBitmaps = HashMap<Int, Bitmap>()
        private val otherBitmaps = HashMap<Int, Bitmap>()
        private val raceBitmaps = HashMap<Int, Bitmap>()
        private val shipBitmaps = HashMap<Int, Bitmap>()

        val numberOfFrames = 150 // 10s rotation, 30fps -> up to 300 different angles/shipBitmaps! 600 @60fps
        var wheelBitmaps = arrayOfNulls<Bitmap>(numberOfFrames)

        var initTimes = mutableMapOf<String, Long>()

        fun surfaceBitmap(id: Int): Bitmap {
            return surfaceBitmaps[id]!!
        }

        fun otherBitmap(id: Int): Bitmap {
            return otherBitmaps[id]!!
        }

        fun raceBitmap(id: Int): Bitmap {
            return raceBitmaps[id]!!
        }

        fun shipBitmap(id: Int): Bitmap {
            return shipBitmaps[id]!!
        }

        fun wheelBitmap(id: Int): Bitmap {
            return wheelBitmaps[id]!!
        }

        fun loadBitmaps(context: Context) {
            // Do a better way. If it works, we replace above... Neet do figure out planet/star, where we divide by 2/1

            var h: Float
            var w: Float

            val density = context.resources.displayMetrics.densityDpi.toFloat()

            // Get star and planet
            val bmStar = BitmapFactory.decodeResource(context.getResources(), R.drawable.star)!!
            w = density / 420f * bmStar.getWidth()
            h = density / 420f * bmStar.getHeight()
            otherBitmaps[R.drawable.star] = Bitmap.createScaledBitmap(bmStar, w.toInt(), h.toInt(), true)!!

            val bmPlanet = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet)!!
            w = density / 420f * bmPlanet.getWidth() / 2
            h = density / 420f * bmPlanet.getHeight() / 2
            otherBitmaps[R.drawable.planet] = Bitmap.createScaledBitmap(bmPlanet, w.toInt(), h.toInt(), true)!!


            initTimes["iPS"] = measureNanoTime {
                surfaceBitmaps[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.desert)!!
                surfaceBitmaps[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.forest)!!
                surfaceBitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.gas)!!
                surfaceBitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.gas)!!
                surfaceBitmaps[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ice)!!
                surfaceBitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.land)!!
                surfaceBitmaps[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mountain)!!
                surfaceBitmaps[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock)!!
                surfaceBitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.water)!!
            }

            // get Races
            initTimes["iRB"] = measureNanoTime {
                val drawables = listOf<Int>(
                    R.drawable.xenost, R.drawable.impit, R.drawable.beetle, R.drawable.tortoise
                )
                for (i in drawables) {
                    val bm = BitmapFactory.decodeResource(context.getResources(), i)!!
                    w = density / 420f * bm.getWidth() / 30
                    h = density / 420f * bm.getHeight() / 30
                    raceBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            initTimes["iSB"] = measureNanoTime {
                val drawables = listOf<Int>(
                    R.drawable.podt, R.drawable.cruisert, R.drawable.factory,
                    R.drawable.beetlepod, R.drawable.shuttle, R.drawable.research, R.drawable.hq, R.drawable.battlestar,
                    R.drawable.wheel
                )
                for (i in drawables) {
                    val bm = BitmapFactory.decodeResource(context.getResources(), i)!!
                    w = density / 420f * bm.getWidth() / 6
                    h = density / 420f * bm.getHeight() / 6
                    shipBitmaps[i] = Bitmap.createScaledBitmap(bm, w.toInt(), h.toInt(), true)!!
                }
            }

            initTimes["iRB"] = measureNanoTime {
                for (i in 0 until numberOfFrames) {
                    wheelBitmaps[i] = shipBitmaps[R.drawable.wheel]!!.rotate((360.toFloat() / numberOfFrames * i))
                }
            }
            ready = true
        }
    }
}