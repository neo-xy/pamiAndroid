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
    var locationType:String? = null

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

enum class LocationType{
    none, gps, wifi
}