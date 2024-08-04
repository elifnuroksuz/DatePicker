package com.elifnuroksuz.datepicker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private lateinit var textDate: TextView
private lateinit var buttonDate: Button
private lateinit var buttonRangeDate: Button
private lateinit var buttonDateFormat: Button
private lateinit var buttonTime: Button
private lateinit var buttonToggleTheme: Button
private lateinit var buttonChangeLanguage: Button
private var selectedDateFormat = "dd-MM-yyyy"
private val calendarBox = Calendar.getInstance()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textDate = findViewById(R.id.textDate)
        buttonDate = findViewById(R.id.buttonDate)
        buttonRangeDate = findViewById(R.id.buttonRangeDate)
        buttonDateFormat = findViewById(R.id.buttonDateFormat)
        buttonTime = findViewById(R.id.buttonTime)
        buttonToggleTheme = findViewById(R.id.buttonToggleTheme)
        buttonChangeLanguage = findViewById(R.id.buttonChangeLanguage)

        val dateBox = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)
            updateText(calendarBox)
        }

        buttonDate.setOnClickListener {
            DatePickerDialog(
                this, dateBox, calendarBox.get(Calendar.YEAR), calendarBox.get(Calendar.MONTH),
                calendarBox.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        buttonRangeDate.setOnClickListener {
            showDateRangeDialog()
        }

        buttonDateFormat.setOnClickListener {
            showDateFormatOptions()
        }

        buttonTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                textDate.text = time
            }, hour, minute, true).show()
        }

        buttonToggleTheme.setOnClickListener {
            toggleTheme()
        }

        buttonChangeLanguage.setOnClickListener {
            showLanguageOptions()
        }
    }

    private fun updateText(calendar: Calendar) {
        val sdf = SimpleDateFormat(selectedDateFormat, Locale.getDefault())
        val selectedDate = sdf.format(calendar.time)
        textDate.text = selectedDate
    }

    private fun showDateRangeDialog() {
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()

        val startDateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            startCalendar.set(Calendar.YEAR, year)
            startCalendar.set(Calendar.MONTH, month)
            startCalendar.set(Calendar.DAY_OF_MONTH, day)
        }

        val endDateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            endCalendar.set(Calendar.YEAR, year)
            endCalendar.set(Calendar.MONTH, month)
            endCalendar.set(Calendar.DAY_OF_MONTH, day)

            if (startCalendar.after(endCalendar)) {
                Toast.makeText(this, "Başlangıç tarihi bitiş tarihinden önce olmalı!", Toast.LENGTH_SHORT).show()
            } else {
                val sdf = SimpleDateFormat(selectedDateFormat, Locale.getDefault())
                val startDate = sdf.format(startCalendar.time)
                val endDate = sdf.format(endCalendar.time)
                textDate.text = "$startDate - $endDate"
            }
        }

        DatePickerDialog(this, startDateListener, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show()
        DatePickerDialog(this, endDateListener, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showDateFormatOptions() {
        val dateFormatOptions = arrayOf("dd-MM-yyyy", "MM-dd-yyyy", "yyyy-MM-dd", "EEE, d MMM yyyy")
        val selectedOption = dateFormatOptions.indexOf(selectedDateFormat)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Tarih Formatı Seçin")
            .setSingleChoiceItems(dateFormatOptions, selectedOption) { dialog, which ->
                selectedDateFormat = dateFormatOptions[which]
                updateText(calendarBox) // Seçilen formatı uygulamak için tarihi güncelleyin
                dialog.dismiss()
            }
            .show()
    }

    private fun showLanguageOptions() {
        val languages = arrayOf("Türkçe", "English")
        val languageCodes = arrayOf("tr", "en")

        val dialog = AlertDialog.Builder(this)
            .setTitle("Dil Seçin")
            .setItems(languages) { _, which ->
                changeLanguage(languageCodes[which])
                Toast.makeText(this, "Dil değiştirildi: ${languages[which]}", Toast.LENGTH_SHORT).show()
            }
            .create()
        dialog.show()
    }

    private fun changeLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // Aktiviteyi yeniden başlatır
    }

    private fun toggleTheme() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        recreate()
    }
}
