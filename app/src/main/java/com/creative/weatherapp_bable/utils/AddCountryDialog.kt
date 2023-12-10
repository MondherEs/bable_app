package com.creative.weatherapp_bable.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.creative.weatherapp_bable.R

@SuppressLint("MissingInflatedId")
class AddCountryDialog(context: Context, private val onCountryAdded: (String) -> Unit) : Dialog(context) {

    init {
        setContentView(R.layout.add_country)
        setCancelable(true)

        val editTextCountry = findViewById<EditText>(R.id.editTextCountry)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnConfirm.setOnClickListener {
            val newCountry = editTextCountry.text.toString().trim()
            if (newCountry.isNotEmpty()) {
                onCountryAdded.invoke(newCountry)
                dismiss()
            } else {
                Toast.makeText(context, context.getString(R.string.popupMsg), Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener { dismiss() }
    }
}
