package pami.com.pami.models

import pami.com.pami.models.CustomDateModel
import pami.com.pami.models.Salary


object User{
    val firstName: String = ""
    val lastName: String = ""
    val employeeId:String=""
    val city:String = ""
    val email:String=""
    val salaries = mutableListOf<Salary>()
    val imgUrl:String=""
    val employmentStatus:String=""
    val employmentDate: CustomDateModel = CustomDateModel()
    val companyId:String=""
    var datesUnavailable= mutableListOf<Int>()
    var address:String=""
    var personNummer:Long=0
    var accountNr=""
    var bankName=""
    var clearNr=""
    var role=""
    var registrationToken=""
    var phoneNumber=""
    var socialSecurityNumber:Long=0;

}





