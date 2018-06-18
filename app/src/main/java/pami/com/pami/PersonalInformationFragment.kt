package pami.com.pami


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import pami.com.pami.models.User

class PersonalInformationFragment : Fragment() {
    private lateinit var totalSalaryView: TextView
    private lateinit var totalWorkedHoursView: TextView
    private lateinit var firstWorkDateView: TextView
    private lateinit var lastWorkDateView: TextView
    private lateinit var currentSalaryView: TextView
    private lateinit var nameView: TextView
    private lateinit var emailView: TextView
    private lateinit var homeAdressView: TextView
    private lateinit var socialNumberView: TextView
    private lateinit var bankAccountView: TextView
    private lateinit var phoneNrView: TextView

    private var totalSalary = 0L
    private var totalHours = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_personal_information, container, false)
        val profileViews = view.findViewById<ImageView>(R.id.profile_img)
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

        var sumDuration = 0.0
        var sumSalary = 0L

        FirebaseController.accteptedShifts.forEach {
            sumDuration += it.duration
            sumSalary = (sumSalary + it.brutto)
        }

        this.totalWorkedHoursView.text = sumDuration.toString()
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


        val shifts = Shared.sortShifts(FirebaseController.shifts)
        if (shifts.size > 0) {
            firstWorkDateView.text = "" + shifts[0].startTime.year + "/" + Shared.df.format(shifts[0].startTime.month) + "/" +
                    Shared.df.format(shifts[0].startTime.day)
            lastWorkDateView.text = "" + shifts[shifts.size - 1].startTime.year + "/" + Shared.df.format(shifts[shifts.size - 1].startTime.month) + "/" +
                    Shared.df.format(shifts[shifts.size - 1].startTime.day)
        }

        Glide.with(this)
                .load(User.imgUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileViews)
        return view
    }
}


