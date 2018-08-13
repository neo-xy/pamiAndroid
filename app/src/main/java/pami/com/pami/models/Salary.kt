package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import pami.com.pami.enums.EmploymentType
import java.util.*
@IgnoreExtraProperties
class Salary {

    var salary:Int = 0
    var partValue:Int? = 0
    var startDate: Date = Date()
    var employmentType: EmploymentType? = null

    //var employeeId: String? = ""
//    var bossId:String=""
//    var changeDate: Date = Date()
//    var tax: Int? = 0
//    var obExtra:Boolean? = false;
//    var monthSalary:Int= 0;
//    var salaryMessage:String? =""

}
