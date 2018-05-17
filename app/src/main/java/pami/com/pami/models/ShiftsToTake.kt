package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
class ShiftsToTake {
    lateinit var date: Date;
    lateinit var departmentName:String;
    lateinit var end:String;
    lateinit var start:String;
    lateinit var id:String;
    lateinit var employeeId:String;
}