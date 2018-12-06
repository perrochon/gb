package com.zwsi.gb.feature

import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.zwsi.gblib.GBController

class GlobalButtonOnClick {

    companion object {

        fun doUniverse(view: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDelay) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();


            //output.setText("") // TODO: But output back in...

            val message = "Executing Orders"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            Thread(Runnable {

                // Capture output from tester in an byte array
//            val baos = ByteArrayOutputStream()
//            val ps = PrintStream(baos)
//            System.setOut(ps)

                if (GBController.universe.autoDo) {
                    return@Runnable
                }

                GBController.doUniverse()

                view.post {
                    GBViewModel.update()
                }

//            System.out.flush()

//            view.post { // This is going to the button's UI thread, which is the same as the ScrollView
//                // output.append(baos.toString())
//            }

                view.post {
                    // Worth making a string in this thread and post just result?
                    for (s in GBController.universe.news)
                    //output.append(s)

                        MissionController.checkMissionStatus()
                    //output.append(MissionController.getCurrentMission(this))
                }

            }).start()

        }
    }

}