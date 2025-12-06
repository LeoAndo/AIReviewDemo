package jp.ac.jec.cm0199.aireviewdemo

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the SplashScreen before any other setup
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTotal = findViewById<EditText>(R.id.editTotalAmount)
        val editPeople = findViewById<EditText>(R.id.editPeopleCount)
        val editTip = findViewById<EditText>(R.id.editTipPercent)
        val editService = findViewById<EditText>(R.id.editServiceFee)
        val radioGroup = findViewById<RadioGroup>(R.id.radioRoundGroup)
        val btnCalc = findViewById<Button>(R.id.btnCalculate)
        val textPerPerson = findViewById<TextView>(R.id.textPerPerson)
        val textTotalWithTip = findViewById<TextView>(R.id.textTotalWithTip)

        val currency = NumberFormat.getCurrencyInstance(Locale.JAPAN)

        btnCalc.setOnClickListener {
            // reset errors
            fun EditText.clearError() {
                error = null
            }
            editTotal.clearError()
            editPeople.clearError()
            editTip.clearError()
            editService.clearError()

            val totalAmount = parseDecimal(editTotal)
            val peopleCount = parseInt(editPeople)
            val tipPercent = parseDecimalAllowEmpty(editTip) ?: BigDecimal.ZERO
            val serviceFee = parseDecimalAllowEmpty(editService) ?: BigDecimal.ZERO

            var hasError = false
            if (totalAmount == null) {
                setError(editTotal, R.string.error_invalid_number)
                hasError = true
            } else if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                setError(editTotal, R.string.error_positive_number)
                hasError = true
            }
            if (peopleCount == null) {
                setError(editPeople, R.string.error_people_integer)
                hasError = true
            } else if (peopleCount < 1) {
                setError(editPeople, R.string.error_people_integer)
                hasError = true
            }
            if (serviceFee < BigDecimal.ZERO) {
                setError(editService, R.string.error_non_negative_number)
                hasError = true
            }
            if (hasError) return@setOnClickListener

            val hundred = BigDecimal(100)
            val rate = BigDecimal.ONE.add(tipPercent.divide(hundred, 10, RoundingMode.HALF_UP))
            val totalWithTip = totalAmount?.multiply(rate)
            val perPersonRaw =
                totalWithTip?.divide(BigDecimal(peopleCount!!), 10, RoundingMode.HALF_UP)

            val roundingMode = when (radioGroup.checkedRadioButtonId) {
                R.id.radioNearest -> RoundingMode.HALF_UP
                R.id.radioUp -> RoundingMode.CEILING
                R.id.radioDown -> RoundingMode.FLOOR
                else -> null // none
            }

            val perPerson =
                if (roundingMode == null) perPersonRaw else perPersonRaw?.setScale(0, roundingMode)
            val totalWithTipRounded = totalWithTip?.setScale(0, RoundingMode.HALF_UP)

            textPerPerson.text = getString(R.string.result_per_person, currency.format(perPerson))
            textTotalWithTip.text =
                getString(R.string.result_total_with_tip, currency.format(totalWithTipRounded))
        }
    }

    private fun parseDecimal(editText: EditText): BigDecimal? = try {
        val s = editText.text.toString().trim()
        if (s.isEmpty()) null else BigDecimal(s)
    } catch (_: Exception) {
        null
    }

    private fun parseDecimalAllowEmpty(editText: EditText): BigDecimal? = try {
        val s = editText.text.toString().trim()
        if (s.isEmpty()) null else BigDecimal(s)
    } catch (_: Exception) {
        null
    }

    private fun parseInt(editText: EditText): Int? = try {
        val s = editText.text.toString().trim()
        if (s.isEmpty()) null else s.toInt()
    } catch (_: Exception) {
        null
    }

    private fun setError(editText: EditText, resId: Int) {
        editText.error = getString(resId)
        editText.requestFocus()
    }
}
