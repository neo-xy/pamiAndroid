package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
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
    var companyId = ""
    var role ="boss"

}


object UserCompany {
    var companyId = "";
    var role = ""
}


