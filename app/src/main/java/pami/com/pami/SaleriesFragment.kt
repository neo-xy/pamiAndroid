package pami.com.pamiimport android.graphics.Colorimport android.graphics.Typefaceimport android.graphics.Typeface.BOLDimport android.os.Bundleimport android.support.v4.app.Fragmentimport android.view.Gravityimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.LinearLayoutimport android.widget.TableLayoutimport android.widget.TableRowimport android.widget.TextViewimport pami.com.pami.models.Shiftimport java.text.DecimalFormatimport java.text.SimpleDateFormatimport java.util.*import java.util.concurrent.TimeUnitclass SaleriesFragment : Fragment() {    lateinit var prevBtn: LinearLayout    lateinit var nextBtn: LinearLayout    var monthTv: TextView? = null    var selectedMonth: Int = 0    var selectedYear: Int = 0    var calendar: Calendar = Calendar.getInstance()    var table: TableLayout? = null    var shifts = mutableListOf<Shift>()    var df: DecimalFormat = DecimalFormat("00")    var simpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale("swe"))    var selectedDate = Date()    lateinit var salaryView: TextView;    companion object {        fun getInstance(): SaleriesFragment {            return SaleriesFragment()        }    }    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {        val view = inflater.inflate(R.layout.fragment_shifts, container, false)        this.prevBtn = view.findViewById(R.id.prev_btn)        this.nextBtn = view.findViewById(R.id.next_btn)        monthTv = view.findViewById<TextView>(R.id.month_TV)        table = view.findViewById<TableLayout>(R.id.tableLayout)        this.salaryView = view.findViewById(R.id.salary_tv)        this.selectedDate = Date()        monthTv?.text = this.simpleDateFormat.format(this.selectedDate)        FirebaseController.getAcceptedShifts().subscribe() {            this.shifts = it;            FirebaseController.shifts.forEach {                if (it.timeStempIn > Date().time) {                    shifts.add(it)                }            }            this.shifts = Shared.sortShifts(this.shifts);            setUpTable();        }        prevBtn.setOnClickListener { view2 -> handleDateChange(view2) }        nextBtn.setOnClickListener { view3 -> handleDateChange(view3) }        return view    }    private fun setUpTable() {        this.table?.removeAllViewsInLayout()        var totalNetto = 0L        var totalDuration = 0.0        var totalBrutto = 0L        val row = TableRow(context)        row.setBackgroundColor(Color.LTGRAY)        val tableHeaderViews = mutableListOf<TextView>()        val dayHeaderTV = TextView(context)        val startHeaderTV = TextView(context)        val endHeaderTV = TextView(context)        val durationHeaderTV = TextView(context)        val payHeaderTV = TextView(context)        dayHeaderTV.text = "Dag"        startHeaderTV.text = "Start"        endHeaderTV.text = "Slut"        durationHeaderTV.text = "Längd"        payHeaderTV.text = "Sek"        tableHeaderViews.add(dayHeaderTV)        tableHeaderViews.add(startHeaderTV)        tableHeaderViews.add(endHeaderTV)        tableHeaderViews.add(durationHeaderTV)        tableHeaderViews.add(payHeaderTV)        tableHeaderViews.forEach {            it.textSize = 16F            it.typeface = Typeface.MONOSPACE            it.gravity = Gravity.CENTER            it.setTextColor(Color.WHITE)            row.addView(it)        }        val calendar = Calendar.getInstance()        calendar.time = selectedDate        row.setBackgroundResource(R.drawable.bg_table_header_row)        row.setPadding(0, 30, 0, 30);        this.table?.addView(row)        var tax = 0.0;        shifts?.forEach {            tax = it.tax            if (it.startTime.month == calendar.get(Calendar.MONTH) + 1 && it.startTime.year == calendar.get(Calendar.YEAR)) {                totalNetto += it.netto                totalDuration += it.duration                totalBrutto += it.brutto                val row2 = TableRow(context)                var tableCells: MutableList<TextView> = mutableListOf()                val dayTV = TextView(context)                val startTV = TextView(context)                val endTV = TextView(context)                val durationTV = TextView(context)                val payTV = TextView(context)                dayTV.setTypeface(Typeface.MONOSPACE, BOLD)                dayTV.text = String.format("%02d", it.startTime.day)                startTV.text = df.format(it.startTime.hour) + ":" + df.format(it.startTime.minute)                endTV.text = df.format(it.endTime.hour) + ":" + df.format(it.endTime.minute)                durationTV.text = it.duration.toString()                payTV.text = it.netto.toString()                tableCells.add(dayTV)                tableCells.add(startTV)                tableCells.add(endTV)                tableCells.add(durationTV)                tableCells.add(payTV)                tableCells.forEach {                    it.gravity = Gravity.CENTER                    it.textSize = 15F                    it.typeface = Typeface.MONOSPACE                    row2.addView(it)                }                row2.setBackgroundResource(R.drawable.bg_bottom_border_gray)                row2.setPadding(0, 50, 0, 50)                this.table?.addView(row2)            }        }        val lastRow = TableRow(context)        val totalCell = TextView(context)        totalCell.text = ""        totalCell.setTypeface(Typeface.MONOSPACE, BOLD)        totalCell.setTextColor(Color.BLACK)        totalCell.setBackgroundResource(R.drawable.bg_bottom_border_gray)        totalCell.setPadding(0, 50, 0, 50)        totalCell.gravity = Gravity.CENTER        this.salaryView.text = totalBrutto.toString() + " Sek"        val totalDurationCell = TextView(context)//        totalDurationCell.text = changeDecimalToMinutesForm(totalDuration)        totalDurationCell.text = totalDuration.toString()        totalDurationCell.setTypeface(Typeface.MONOSPACE, BOLD)        totalDurationCell.setTextColor(Color.BLACK)        totalDurationCell.gravity = Gravity.CENTER        totalDurationCell.setBackgroundResource(R.drawable.bg_bottom_border_gray)        totalDurationCell.setPadding(0, 50, 0, 50)        lastRow.addView(TextView(context))        lastRow.addView(TextView(context))        lastRow.addView(TextView(context))        lastRow.addView(totalDurationCell)        lastRow.addView(totalCell)        this.table?.addView(lastRow)        val nettoRow = TableRow(context)        val nettoName = TextView(context)        nettoName.text = "Netto"        nettoName.setTypeface(Typeface.MONOSPACE, BOLD)        nettoName.setTextColor(Color.BLACK)        nettoName.setBackgroundResource(R.drawable.bg_bottom_border_gray)        nettoName.setPadding(0, 50, 0, 50)        nettoName.gravity = Gravity.CENTER        val nettoValue = TextView(context)        nettoValue.text = totalNetto.toString()        nettoValue.setTypeface(Typeface.MONOSPACE, BOLD)        nettoValue.setTextColor(Color.BLACK)        nettoValue.gravity = Gravity.CENTER        nettoValue.setBackgroundResource(R.drawable.bg_bottom_border_gray)        nettoValue.setPadding(0, 50, 0, 50)        nettoRow.addView(TextView(context))        nettoRow.addView(TextView(context))        nettoRow.addView(TextView(context))        nettoRow.addView(nettoName)        nettoRow.addView(nettoValue)        this.table?.addView(nettoRow)    }    private fun changeDecimalToMinutesForm(totalDuration: Double): String {        var total2 = String.format("%.2f", totalDuration);        var hour = 0;        var minutes = 0;        var index = total2.indexOf(".")        if (index == -1) {            index = total2.indexOf(",")        }        var parts = total2.split(",")        var dec = parts[1]        var number = dec.toInt()        val minutes2 = (number * 60) / 100        var df = DecimalFormat("00");        return df.format(parts[0].toInt()) + ":" + df.format(minutes2)    }    private fun handleDateChange(view: View?) {        var fix = 0        if (view == prevBtn) {            fix = -1        }        if (view == nextBtn) {            fix = 1        }        calendar.add(Calendar.MONTH, fix)        this.selectedDate = calendar.time        monthTv?.text = simpleDateFormat.format(this.selectedDate);        setUpTable()    }    private fun setUpMonthPicker() {        selectedMonth = calendar.get(Calendar.MONTH)        selectedYear = calendar.get(Calendar.YEAR)        var df = DecimalFormat("00");        monthTv?.text = df.format(selectedMonth + 1) + "-" + selectedYear.toString();    }}