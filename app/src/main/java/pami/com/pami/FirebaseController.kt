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

    constructor() {
        firestore = FirebaseFirestore.getInstance();
    }

    companion object {
        fun getInstance(): FirebaseController {
            return FirebaseController();
        }
    }

    fun getUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        firestore?.collection("users")?.document(userId.toString())?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
            override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                if (p0!!.exists()) {
                    p0.toObject(User::class.java)
                } else {
                }
            }
        }
        )
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
        return Observable.create() {
            firestore!!.collection("users").document(User.employeeId).collection("shifts")?.get()?.addOnCompleteListener(object : EventListener<QuerySnapshot>, OnCompleteListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onComplete(p0: Task<QuerySnapshot>) {
                    var results: MutableList<Shift> = p0.result.toObjects(Shift::class.java)
                    Log.d("pawell", "size" + results.size.toString() + " " + results[0].employeeId)
                    it.onNext(results);
                }

            })
        }

    }

}

