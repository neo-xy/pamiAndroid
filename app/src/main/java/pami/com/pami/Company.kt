package pami.com.pami

import java.util.*

class Company {
    var infoMessage:InfoMessage? = null
    var companyId:String?=null
}

class InfoMessage{
    var author:String=""
    var authorTel:String=""
    var date: Date?=null
    var message:String=""
}