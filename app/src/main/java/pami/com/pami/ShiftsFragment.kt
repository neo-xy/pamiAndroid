package pami.com.pami


import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.DecimalFormat
import java.util.*


class ShiftsFragment : Fragment() {
    var prevBtn: TextView? = null;
    var nextBtn: TextView? = null;
    var monthTv: TextView? = null;
    var selectedMonth: Int = 0;
    var selectedYear: Int = 0;
    var date: Calendar = Calendar.getInstance();
    var table: TableLayout? = null;
    var shifts: MutableList<Shift>? = null;
    var df: DecimalFormat = DecimalFormat("00");


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

        FirebaseController.getInstance().getShifts().observeOn(AndroidSchedulers.mainThread()).subscribe() {
            this.shifts = it;
            setUpTable()
        }
        setUpMonthPicker()

        prevBtn?.setOnClickListener { view -> handleDateChange(view) }
        nextBtn?.setOnClickListener { view -> handleDateChange(view) }
        return view
    }

    private fun setUpTable() {

        this.table?.removeAllViewsInLayout()
        var totalMonthSalary = 0;
        var totalDuration = 0;

        var parts = this.monthTv?.text.toString().split("-");
        var month = parts[0].toInt();
        var year = parts[1].toInt();

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


        this.table?.addView(row);

        shifts?.forEach {
            if (it.startTime.month == month && it.startTime.year == year) {
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
        date.add(Calendar.MONTH, fix);

        selectedMonth = date.get(Calendar.MONTH);
        selectedYear = date.get(Calendar.YEAR);
        var df: DecimalFormat = DecimalFormat("00");
        monthTv?.text = df.format(selectedMonth + 1) + "-" + selectedYear.toString();
        setUpTable();
    }

    private fun setUpMonthPicker() {
        selectedMonth = date.get(Calendar.MONTH)
        selectedYear = date.get(Calendar.YEAR)
        var df: DecimalFormat = DecimalFormat("00");

        monthTv?.text = df.format(selectedMonth + 1) + "-" + selectedYear.toString();
    }

    fun getShifts(V: View) {
        Log.d("pawell", "clickecd")
        FirebaseController.getInstance().getShifts().subscribe() {
            Log.d("pawell", "inside")
            Log.d("pawell", it.size.toString())
            it.forEach({


            })
        }
    }

}// Required empty public constructor
