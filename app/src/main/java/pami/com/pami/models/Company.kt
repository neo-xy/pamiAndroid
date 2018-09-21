package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*
@IgnoreExtraProperties
class Company {
    var infoMessage: InfoMessage? = null
    var companyId:String?=null
    var gpsLocation:PamiLocation? = null
    var wifiName:String? = null
    var salt:String? = null
    var locationType:Int? = null
    var sickAccess = mutableListOf<Int>()

}

class InfoMessage{
    var author:String=""
    var authorTel:String=""
    var date: Date?=null
    var message:String=""
}

class PamiLocation{
    var latitude = 0.0
    var longitude = 0.0
    var address = ""
}

class LocationType{
    companion object {

        val None=0
        val Wifi=1
        val Gps=2
        val Tablet=3
    }

}

class SickAccess{
    companion object {
        val FullTime=0
        val PartTime=1
        val hourly=2
    }
}