package com.example.createitinerary

import android.app.DatePickerDialog
import android.database.Cursor
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class CreateItineraryActivity : AppCompatActivity() {

    private lateinit var etTripName: EditText
    private lateinit var etDestination: EditText
    private lateinit var tilTripName: TextInputLayout
    private lateinit var tilDestination: TextInputLayout
    private lateinit var btnStartDate: MaterialButton
    private lateinit var btnEndDate: MaterialButton
    private lateinit var btnSaveItinerary: MaterialButton
    private lateinit var dbHelper: ItineraryDatabaseHelper

    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_itinerary)

        // Initialize Database Helper
        dbHelper = ItineraryDatabaseHelper(this)

        // Initialize UI components
        etTripName = findViewById(R.id.etTripName)
        etDestination = findViewById(R.id.etDestination)
        tilTripName = findViewById(R.id.tilTripName)
        tilDestination = findViewById(R.id.tilDestination)
        btnStartDate = findViewById(R.id.btnStartDate)
        btnEndDate = findViewById(R.id.btnEndDate)
        btnSaveItinerary = findViewById(R.id.btnSaveItinerary)

        // Set up simple animation
        val buttonClickAnimation = AlphaAnimation(1f, 0.8f)

        // Calendar instance for date pickers
        val calendar = Calendar.getInstance()

        // Set up Start Date picker
        btnStartDate.setOnClickListener {
            it.startAnimation(buttonClickAnimation)
            DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    startDate = dateFormat.format(calendar.time)
                    btnStartDate.text = startDate
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Set up End Date picker
        btnEndDate.setOnClickListener {
            it.startAnimation(buttonClickAnimation)
            DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    endDate = dateFormat.format(calendar.time)
                    btnEndDate.text = endDate
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Save itinerary when button is clicked
        btnSaveItinerary.setOnClickListener {
            it.startAnimation(buttonClickAnimation)
            insertItinerary()
        }

        // Example of retrieving and displaying all data
        displayAllItineraries()
    }

    // Function to insert data into the database
    private fun insertItinerary() {
        val tripName = etTripName.text.toString().trim()
        val destination = etDestination.text.toString().trim()

        tilTripName.error = null
        tilDestination.error = null

        if (tripName.isEmpty()) {
            tilTripName.error = "Trip Name is required"
            return
        }

        if (destination.isEmpty()) {
            tilDestination.error = "Destination is required"
            return
        }

        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Please select start and end dates", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validateDates()) {
            Toast.makeText(this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert data into the database
        val result = dbHelper.insertItinerary(tripName, destination, startDate!!, endDate!!)
        if (result > 0) {
            Toast.makeText(this, "Itinerary Saved!", Toast.LENGTH_SHORT).show()
            clearFields()
        } else {
            Toast.makeText(this, "Failed to Save Itinerary", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to validate date range
    private fun validateDates(): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val start = sdf.parse(startDate!!)
        val end = sdf.parse(endDate!!)
        return start <= end
    }

    // Function to retrieve and display all itineraries
    private fun displayAllItineraries() {
        val cursor: Cursor = dbHelper.getAllItineraries()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val tripName = cursor.getString(cursor.getColumnIndexOrThrow("trip_name"))
                val destination = cursor.getString(cursor.getColumnIndexOrThrow("destination"))
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"))

                // Log or use the retrieved data
                println("Itinerary: $id, $tripName, $destination, $startDate, $endDate")
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    // Function to clear input fields
    private fun clearFields() {
        etTripName.text?.clear()
        etDestination.text?.clear()
        btnStartDate.text = "Select Start Date"
        btnEndDate.text = "Select End Date"
        startDate = null
        endDate = null
        tilTripName.error = null
        tilDestination.error = null
    }
}
