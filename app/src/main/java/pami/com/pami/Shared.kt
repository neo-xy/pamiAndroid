package pami.com.pami

import android.util.Log
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Pawel on 20/02/2018.
 */
object Shared {

    val df: DecimalFormat = DecimalFormat("00")

   fun sortShifts(shifts:MutableList<Shift>):MutableList<Shift>{
      val gg =  Collections.sort(shifts, object : Comparator<Shift> {
            override fun compare(p0: Shift, p1: Shift): Int {
                if (p0.startTime.year > p1.startTime.year) {
                    return 1
                } else if (p0.startTime.year < p1.startTime.year) {
                    return -1
                } else {
                    if (p0.startTime.month > p1.startTime.month) {
                        return 1
                    } else if (p0.startTime.month < p1.startTime.month) {
                        return -1
                    } else {

                        if (p0.startTime.day > p1.startTime.day) {
                            return 1
                        } else if (p0.startTime.day < p1.startTime.day) {
                            return -1
                        } else {
                            return 0
                        }
                    }
                }
            }
        })
       return shifts
    }

    fun calculateDuration(startTime: CustomDateModel, endTime: CustomDateModel, duration: Double): CharSequence? {
        var displayedDuration =""
        val hour = duration.toInt()
        var minute = endTime.minute -startTime.minute
        if(endTime.minute<startTime.minute){
            minute = endTime.minute+60-startTime.minute
        }
        var df = DecimalFormat("00");
        displayedDuration = df.format(hour)+":"+ df.format(minute)
        return displayedDuration
    }



}