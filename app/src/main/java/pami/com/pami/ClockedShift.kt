package pami.com.pami

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Created by Pawel on 13/03/2018.
 */

@IgnoreExtraProperties
class ClockedShift {
    var startTime: CustomDateModel= CustomDateModel()
    var endTime: CustomDateModel= CustomDateModel()
    var employeeId=""
    var firstName=""
    var lastName=""
    var correspondingShiftId=""
    var clockedShiftId=""
    var messageIn = ""
    var messageOut = ""
    var companyId=""
    var timeStempIn:Long=0;
    var timeStempOut:Long=0;
}