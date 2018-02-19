package pami.com.pami


import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*


class ShiftsFragment : Fragment() {
    var prevBtn: TextView? = null;
    var nextBtn: TextView? = null;
    var monthTv: TextView? = null;
    var selectedMonth: Int = 0;
    var selectedYear: Int = 0;
    var calendar: Calendar = Calendar.getInstance();
    var table: TableLayout? = null;
    var shifts: MutableList<Shift>? = null;
    var df: DecimalFormat = DecimalFormat("00");
    var simpleDateFormat=SimpleDateFormat("MMMM yyyy",Locale("swe"));
    var selectedDate = Date();


    companion object {
        fun getInstance(): ShiftsFragment {
            return ShiftsFragment();
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_shifts, container, false)

        prevBtn = view.findViewById<TextView>(R.id.prev_btn);
        nextBtn = view.findViewById<TextView>(R.id.next_btn);
        monthTv = view.findViewById<TextView>(R.id.month_TV);
        table = view.findViewById<TableLayout>(R.id.tableLayout)

        this.selectedDate =Date();
        monthTv?.text =this.simpleDateFormat.format(this.selectedDate)


        FirebaseController.getInstance().getShifts().observeOn(AndroidSchedulers.mainThread()).subscribe() {
            this.shifts = it;
            setUpTable()
        }
//        setUpMonthPicker()

        prevBtn?.setOnClickListener { view -> handleDateChange(view) }
        nextBtn?.setOnClickListener { view -> handleDateChange(view) }
        return view
    }

    private fun setUpTable() {

        this.table?.removeAllViewsInLayout()
        var totalMonthSalary = 0;
        var totalDuration = 0;


        val row = TableRow(context)
        row.setBackgroundColor(Color.LTGRAY);
        var tableHeaderViews= mutableListOf<TextView>();

        val dayHeaderTV = TextView(context);
        val startHeaderTV = TextView(context)
        val endHeaderTV = TextView(context)
        val durationHeaderTV = TextView(context)
        val payHeaderTV = TextView(context)

        dayHeaderTV.text = "Dag"
        startHeaderTV.text = "Start"
        endHeaderTV.text = "Slut"
        durationHeaderTV.text = "Längd"
        payHeaderTV.text = "Sek"

        tableHeaderViews.add(dayHeaderTV);
        tableHeaderViews.add(startHeaderTV);
        tableHeaderViews.add(endHeaderTV);
        tableHeaderViews.add(durationHeaderTV);
        tableHeaderViews.add(payHeaderTV);


        tableHeaderViews.forEach{

            it.textSize=16F
            it.typeface= Typeface.MONOSPACE
            it.setBackgroundResource(R.drawable.table_cell_bg)
            it.setTextColor(Color.WHITE)
            row.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            row.addView(it)
        }

        var calendar =Calendar.getInstance()
        calendar.time=selectedDate;

        this.table?.addView(row);

        shifts?.forEach {
            if (it.startTime.month == calendar.get(Calendar.MONTH)+1 && it.startTime.year == calendar.get(Calendar.YEAR)) {
                totalMonthSalary += it.ObnattMoney + it.Obmoney + it.duration*it.employeeSalary;
                totalDuration += it.duration;

                val row = TableRow(context)
                var tableCells:MutableList<TextView> = mutableListOf();

                val dayTV = TextView(context)
                val startTV = TextView(context)
                val endTV = TextView(context)
                val durationTV = TextView(context)
                val payTV = TextView(context)

                dayTV.text = it.startTime.day.toString()
                startTV.text =df.format(it.startTime.hour) + ":" + df.format(it.startTime.minute)
                endTV.text = df.format(it.endTime.hour) + ":" + df.format(it.startTime.minute)
                durationTV.text = it.duration.toString()
                payTV.text = ((it.duration * it.employeeSalary) + it.Obmoney + it.ObnattMoney).toString()

                tableCells.add(dayTV);
                tableCells.add(startTV);
                tableCells.add(endTV);
                tableCells.add(durationTV);
                tableCells.add(payTV);

                tableCells.forEach{
                    it.textSize=15F
                    it.typeface= Typeface.MONOSPACE
                    it.setBackgroundResource(R.drawable.table_cell_bg)
                    if(this.table?.childCount!! %2!=0){
                        row.setBackgroundResource(R.color.main_gray)
                    }

                    row.addView(it)
                }

                this.table?.addView(row);
            }
        }

        var lastRow = TableRow(context);
        var totalCell =TextView(context);
        totalCell.text = totalMonthSalary.toString()
        totalCell.setTypeface(Typeface.MONOSPACE,BOLD)
        totalCell.setTextColor(Color.BLACK)
        totalCell.setBackgroundResource(R.drawable.table_cell_bg)

        var totalDurationCell =TextView(context);
        totalDurationCell.text = totalDuration.toString()
        totalDurationCell.setTypeface(Typeface.MONOSPACE,BOLD)
        totalDurationCell.setTextColor(Color.BLACK)
        totalDurationCell.setBackgroundResource(R.drawable.table_cell_bg)

        lastRow.addView(TextView(context));
        lastRow.addView(TextView(context));
        lastRow.addView(TextView(context));
        lastRow.addView(totalDurationCell);
        lastRow.addView(totalCell);

        this.table?.addView(lastRow);

    }

    private fun handleDateChange(view: View?) {
        var fix = 0;
        if (view == prevBtn) {
            fix = -1;
        }
        if (view == nextBtn) {
            fix = 1;
        }
        calendar.add(Calendar.MONTH, fix);

//        selectedMonth = calendar.get(Calendar.MONTH);
//        selectedYear = calendar.get(Calendar.YEAR);
        this.selectedDate = calendar.time;

        monthTv?.text = simpleDateFormat.format(this.selectedDate);
        setUpTable();
    }

    private fun setUpMonthPicker() {
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        var df = DecimalFormat("00");

        monthTv?.text = df.format(selectedMonth + 1) + "-" + selectedYear.toString();
    }

    fun getShifts(V: View) {
        FirebaseController.getInstance().getShifts().subscribe() {
            it.forEach({


            })
        }
    }

}// Required empty public constructor
