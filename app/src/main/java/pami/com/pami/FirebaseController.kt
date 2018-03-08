package pami.com.pami

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import io.reactivex.Observable
import io.reactivex.Observable.create

object FirebaseController {

    var shifts = mutableListOf<Shift>()
    var departments: MutableList<Department>? = null
    var employees: MutableList<Employees>? = null
    lateinit var token:String

    fun setUpToken() {
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener(OnCompleteListener {
            token = it.getResult().token!!
        })
    }
    fun getUser(): Observable<Boolean> {
        return Observable.create<Boolean> {
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0!!.exists()) {
                        var t =p0.toObject(User::class.java)
                        Log.d("pawell","zzz"+t.imgUrl)
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

    fun getShifts(): Observable<MutableList<Shift>> {
        return create() {
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("shifts").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    val results: MutableList<Shift> = p0!!.toObjects(Shift::class.java)
                    shifts = results
                    it.onNext(results)
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
                        it.onNext(comp)
                    }
                }
            })
        }
    }

    fun setUpDepartments() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("departments").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                departments = p0?.toObjects(Department::class.java)
            }
        })
    }

    fun setUpEmployees() {
        FirebaseFirestore.getInstance().collection("companies").document(User.companyId).collection("employees").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                employees = p0?.toObjects(Employees::class.java)
            }
        })
    }

    fun updateUnavailableDates(dates: List<Int>) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("datesUnavailable", dates)
    }

    fun saveImgUrl(photoUrl: Uri?) {
        FirebaseFirestore.getInstance().collection("users").document(User.employeeId).update("imgUrl",photoUrl.toString())
    }
}

