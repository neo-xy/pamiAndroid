package pami.com.pami.models

import java.util.*


object User {
    val firstName: String = ""
    val lastName: String = ""
    val employeeId: String = ""
    val city: String = ""
    val email: String = ""
    val salaries = mutableListOf<Salary>()
    val imgUrl: String = ""
    val employmentStatus: String = ""
    var employmentDate: Date = Date()
    var datesUnavailable = mutableListOf<Int>()
    var address: String = ""
    var personNummer: Long = 0
    var accountNr = ""
    var bankName = ""
    var clearNr = ""
    var registrationToken = ""
    var phoneNumber = ""
    var socialSecurityNumber: Long = 0;
    var latestCompanyIndex = 0;
    var companies = mutableListOf <UserCompany>();

}


object UserCompany {
    var companyId = "";
    var role = ""
}


