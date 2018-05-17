package pami.com.pami.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class UnavailableDate {
    var employeeId: String = ""
    var date: Date? = null
    var id: String = ""
    var markDate: Date? = null
}