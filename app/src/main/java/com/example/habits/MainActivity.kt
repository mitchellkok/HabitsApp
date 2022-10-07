package com.example.habits

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    // on below line we are creating
    // variables for text view and calendar view
    private lateinit var dateTV: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var uid: EditText
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // initializing variables of
        // list view with their ids.
        dateTV = findViewById(R.id.idTVDate)
        calendarView = findViewById(R.id.calendarView)
        uid = findViewById<View>(R.id.u_id) as EditText

        // on below line we are adding set on
        // date change listener for calendar view.
        calendarView
            .setOnDateChangeListener(
                OnDateChangeListener { view, year, month, dayOfMonth ->
                    val date = (dayOfMonth.toString() + "-"  + (month + 1) + "-" + year)
                    val dateInt = year*10000 + (month+1)*100 + dayOfMonth

                    dateTV.text = date
                    uid.setText(dateInt.toString())
                })

        //method for saving records in database
        fun saveRecord() {
            val uDate = uid.text.toString()
            val databaseHandler = DatabaseHandler(this)
            if(uDate.trim()!=""){
                val status = databaseHandler.addEntry(EmpModelClass(Integer.parseInt(uDate)))
                if(status > -1){
                    Toast.makeText(applicationContext,"record save",Toast.LENGTH_LONG).show()
//                    dateTV.text.clear()
                }
            }else{
                Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
            }

        }
        val saveRecordButton: Button = findViewById(R.id.saveRecordButton)
        saveRecordButton.setOnClickListener {
            saveRecord()
        }

        //method for read records from database in ListView
        fun viewRecord() {
            //creating the instance of DatabaseHandler class
            val databaseHandler = DatabaseHandler(this)

            //calling the viewEntry method of DatabaseHandler class to read the records
            val emp: List<EmpModelClass> = databaseHandler.viewEntry()
            val empArrayId = Array(emp.size){"0"}
            for((index, e) in emp.withIndex()){
                empArrayId[index] = e.userId.toString()
                println("empArrayId[index] = ${empArrayId[index]}")
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, empArrayId)

            //creating custom ArrayAdapter
            listView = findViewById<ListView>(R.id.listView)
            listView.adapter = adapter

        }
        val viewRecordButton: Button = findViewById(R.id.viewRecordButton)
        viewRecordButton.setOnClickListener {
            viewRecord()
        }

        //method for updating records based on user id
        fun updateRecord() {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.update_dialog, null)
            dialogBuilder.setView(dialogView)

            val edtId = dialogView.findViewById(R.id.updateId) as EditText

            dialogBuilder.setTitle("Update Record")
            dialogBuilder.setMessage("Enter data below")
            dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

                val updateId = edtId.text.toString()
                //creating the instance of DatabaseHandler class
                val databaseHandler = DatabaseHandler(this)
                if(updateId.trim()!=""){
                    //calling the updateEntry method of DatabaseHandler class to update record
                    val status = databaseHandler.updateEntry(EmpModelClass(Integer.parseInt(updateId)))
                    if(status > -1){
                        Toast.makeText(applicationContext,"record update",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
                }

            })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                //pass
            })
            val b = dialogBuilder.create()
            b.show()
        }
        val updateRecordButton: Button = findViewById(R.id.updateRecordButton)
        updateRecordButton.setOnClickListener {
            updateRecord()
        }

        //method for deleting records based on id
        fun deleteRecord() {
            //creating AlertDialog for taking user id
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.delete_dialog, null)
            dialogBuilder.setView(dialogView)

            val dltId = dialogView.findViewById(R.id.deleteId) as EditText
            dialogBuilder.setTitle("Delete Record")
            dialogBuilder.setMessage("Enter id below")
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->

                val deleteId = dltId.text.toString()
                //creating the instance of DatabaseHandler class
                val databaseHandler = DatabaseHandler(this)
                if(deleteId.trim()!=""){
                    //calling the deleteEntry method of DatabaseHandler class to delete record
                    val status = databaseHandler.deleteEntry(EmpModelClass(Integer.parseInt(deleteId)))
                    if(status > -1){
                        Toast.makeText(applicationContext,"record deleted",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(applicationContext,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
                }

            })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                //pass
            })
            val b = dialogBuilder.create()
            b.show()
        }
        val deleteRecordButton: Button = findViewById(R.id.deleteRecordButton)
        deleteRecordButton.setOnClickListener {
            deleteRecord()
        }

    }
}