package pami.com.pami.models

import java.util.*

class SickReport {
    var rangeStart:Date = Date()
    var rangeEnd:Date = Date()
    var employeeId = ""
    var dateAdded = Date()
    var reportId = ""
    var type:SickType = SickType.CHILD
}

enum class SickType{
    NORMAL,CHILD
}