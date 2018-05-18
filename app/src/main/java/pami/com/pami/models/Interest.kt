package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
class Interest {
     var employeeId:String="";
     var shiftToTakeId:String="";
     var dateAdded: Date=Date();
     var department:String="";
     var timeInterval:String="";
     var interestId:String="";
     var startDate:Date = Date();
     var endDate:Date = Date();
}