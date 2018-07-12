package pami.com.pami.models

import pami.com.pami.enums.EmploymentType
import java.util.*

class Salary {
    var dataKey: String? = ""
    var employeeId: String? = ""
    var duration: Int? = 0
    var month: Int? = 0
    var year: Int? = 0
    var obHours: Int? = 0
    var obMoney: Int? = 0
    var obNightHours: Int? = 0
    var obNightMmoney: Int? = 0
    var total: Long? = 0
    var salary = 0;
    var bossId: String? = ""
    var changeDate: Date? = null
    var employmentType: EmploymentType? = null
    var salaryMessage: String? = ""
    var startDate: Date = Date()
    var tax: Int? = 0

}
