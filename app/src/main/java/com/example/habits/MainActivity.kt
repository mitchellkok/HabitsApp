package com.example.habits

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener


class MainActivity : AppCompatActivity() {
    // on below line we are creating
    // variables for text view and calendar view
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var uid: EditText
    private lateinit var listView: ListView
    private val daysInMilli: Int = (1000 * 60 * 60 * 24)

    private fun getStats(databaseHandler: DatabaseHandler, button: Button): IntArray {
        //calling the viewEntry method of DatabaseHandler class to read the records
        val dte: List<DateModelClass> = databaseHandler.viewEntry()
        val dateArrayId = Array(dte.size){"0"}
        for((index, e) in dte.withIndex()){
            val dateString = e.date.toString()
            val statusString = e.status.toString()
            dateArrayId[index] = "$dateString : $statusString"
        }
        dateArrayId.sortDescending()
        var dayCount = 0
        var passCount = 0
        var maxDays =  0
        var failed = false
        var prevJavaDate = CalendarDay.today().date
        for (item in dateArrayId) {
            val year = item.substring(0,4).toInt()
            val month = item.substring(4,6).toInt()
            val day = item.substring(6,8).toInt()
            val javaDate = CalendarDay.from(year, month-1, day).date
            if (((prevJavaDate.time - javaDate.time ) / daysInMilli) > 1) { break } // gap too big
            prevJavaDate = javaDate
            dayCount += 1
            if (item[item.length - 1] == '1') {
                if (!failed) { maxDays = dayCount }
                passCount += 1
            } else {
                failed = true
            }
        }

        button.text = maxDays.toString()
        return intArrayOf(maxDays, passCount, dayCount)
    }

    @SuppressLint("SetTextI18n")
    private fun viewStats(databaseHandler: DatabaseHandler, viewStatsButton: Button) {
        //creating AlertDialog for taking user id
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.stats_dialog, null)
        dialogBuilder.setView(dialogView)

        val streakBox = dialogView.findViewById(R.id.stats_streak) as TextView
        val percentBox = dialogView.findViewById(R.id.stats_percent) as TextView

        val stats = getStats(databaseHandler, viewStatsButton)
        val percent = 100 * stats[1] / stats[2]
        streakBox.text = stats[0].toString()
        percentBox.text = "$percent%"

        dialogBuilder.setTitle("Habit Statistics")
        dialogBuilder.setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->


        })

        val b = dialogBuilder.create()
        b.show()
    }

    private fun highlightDates(calendarView: MaterialCalendarView, databaseHandler: DatabaseHandler) {
        val dte: List<DateModelClass> = databaseHandler.viewEntry()

        val datesPass = ArrayList<CalendarDay>()
        val datesFail = ArrayList<CalendarDay>()
        for (e in dte) {
            val dateString = e.date.toString()
            val year = dateString.substring(0,4).toInt()
            val month = dateString.substring(4,6).toInt()
            val day = dateString.substring(6,8).toInt()
            val fullDate = CalendarDay.from(year, month-1, day)
            if (e.status == 1) {
                datesPass.add(fullDate)
            } else {
                datesFail.add(fullDate)
            }
        }
        calendarView.addDecorators(
            MPassDecorator(this@MainActivity, datesPass),
            MFailDecorator(this@MainActivity, datesFail),
        )
    }

    private fun unhighlightDates(calendarView: MaterialCalendarView, dte: Int) {
        val dateString = dte.toString()
        val year = dateString.substring(0,4).toInt()
        val month = dateString.substring(4,6).toInt()
        val day = dateString.substring(6,8).toInt()
        val fullDate = CalendarDay.from(year, month-1, day)
        calendarView.addDecorator(MBlankDecorator(this@MainActivity, fullDate))
    }

    //method for saving records in database
    private fun saveRecord(databaseHandler: DatabaseHandler, pass: Boolean) {
        val uDate = uid.text.toString()
        if(uDate.trim()!=""){
            var status: Long
            if (pass) {
                status = databaseHandler.addEntry(DateModelClass(Integer.parseInt(uDate), 1))
            } else {
                status = databaseHandler.addEntry(DateModelClass(Integer.parseInt(uDate), 0))
            }
            if(status > -1){
                Toast.makeText(applicationContext,"record saved",Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(applicationContext,"date cannot be blank",Toast.LENGTH_LONG).show()
        }
    }

    //method for read records from database in ListView
    private fun viewRecord(databaseHandler: DatabaseHandler) {
        //calling the viewEntry method of DatabaseHandler class to read the records
        val dte: List<DateModelClass> = databaseHandler.viewEntry()
        val dateArrayId = Array(dte.size){"0"}
        for((index, e) in dte.withIndex()){
            val dateString = e.date.toString()
            val statusString = e.status.toString()
            dateArrayId[index] = "$dateString : $statusString"
            println("dateArrayId[index] = ${dateArrayId[index]}")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dateArrayId)
        listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

    }

    //method for deleting records based on id
    private fun deleteRecord(databaseHandler: DatabaseHandler, statsButton: Button) {
        //creating AlertDialog for taking user id
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val dltId = dialogView.findViewById(R.id.deleteId) as EditText
        dltId.text = uid.text

        dialogBuilder.setTitle("Delete Record")
        dialogBuilder.setMessage("Enter id below")
        dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->

            val deleteId = dltId.text.toString()
            unhighlightDates(calendarView, deleteId.toInt())  // Remove highlight
            if (deleteId.trim() != "") {
                //calling the deleteEntry method of DatabaseHandler class to delete record
                val status = databaseHandler.deleteEntry(
                    DateModelClass(
                        Integer.parseInt(deleteId),
                        1
                    )
                ) // only date matters
                if (status > -1) {
                    Toast.makeText(applicationContext, "record deleted", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "id or name or email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
            highlightDates(calendarView, databaseHandler)      // Refresh highlights
            getStats(databaseHandler, statsButton)
        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        this.deleteDatabase("EntryDatabase.db")
        //creating the instance of DatabaseHandler class
        val databaseHandler = DatabaseHandler(this)
//        databaseHandler.deleteAll()
        val viewStatsButton: Button = findViewById(R.id.viewStatsButton)
        getStats(databaseHandler, viewStatsButton)


        val today = CalendarDay.today()
        calendarView = findViewById(R.id.calendarView)
        calendarView.setDateSelected(today,true);
        uid = findViewById<View>(R.id.u_id) as EditText
        highlightDates(calendarView, databaseHandler)

        val year = today.year
        val month = today.month + 1
        val dayOfMonth = today.day
        val dateInt = year * 10000 + month * 100 + dayOfMonth
        uid.setText(dateInt.toString())

        calendarView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            val year = date.year
            val month = date.month + 1
            val dayOfMonth = date.day
            val dateInt = year * 10000 + month * 100 + dayOfMonth
            uid.setText(dateInt.toString())
        })

        viewStatsButton.setOnClickListener {
            viewStats(databaseHandler, viewStatsButton)
        }
        val savePassRecordButton: Button = findViewById(R.id.savePassRecordButton)
        savePassRecordButton.setOnClickListener {
            saveRecord(databaseHandler, true)
            highlightDates(calendarView, databaseHandler)
            getStats(databaseHandler, viewStatsButton)
        }
        val saveFailRecordButton: Button = findViewById(R.id.saveFailRecordButton)
        saveFailRecordButton.setOnClickListener {
            saveRecord(databaseHandler, false)
            highlightDates(calendarView, databaseHandler)
            getStats(databaseHandler, viewStatsButton)
        }
        val viewRecordButton: Button = findViewById(R.id.viewRecordButton)
        viewRecordButton.setOnClickListener {
            viewRecord(databaseHandler)
        }
        val deleteRecordButton: Button = findViewById(R.id.deleteRecordButton)
        deleteRecordButton.setOnClickListener {
            deleteRecord(databaseHandler, viewStatsButton)
        }

    }
}