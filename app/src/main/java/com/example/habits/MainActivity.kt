package com.example.habits

import android.content.DialogInterface
import android.icu.util.Calendar
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
    private lateinit var viewStreakButton: Button
    private lateinit var uid: EditText
    private lateinit var listView: ListView

    private fun getStats(databaseHandler: DatabaseHandler): Int {
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
        for (item in dateArrayId) {
            if (item[item.length - 1] != '1') { break }
            else {
                var year = item.substring(0,4).toInt()
                var month = item.substring(4,6).toInt()
                var day = item.substring(6,8).toInt()
//                if
            }
            dayCount += 1
        }
        return dayCount
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
    private fun deleteRecord(databaseHandler: DatabaseHandler) {
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
            if(deleteId.trim()!=""){
                //calling the deleteEntry method of DatabaseHandler class to delete record
                val status = databaseHandler.deleteEntry(DateModelClass(Integer.parseInt(deleteId), 1)) // only date matters
                if(status > -1){
                    Toast.makeText(applicationContext,"record deleted",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
            }
            highlightDates(calendarView, databaseHandler)      // Refresh highlights
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
        viewStreakButton = findViewById(R.id.viewStreakButton)
        viewStreakButton.text = getStats(databaseHandler).toString()

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

        val savePassRecordButton: Button = findViewById(R.id.savePassRecordButton)
        savePassRecordButton.setOnClickListener {
            saveRecord(databaseHandler, true)
            highlightDates(calendarView, databaseHandler)
        }
        val saveFailRecordButton: Button = findViewById(R.id.saveFailRecordButton)
        saveFailRecordButton.setOnClickListener {
            saveRecord(databaseHandler, false)
            highlightDates(calendarView, databaseHandler)
        }
        val viewRecordButton: Button = findViewById(R.id.viewRecordButton)
        viewRecordButton.setOnClickListener {
            viewRecord(databaseHandler)
        }
        val deleteRecordButton: Button = findViewById(R.id.deleteRecordButton)
        deleteRecordButton.setOnClickListener {
            deleteRecord(databaseHandler)
        }

    }
}