package pami.com.pami.models

import pami.com.pami.enums.ClockedInStatus

class Employee {
    val firstName:String=""
    val lastName:String=""
    val employeeId:String=""
    val socialSecurityNumber="";
    val clockedInStatus: ClockedInStatus = ClockedInStatus.CLOCKED_OUT
}