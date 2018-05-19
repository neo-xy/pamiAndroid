package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import pami.com.pami.models.CustomDateModel
import pami.com.pami.models.Department
import java.time.LocalDate
import java.util.*


@IgnoreExtraProperties
class Shift {
    var OBhours: Double = 0.0
    var OBmoney: Double = 0.0
    var OBnattHours: Double = 0.0
    var OBnattMoney: Double = 0.0
    var employeeSalary: Int = 0
    var department: Department = Department()
    var startTime: CustomDateModel = CustomDateModel()
    var endTime: CustomDateModel = CustomDateModel()
    var employeeId: String = ""
    var employmentType: String = ""
    var message: String? =null;
    var duration: Long = 0L
    var badge: String = ""
    var shiftId: String = ""
    var timeStempIn:Long=0
    var timeStempOut:Long=0
    var tax:Double=0.0;
    var shiftStatus:String=""

    var startDate:Date = Date()
    var endDate:Date = Date()



}