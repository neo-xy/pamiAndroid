package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
object User {
    var firstName: String = ""
    var lastName: String = ""
    var employeeId: String = ""
    var city: String = ""
    var email: String = ""
    var salaries = mutableListOf<Salary>()
    var imgUrl: String = ""
    var employmentStatus: String = ""
    var employmentDate: Date = Date()
    var datesUnavailable = mutableListOf<Int>()
    var address: String = ""
    var personNummer: Long = 0
    var accountNr = ""
    var bankName = ""
    var clearNr = ""
    var registrationToken = ""
    var phoneNumber = ""
    var socialSecurityNumber= "";
    var latestCompanyIndex = 0;
    var companies = mutableListOf <UserCompany>();
    var companyId = ""
    var role ="boss"
}


class UserCompany {
    var companyId = "";
    var role = ""
}


