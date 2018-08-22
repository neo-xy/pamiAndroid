package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class ClockedShift {
    var employeeId=""
    var firstName=""
    var lastName=""
    var correspondingShiftId=""
    var clockedShiftId=""
    var messageIn = ""
    var messageOut = ""
    var timeStempIn:Long=0
    var timeStempOut:Long=0
    var startDate:Date = Date()
    var endDate:Date = Date()
    var shiftStatus: ShiftStatus? = null;
   var logs = mutableListOf<ShiftLog>()
}

 class ShiftLog {

    var startDate: Date?=null
    var endDate: Date? = null
    var bossId: String? = null
    var bossFirstName: String? = null
    var bossLastName: String? = null
    var message: String? =null
    var date: Date? = null
     var shiftStatus: ShiftStatus? = null;
}