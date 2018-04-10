package pami.com.pami

import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
class Shift {
    var Obhours: Double = 0.0
    var Obmoney: Double = 0.0
    var ObnattHours: Double = 0.0
    var ObnattMoney: Double = 0.0
    var employeeSalary: Int = 0
    var department: Department = Department()
    var startTime: CustomDateModel = CustomDateModel()
    var endTime: CustomDateModel = CustomDateModel()
    var employeeId: String = ""
    var employmentType: String = ""
    var message: String = ""
    var duration: Double = 0.0
    var badge: String = ""
    var shiftId: String = ""
    var timeStempIn:Long=0
    var timeStempOut:Long=0

}