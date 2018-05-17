package pami.com.pami


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import pami.com.pami.models.User


/**
 * A simple [Fragment] subclass.
 */
class PersonalInformationFragment : Fragment() {
    lateinit var totalSalaryView: TextView
    lateinit var totalWorkedHoursView: TextView
    lateinit var firstWorkDateView: TextView
    lateinit var lastWorkDateView: TextView
    lateinit var currentSalaryView: TextView;
    lateinit var nameView: TextView
    lateinit var emailView: TextView
    lateinit var homeAdressView: TextView
    lateinit var socialNumberView: TextView
    lateinit var bankAccountView: TextView
    lateinit var phoneNrView: TextView
    lateinit var totalSumHours: TextView
    lateinit var totalSumMonay: TextView
    var totalSalary = 0L
    var totalHours = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal_information, container, false);
        val profileViews = view.findViewById<ImageView>(R.id.profile_img);
        totalSalaryView = view.findViewById(R.id.total_made_money)
        totalWorkedHoursView = view.findViewById(R.id.total_worked_hours_tv)
        firstWorkDateView = view.findViewById(R.id.first_work_date_tv)
        lastWorkDateView = view.findViewById(R.id.last_work_date_tv)
        currentSalaryView = view.findViewById(R.id.current_salary_tv)



        nameView = view.findViewById(R.id.name_tv)
        emailView = view.findViewById(R.id.email_tv)
        homeAdressView = view.findViewById(R.id.home_adress_tv)
        socialNumberView = view.findViewById(R.id.social_number)
        bankAccountView = view.findViewById(R.id.bank_account_tv)
        phoneNrView = view.findViewById(R.id.phone_tv)

        nameView.text = User.firstName + " " + User.lastName
        emailView.text = User.email
        homeAdressView.text = User.address + ", " + User.city
        socialNumberView.text = User.socialSecurityNumber.toString()
        bankAccountView.text = User.clearNr + "-" + User.accountNr + ", " + User.bankName
        phoneNrView.text = User.phoneNumber

        var sumDuration = 0L;
        var sumSalary = 0L;

        FirebaseController.accteptedShifts.forEach {
            sumDuration = sumDuration + it.duration
            val hourDecimal = sumDuration / (1000 * 60 * 60)
            sumSalary =(sumSalary + (hourDecimal*it.employeeSalary) + it.OBmoney + it.OBnattMoney).toLong()
        }

        Log.d("pawell","dd "+FirebaseController.accteptedShifts+"  h  "+ sumDuration.toLong().toString())
        totalWorkedHoursView.text = (sumDuration/(1000 * 60 * 60).toLong()).toString()
        totalSalaryView.text = sumSalary.toString()

        var salarySuffix = "Sek/tim"
        if (User.salaries[0].salary > 1000) {
            salarySuffix = "Sek/månad"
        }

        currentSalaryView.text = User.salaries[0].salary.toString() + " " + salarySuffix

        FirebaseController.salaries.forEach {
            totalHours += it.duration!!
            totalSalary += it.total!!
        }

//        totalSalaryView.text = "" + totalSalary + " Sek"
//        totalWorkedHoursView.text = "" + totalHours + " timmar"

        val shifts = Shared.sortShifts(FirebaseController.shifts)
        if (shifts.size > 0) {
            firstWorkDateView.text = "" + shifts[0].startTime.year + "/" + Shared.df.format(shifts[0]?.startTime.month) + "/" +
                    Shared.df.format(shifts[0]?.startTime.day)
            lastWorkDateView.text = "" + shifts[shifts.size - 1]?.startTime.year + "/" + Shared.df.format(shifts[shifts.size - 1]?.startTime.month) + "/" +
                    Shared.df.format(shifts[shifts.size - 1]?.startTime.day)
        }


        Glide.with(this)
                .load(User.imgUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileViews)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}


