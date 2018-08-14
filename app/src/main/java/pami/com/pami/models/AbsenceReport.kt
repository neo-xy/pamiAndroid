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
    var type:SickType = SickType.CHILD
    var firstName = ""
    var lastName = ""
    var socialSecurityNumber = 0L
}

enum class SickType{
    NORMAL,CHILD
}