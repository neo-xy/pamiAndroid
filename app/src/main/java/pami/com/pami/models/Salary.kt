package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import pami.com.pami.enums.EmploymentType
import java.util.*
@IgnoreExtraProperties
object Salary {
    var employeeId: String? = ""
    var duration: Int? = 0
    var month: Int? = 0
    var year: Int? = 0
    var total: Long? = 0
    var salary = 0;
    var obExtra:Boolean? = false;
    var monthSalary:Int= 0;

    var employmentType: EmploymentType? = null
    var startDate: Date = Date()
    var tax: Int? = 0
    var partValue:Int? =0;

}
