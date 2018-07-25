package pami.com.pami

import android.util.Log
import pami.com.pami.models.CustomDateModel
import pami.com.pami.models.Salary
import pami.com.pami.models.Shift
import java.text.DecimalFormat
import java.util.*
import kotlin.Comparator

object Shared {

    val df: DecimalFormat = DecimalFormat("00")

    fun sortShifts(shifts: MutableList<Shift>): MutableList<Shift> {
        Collections.sort(shifts, object : Comparator<Shift> {
            override fun compare(p0: Shift, p1: Shift): Int {
                if (p0.start.get(Calendar.YEAR) > p1.start.get(Calendar.YEAR)) {
                    return 1
                } else if (p0.start.get(Calendar.YEAR) < p1.start.get(Calendar.YEAR)) {
                    return -1
                } else {
                    if (p0.start.get(Calendar.MONTH) > p1.start.get(Calendar.MONTH)) {
                        return 1
                    } else if (p0.start.get(Calendar.MONTH) < p1.start.get(Calendar.MONTH)) {
                        return -1
                    } else {

                        if (p0.start.get(Calendar.DATE) > p1.start.get(Calendar.DATE)) {
                            return 1
                        } else if (p0.start.get(Calendar.DATE) < p1.start.get(Calendar.DATE)) {
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
        val displayedDuration: String
        val hour = duration.toInt()
        var minute = endTime.minute - startTime.minute
        if (endTime.minute < startTime.minute) {
            minute = endTime.minute + 60 - startTime.minute
        }
        val df = DecimalFormat("00")
        displayedDuration = df.format(hour) + ":" + df.format(minute)
        return displayedDuration
    }

    fun timestempToClockString(timeStemp: Long): String {
        val date = Date(timeStemp)
        val cal = Calendar.getInstance()
        cal.time = date

        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        return this.df.format(month) + "/" + this.df.format(day) + "  " + this.df.format(hour) + ":" + this.df.format(minute)
    }

    fun getHour(timeStemp: Long): Int {
        val date = Date(timeStemp)
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinute(timeStemp: Long): Int {
        val date = Date(timeStemp)
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.MINUTE)
    }

    fun sortSalaries(salaries: MutableList<Salary>): MutableList<Salary> {

        Collections.sort(salaries, object : Comparator<Salary> {
            override fun compare(p0: Salary?, p1: Salary?): Int {
                if (p0!!.startDate.time < p1!!.startDate.time) {
                    return 1
                } else {
                    return -1
                }
            }
        })
        salaries.forEach {
            Log.d("pawell",it.employmentType.toString())
        }

        return salaries
    }


}