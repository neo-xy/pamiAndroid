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

    var netto = 0L
    var brutto = 0L


    var startDate: Date = Date()

    var endDate: Date = Date()
    var start: Calendar = Calendar.getInstance()
    var end: Calendar = Calendar.getInstance()

    var shiftStatus: ShiftStatus? = null;

}

enum class ShiftStatus(val value: String) {
    original("original"),
    schemalagt("schemalagt"),
    aktiv("aktiv"),
    avvistat("avvistat"),
    accepterat("accepterat"),
    utstämplat("utstämplat")

}