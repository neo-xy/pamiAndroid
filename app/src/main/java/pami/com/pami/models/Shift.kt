package pami.com.pami.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import pami.com.pami.models.CustomDateModel
import pami.com.pami.models.Department
import java.time.LocalDate
import java.util.*


@IgnoreExtraProperties
class Shift {
    var department: Department = Department()
    var employeeId: String = ""
    var message: String? = null
    var duration: Double = 0.0
    var badge: String = ""
    var shiftId: String = ""
    var tax: Double = 0.0;

    var netto = 0.0
    var brutto = 0.0
    var socialNumber = ""
    var startDate: Date = Date()
    var endDate: Date = Date()
    var start: Calendar = Calendar.getInstance()
    var end: Calendar = Calendar.getInstance()

    var shiftStatus: Int? = null;
}

class ShiftStatus {
    companion object {
        val Scheduled = 0;
        val Active = 1
        val Accepted = 2
        val Rejected = 3
        val ClockedOut = 4
    }

}