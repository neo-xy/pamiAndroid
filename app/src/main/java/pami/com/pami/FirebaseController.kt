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

object FirebaseController  {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance();
    var shifts = mutableListOf<Shift>()
    var departments:MutableList<Department>? = null;

    fun getUser(): Observable<Boolean> {

        return Observable.create<Boolean> {
            firestore?.collection("users")?.document(FirebaseAuth.getInstance().currentUser!!.uid)?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    if (p0!!.exists()) {
                        p0.toObject(User::class.java)
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
            firestore!!.collection("users").document(User.employeeId).collection("salaries")?.get()?.addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    var results = p0.result.toObjects(Salary::class.java)
                    sub.onNext(results)
                }
            })
        }
    }

    fun getShifts(): Observable<MutableList<Shift>> {


        return create() {
            firestore!!.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("shifts")?.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    var results: MutableList<Shift> = p0!!.toObjects(Shift::class.java)
                    shifts = results;
                    it.onNext(results);
                }

            })
        }
    }



    fun getCompany(): Observable<Company> {
        return Observable.create<Company> {
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

    fun setUpDepartments():Observable<MutableList<Department>>{
        return create<MutableList<Department>>{
            firestore.collection("companies").document(User.companyId).collection("departments").addSnapshotListener(object:EventListener<QuerySnapshot>{
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    departments = p0?.toObjects(Department::class.java);
                }

            })
        }
    }

}

