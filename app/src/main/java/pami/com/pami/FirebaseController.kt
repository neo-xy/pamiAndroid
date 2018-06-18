package pami.com.pami

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import io.reactivex.Observable
import io.reactivex.Observable.create
import pami.com.pami.models.*
import java.util.*

object FirebaseController {

    var shifts = mutableListOf<Shift>()
    var departments: MutableList<Department>? = null
    var employees: MutableList<Employee> = mutableListOf()
    var colleague = mutableListOf<Colleague>()
    var salaries = mutableListOf<Salary>()
    var unavailableShifts = mutableListOf<UnavailableDate>();
    var accteptedShifts = mutableListOf<Shift>()
    lateinit var token: String
    var interests = mutableListOf<Interest>()

    fun getUser(): Observable<Boolean> {
        return Observable.create<Boolean> {
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0!!.exists()) {
                        p0.toObject(User::class.java)
//                        User.saleries.sortBy({
//                            it.salary
//                        })
                        it.onNext(true)
                    } else {
                    }
                }
            }
            )
        }
    }

    fun getSalaries(): Observable<MutableList<Salary>> {
        return create { sub ->
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("salaries").get().addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    val results = p0.result.toObjects(Salary::class.java)
                    sub.onNext(results)
                }
            })
        }
    }

    fun getUserShifts(): Observable<MutableList<Shift>> {
        return create() {
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("shifts").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {

                    shifts.removeAll(shifts)

                    p0?.forEach {

                        var shift = it.toObject(Shift::class.java)
                        shift.shiftId = it.id
                        shifts.add(shift)
                    }

//
                    it.onNext(shifts)
                }
            })
        }
    }

    fun getShiftsOfaMonth(dateKey: String): Observable<MutableList<Shift>> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("months").document(dateKey).collection("shifts").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    it.onNext(p0!!.toObjects(Shift::class.java))
                }
            })
        }
    }

    fun getCompany(): Observable<Company> {
        return Observable.create<Company> {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0 != null) {
                        val comp = p0.toObject(Company::class.java)
                        if (comp != null) {
                            it.onNext(comp)
                        }
                    }
                }
            })
        }
    }

    fun setUpDepartments(): Observable<Boolean> {
      return  Observable.create {
          val obs = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("departments").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    departments = p0?.toObjects(Department::class.java)
                    obs.onNext(true)
                }
            })
        }

    }

    fun setUpEmployees() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                employees = p0?.toObjects(Employee::class.java) as MutableList<Employee>
            }
        })
    }

    fun updateUnavailableDates(dates: List<Int>) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("datesUnavailable", dates)
    }

    fun updateUnavailableDates2(us: UnavailableDate, dateKey: String) {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("datesUnavailable").add(us).addOnCompleteListener(OnCompleteListener {
            val id = it.result.id

            Log.d("pawell", "compl")
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("datesUnavailable").document(id).set(us)
        })

    }

    fun getUanavailableDates(): Observable<MutableList<UnavailableDate>> {
        return create({
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("datesUnavailable").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    unavailableShifts.removeAll(unavailableShifts);
                    p0?.forEach {

                        var us = it.toObject(UnavailableDate::class.java)
                        us.id = it.id
                        unavailableShifts.add(us)
                    }
                }
            })
        })
    }

    fun removeUnavailableDate(us: UnavailableDate, dateKey: String) {

        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("datesUnavailable").document(us.id).delete()
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("datesUnavailable").document(us.id).delete()
    }

    fun saveImgUrl(photoUrl: Uri?) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("imgUrl", photoUrl.toString())
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").document(User.employeeId).update("imgUrl", photoUrl.toString())
    }

    fun setUpColleagues() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p0 !== null) {
                    colleague = p0.toObjects(Colleague::class.java)
                }
            }

        })
    }

    fun setupSalleries() {
        //TODO change employeeID to UserId
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("salaries").get().addOnCompleteListener {
            if (it.isSuccessful) {
                this.salaries = it.getResult().toObjects(Salary::class.java)
            }
        }
    }

    fun ClockItShift(date: Date) {


    }

    fun updateShift(it: Shift) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("shifts").document(it.shiftId).set(it)
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("months")
                .document("" + it.startTime.year + "" + Shared.df.format(it.startTime.month)).collection("shifts").document(it.shiftId).set(it)
    }

    fun markAsToAccept(shift: Shift) {

        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("shiftsToAccept").document(shift.shiftId).set(shift)
    }

    fun markAsToAcceptNew(shift: Shift): Observable<String> {

        return Observable.create<String> {
            val observableEmitter = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("shiftsToAccept").add(shift)
                    .addOnCompleteListener() {
                        observableEmitter.onNext(it.getResult().id)
                    }
        }
    }

    fun addToClockedInShifts(clockedShift: ClockedShift): Observable<String> {
        return create {
            val emiter = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("activeShifts").add(clockedShift).addOnCompleteListener {
                if (it.isSuccessful) {
                    emiter.onNext(it.getResult().id)
//                    FirebaseFirestore.getInstance().collection("data").document("clockedInShift").set(clockedShift)
                } else {
                    emiter.onNext("");
                }
            }
        }
    }

    fun getClockedInShifts(): Observable<MutableList<ClockedShift>> {
        return Observable.create {
            val emitter = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("activeShifts").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    val clockedShifts = mutableListOf<ClockedShift>()
                    p0?.forEach {
                        val cs = it.toObject(ClockedShift::class.java)
                        cs.clockedShiftId = it.id
                        clockedShifts.add(cs)
                    }
                    emitter.onNext(clockedShifts)
                }
            })
        }
    }

    fun removeShiftFromClockedInShifts(clockedShift: ClockedShift): Observable<Boolean> {
        return Observable.create {
            val emitter = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("activeShifts").document(clockedShift.clockedShiftId).delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onNext(true)
                } else {
                    emitter.onNext(false)
                }
            }
        }
    }

    fun addShiftsToAccept(clockedShift: ClockedShift): Observable<Boolean> {
        return Observable.create {
            val emitter = it
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("shiftsToAccept").document(clockedShift.clockedShiftId).set(clockedShift).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onNext(true)
                } else {
                    emitter.onNext(false)
                }
            }
        }
    }

    fun getAcceptedShifts(): Observable<MutableList<Shift>> {
        return create {
            FirebaseFirestore.getInstance().collection("users").document(User.employeeId).collection("acceptedShifts").addSnapshotListener(object : EventListener<QuerySnapshot> {
                val emitter = it
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {

                    accteptedShifts = p0!!.toObjects(Shift::class.java)
                    emitter.onNext(accteptedShifts);
                }

            })
        }
    }

    fun updateRegistrationToken(token: String) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("refreshToken", token)
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").document(User.employeeId)
                .update("refreshToken", token)
    }

    fun getShiftsToTake(): Observable<MutableList<ShiftsToTake>> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("shiftsToTake").addSnapshotListener(object : EventListener<QuerySnapshot> {
                val emiter = it;
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0 != null) {
                        val shiftsToTake = mutableListOf<ShiftsToTake>()
                        p0.forEach {
                            val id = it.id
                            val shift = it.toObject(ShiftsToTake::class.java)
                            shift.id = id;
                            shiftsToTake.add(shift)
                        }

                        emiter.onNext(shiftsToTake)
                    }
                }

            })
        }
    }

    fun getInterests(): Observable<MutableList<Interest>> {
        return create {
            FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("interests").addSnapshotListener(object : EventListener<QuerySnapshot> {
                val emiter = it;

                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0 != null) {
                        val interestList = mutableListOf<Interest>()
                        p0.forEach {


                            val id = it.id;
                            val interest = it.toObject(Interest::class.java)
                            if(User.employeeId==interest.employeeId){
                                interest.interestId = id;
                                interestList.add(interest);
                            }
                        }

                        interests  = interestList;
                        emiter.onNext(interestList);
                    }
                }
            }
            )
        }
    }

    fun addIntress(interest: Interest) {
        Log.d("pawell", "adddd")
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("interests").add(interest)
    }

    fun removeInterest(interest:Interest){
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("interests").document(interest.interestId).delete()
    }

}


