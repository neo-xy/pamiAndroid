package pami.com.pami.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
class ShiftsToTake() : Parcelable {
    var date: Date = Date();
    var departmentName: String = "";
    //    lateinit var end:String;
//    lateinit var start:String;
    var id: String = "";
    var employeeId: String = "";
    var startDate: Date = Date();
    var endDate: Date = Date();

    constructor(parcel: Parcel) : this() {
        departmentName = parcel.readString()
        id = parcel.readString()
        employeeId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(departmentName)
        parcel.writeString(id)
        parcel.writeString(employeeId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShiftsToTake> {
        override fun createFromParcel(parcel: Parcel): ShiftsToTake {
            return ShiftsToTake(parcel)
        }

        override fun newArray(size: Int): Array<ShiftsToTake?> {
            return arrayOfNulls(size)
        }
    }
}