package pami.com.pami

import com.google.firebase.firestore.IgnoreExtraProperties

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
}