package pami.com.pami

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.Observable.create
import pami.com.pami.models.*
import java.io.File
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider






object FirebaseController {

    var shifts = mutableListOf<Shift>()
    var departments: MutableList<Department>? = null
    var employees: MutableList<Employee> = mutableListOf()
    var colleague = mutableListOf<Colleague>()
    var salaries = mutableListOf<Salary>()
    var unavailableShifts = mutableListOf<UnavailableDate>()
    var accteptedShifts = mutableListOf<Shift>()
    var interests = mutableListOf<Interest>()

    var dayInfoMesages = mutableListOf<DayInfoMessage>()


    fun getUser(): Observable<Boolean> {


        Log.d("pawell", "getUSEr 1")
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        firestore.firestoreSettings = settings

        return Observable.create<Boolean> {
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).addSnapshotListener { p0, _ ->
                Log.d("pawell", "getUSEr 2")
                if (p0!!.exists()) {
                    p0.toObject(User::class.java)
                    it.onNext(true)
                } else {
                }
            }
        }
    }

    fun getSalaries(): Observable<MutableList<Salary>> {
        return create { sub ->
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("salaries").get().addOnCompleteListener { p0 ->
                val results = p0.result.toObjects(Salary::class.java)
                sub.onNext(results)
            }
        }
    }

    fun getUserShifts(): Observable<MutableList<Shift>> {
        return create {
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("scheduledShifts").addSnapshotListener { p0, _ ->
                shifts.removeAll(shifts)

                p0?.forEach { snapShot ->
                    val shift = snapShot.toObject(Shift::class.java)
                    shift.shiftId = snapShot.id
                    var cal = Calendar.getInstance()
                    cal.time = shift.startDate
                    shift.start = cal
                    cal = Calendar.getInstance()
                    cal.time = shift.endDate
                    shift.end = cal
                    shifts.add(shift)
                }
                it.onNext(shifts)
            }
        }
    }

    fun getScheduledShifts(): Observable<MutableList<Shift>> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("scheduledShifts").addSnapshotListener { p0, _ ->

                val schedueledShifts = mutableListOf<Shift>()
                p0?.forEach { snapShot ->
                    val shift = snapShot.toObject(Shift::class.java)
                    shift.shiftId = snapShot.id
                    var cal = Calendar.getInstance()
                    cal.time = shift.startDate
                    shift.start = cal
                    cal = Calendar.getInstance()
                    cal.time = shift.endDate
                    shift.end = cal
                    schedueledShifts.add(shift)
                }
                it.onNext(schedueledShifts)
            }
        }
    }

    fun getCompany(): Observable<Company> {
        return Observable.create<Company> {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).addSnapshotListener { p0, _ ->
                if (p0 != null) {
                    Log.d("pawell", "eee " + LocationType.None)
                    val comp = p0.toObject(Company::class.java)
                    if (comp != null) {
                        it.onNext(comp)
                    }
                }
            }
        }
    }

    fun setUpDepartments(): Observable<Boolean> {
        return Observable.create {
            val obs = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("departments").addSnapshotListener { p0, _ ->
                departments = p0?.toObjects(Department::class.java)
                obs.onNext(true)
            }
        }
    }

    fun setUpEmployees() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").addSnapshotListener { p0, _ ->
            employees = p0?.toObjects(Employee::class.java) as MutableList<Employee>
        }
    }

    fun updateUnavailableDates(us: UnavailableDate) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("datesUnavailable").add(us)
                .addOnCompleteListener {
                    FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("datesUnavailable").document(it.result.id).set(us)
                }
    }

    fun getUanavailableDates(): Observable<MutableList<UnavailableDate>> {
        return create {
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("datesUnavailable").addSnapshotListener { p0, _ ->
                unavailableShifts.removeAll(unavailableShifts)
                p0?.forEach { snapshot ->
                    val us = snapshot.toObject(UnavailableDate::class.java)
                    us.id = snapshot.id
                    unavailableShifts.add(us)
                    it.onNext(unavailableShifts)
                }
            }
        }
    }

    fun removeUnavailableDate(us: UnavailableDate) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("datesUnavailable").document(us.id).delete()
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("datesUnavailable").document(us.id).delete()
    }

    fun saveImgUrl(photoUrl: Uri?) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("imgUrl", photoUrl.toString())
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").document(User.employeeId).update("imgUrl", photoUrl.toString())
    }

    fun setUpColleagues() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").addSnapshotListener { p0, _ ->
            if (p0 !== null) {
                colleague = p0.toObjects(Colleague::class.java)
            }
        }
    }

    fun setupSalleries() {
        //TODO change employeeID to UserId
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("salaries").get().addOnCompleteListener {
            if (it.isSuccessful) {
                this.salaries = it.result.toObjects(Salary::class.java)
            }
        }
    }

    fun updateLastLoginDate(lastLoginDate: Date) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").document(User.employeeId).update("lastLoginDate", lastLoginDate)
    }

    fun addToClockedInShifts(clockedShift: ClockedShift): Observable<String> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("activeShifts")
                    .add(clockedShift).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            it.onNext(task.result.id)
                        } else {
                            it.onNext("")
                        }
                    }
        }
    }

    fun getClockedInShifts(): Observable<MutableList<ClockedShift>> {
        return Observable.create { emiter ->
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("activeShifts").addSnapshotListener { p0, _ ->
                val clockedShifts = mutableListOf<ClockedShift>()
                if (p0 != null) {
                    p0.forEach { snapShot ->
                        val cs = snapShot.toObject(ClockedShift::class.java)
                        cs.clockedShiftId = snapShot.id
                        Log.d("pawell", "cdate: " + cs.startDate)
                        clockedShifts.add(cs)
                    }
                    emiter.onNext(clockedShifts)
                }
            }
        }
    }

    fun removeShiftFromClockedInShifts(clockedShift: ClockedShift): Observable<Boolean> {
        return Observable.create {
            val emitter = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("activeShifts").document(clockedShift.clockedShiftId).delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emitter.onNext(true)
                } else {
                    emitter.onNext(false)
                }
            }
        }
    }

    fun addShiftsToAccept(clockedShift: ClockedShift): Observable<Boolean> {
        clockedShift.shiftStatus = ShiftStatus.ClockedOut
        val shiftLog = ShiftLog()
        shiftLog.bossId = User.employeeId
        shiftLog.bossFirstName = User.firstName
        shiftLog.bossLastName = User.lastName
        shiftLog.shiftStatus = ShiftStatus.ClockedOut

        return Observable.create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("shiftsToAccept").add(clockedShift).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    it.onNext(true)
                } else {
                    it.onNext(false)
                }
            }
        }
    }

    fun getAcceptedShifts(): Observable<MutableList<Shift>> {
        return create { it ->
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("shifts").addSnapshotListener { p0, _ ->
                if (p0 != null) {
                    accteptedShifts = p0.toObjects(Shift::class.java)
                    Log.d("pawell", "fff " + ShiftStatus.Accepted)
                    accteptedShifts.filter { shift -> shift.shiftStatus == ShiftStatus.Accepted }
                    it.onNext(accteptedShifts)
                }
            }
        }
    }

    fun updateRegistrationToken(token: String) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("refreshToken", token)
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").document(User.employeeId)
                .update("refreshToken", token)
    }

    fun getShiftsToTake(): Observable<MutableList<ShiftsToTake>> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("shiftsToTake").addSnapshotListener { p0, _ ->
                if (p0 != null) {
                    val shiftsToTake = mutableListOf<ShiftsToTake>()
                    p0.forEach { snapShot ->
                        val id = snapShot.id
                        val shift = snapShot.toObject(ShiftsToTake::class.java)
                        if (shift.startDate > Date()) {
                            shift.id = id
                            shiftsToTake.add(shift)
                        }
                    }
                    it.onNext(shiftsToTake)
                }
            }
        }
    }

    fun getInterests(): Observable<MutableList<Interest>> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("interests").addSnapshotListener { p0, _ ->
                if (p0 != null) {
                    val interestList = mutableListOf<Interest>()
                    p0.forEach { snapShot ->
                        val id = snapShot.id
                        val interest = snapShot.toObject(Interest::class.java)
                        if (User.employeeId == interest.employeeId) {
                            interest.interestId = id
                            interestList.add(interest)
                        }
                    }
                    interests = interestList
                    it.onNext(interestList)
                }
            }
        }
    }

    fun addIntress(interest: Interest) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("interests").add(interest)
    }

    fun removeInterest(interest: Interest) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("interests").document(interest.interestId).delete()
    }

    fun getAbsenceReports(): Observable<MutableList<AbsenceReport>> {
        return create {
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("absenceReports").addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot != null) {
                    val reports = mutableListOf<AbsenceReport>()
                    querySnapshot.forEach { snapShot ->
                        val report: AbsenceReport = snapShot.toObject(AbsenceReport::class.java)
                        report.reportId = snapShot.id
                        reports.add(report)
                    }
                    it.onNext(reports)
                }
            }
        }
    }


    fun addAbsenceReport(absenceReport: AbsenceReport) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("absenceReports").add(absenceReport).addOnCompleteListener {
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("absenceReports").document(it.result.id).set(absenceReport)
        }
    }

    fun removeAbsenceReport(reportId: String) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("absenceReports").document(reportId).delete()
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("absenceReports").document(reportId).delete()
    }

    fun getInfoMessagesForDay() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("infoMessageDay").addSnapshotListener { querySnapshot, _ ->
            if (querySnapshot != null) {
                this.dayInfoMesages = querySnapshot.toObjects(DayInfoMessage::class.java)
            }
        }
    }

    fun getSalarySpec(): Observable<MutableList<SalarySpecification>> {
        return create {
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("salarySpec").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (querySnapshot != null) {
                        val salarySpecifications = mutableListOf<SalarySpecification>()
                        querySnapshot.forEach { snapShot ->
                            val salarySpecification = snapShot.toObject(SalarySpecification::class.java)
                            salarySpecification.id = snapShot.id
                            salarySpecifications.add(salarySpecification)
                        }
                        it.onNext(salarySpecifications)
                }
            }
        }
    }

    fun getPdf(salarySpecification: SalarySpecification,activity:Activity) {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference
        val pathRef = ref.child(salarySpecification.path!!)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1234)
        }else if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(activity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),234)
        }else{
            val rootPath = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
            if (!rootPath.exists()) {
                rootPath.mkdirs()
            }
            val localFile = File(rootPath, salarySpecification.id + ".pdf")

            pathRef.getFile(localFile).addOnCompleteListener {
                if(it.isSuccessful){

                    val intent = Intent()
                    intent.setDataAndType(Uri.fromFile(localFile), "application/pdf")
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    val pintent = PendingIntent.getActivity(activity, 0,
                            intent, 0)

                    val notificationGroup = NotificationCompat.Builder(activity, "pdf")
                            .setSmallIcon(R.drawable.p)
                            .setGroup("myPdf")
                            .setGroupSummary(true)
                            .setContentIntent(pintent)
                            .build()

                    val newMessageNotification = NotificationCompat.Builder(activity, "pdf")
                            .setSmallIcon(R.drawable.p)
                            .setGroup("myPdf")
                            .setContentTitle(salarySpecification.id.toString()+".pdf")
                            .setContentIntent(pintent)
                            .build()

                    val nm: NotificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(4,notificationGroup)
                    nm.notify(Integer.parseInt(salarySpecification.id), newMessageNotification)

                }
            }
        }
    }

}


