package pami.com.pami

import java.util.*

/**
 * Created by Pawel on 19/02/2018.
 */
class Company {
    var infoMessage:InfoMessage? = null;
    var companyId:String?=null;
}

class InfoMessage{
    var author:String=""
    var authorTel:String=""
    var date: Date?=null;
    var message:String="";
}