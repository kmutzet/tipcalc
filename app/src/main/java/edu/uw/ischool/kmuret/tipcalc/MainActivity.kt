package edu.uw.ischool.kmuret.tipcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
    private lateinit var serviceFee: EditText
    private lateinit var tipButton: Button
    private lateinit var tipPercentageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceFee = findViewById(R.id.service_fee)
        tipButton = findViewById(R.id.tip_button)
        tipPercentageSpinner = findViewById(R.id.tip_percentage_spinner)

        tipButton.isEnabled = false

        val percentages = arrayOf("10%", "15%", "18%", "20%")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, percentages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipPercentageSpinner.adapter = adapter

        serviceFee.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isUpdating = false

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                val newString = s.toString()
                if (newString != current) {
                    serviceFee.removeTextChangedListener(this) // Temporarily remove listener to prevent recursion

                    val cleanString = newString.replace("[^\\d]".toRegex(), "") // Strip out all non-digit characters
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = DecimalFormat("$#,##0.00").format(parsed / 100)

                        current = formatted
                        isUpdating = true // Set flag to prevent recursion
                        serviceFee.setText(formatted)
                        serviceFee.setSelection(formatted.length) // Ensure cursor is at the end
                        isUpdating = false // Reset flag

                        tipButton.isEnabled = true // Enable the button once valid input is present
                    } else {
                        tipButton.isEnabled = false // Disable the button if the input is empty
                    }

                    serviceFee.addTextChangedListener(this) // Re-add listener after update
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tipButton.setOnClickListener {
            val serviceAmount = serviceFee.text.toString().replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: return@setOnClickListener

            val percentage = when (tipPercentageSpinner.selectedItem.toString()) {
                "10%" -> 0.10
                "15%" -> 0.15
                "18%" -> 0.18
                "20%" -> 0.20
                else -> 0.15
            }

            val tipAmount = serviceAmount * percentage
            val formattedTip = DecimalFormat("$#,##0.00").format(tipAmount)

            val toast = Toast.makeText(applicationContext, "Tip Amount: $formattedTip", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }
}