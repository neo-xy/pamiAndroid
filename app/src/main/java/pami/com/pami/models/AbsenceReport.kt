package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class AbsenceReport {
    var startDate:Date = Date()
    var endDate:Date = Date()
    var employeeId = ""
    var dateAdded = Date()
    var reportId = ""
    var type = SickType.Child
    var firstName = ""
    var lastName = ""
    var socialSecurityNumber = ""
}

class SickType{

    companion object {
        val Normal= 0
        val Child =1
    }

}