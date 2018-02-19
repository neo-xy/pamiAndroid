package pami.com.pami

import android.util.JsonWriter
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import io.reactivex.Observable
import io.reactivex.Observable.create


/**
 * Created by Pawel on 13/02/2018.
 */

class FirebaseController : EventListener<DocumentSnapshot> {


    override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
        Log.d("pawell", "onEvnet" + p0)
    }

    var firestore: FirebaseFirestore? = null;

    private constructor() {
        firestore = FirebaseFirestore.getInstance();
    }

    companion object {
        fun getInstance(): FirebaseController {
            return FirebaseController();
        }
    }

    fun getUser(): Observable<Boolean> {

//        val userId = FirebaseAuth.getInstance().currentUser?.uid
        return Observable.create<Boolean> {
            firestore?.collection("users")?.document(FirebaseAuth.getInstance().currentUser!!.uid)?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0!!.exists()) {
                        Log.i("pawell", "1")
                        p0.toObject(User::class.java)
                        it.onNext(true)

                        Log.i("pawell", "2")
                    } else {
                    }
                }
            }
            )

        }

    }

    fun setUpUser(): Observable<User> {
        return create { sub ->

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("users")?.document(userId.toString())?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0!!.exists()) {
                        var t = p0.toObject(User::class.java)
                        sub.onNext(t)
                    }
                }
            }
            )
        }
    }

    fun getSalaries(): Observable<MutableList<Salary>> {
        return create { sub ->
            firestore!!.collection("users").document(User.employeeId).collection("salaries")?.get()?.addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    var results = p0.result.toObjects(Salary::class.java)
                    sub.onNext(results)
                }
            })
        }
    }


    fun getShifts(): Observable<MutableList<Shift>> {

        Log.d("pawell", "user" + FirebaseAuth.getInstance().currentUser?.uid)
        return create() {
            firestore!!.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("shifts")?.get()?.addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    var results: MutableList<Shift> = p0.result.toObjects(Shift::class.java)
                    Log.d("pawell", "size" + results.size.toString() + " " + results[0].employeeId)
                    it.onNext(results);
                }
            })
        }

    }


    fun getShifts2(): Observable<MutableList<Shift>> {

        Log.d("pawell", "user" + FirebaseAuth.getInstance().currentUser?.uid)
        return create() {
            firestore!!.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("shifts")?.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    var results: MutableList<Shift> = p0!!.toObjects(Shift::class.java)
                    Log.d("pawell", "size" + results.size.toString() + " " + results[0].employeeId)
                    it.onNext(results);
                }

            })
        }
    }

    fun getCompany(): Observable<Company> {
        return Observable.create<Company> {
            Log.i("pawell", "3" + User.companyId)
            firestore?.collection("companies")?.document(User.companyId)?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0 != null) {
                        var company = p0.toObject(Company::class.java)
                        it.onNext(company)
                    }

                }

            });


        }


    }

}

