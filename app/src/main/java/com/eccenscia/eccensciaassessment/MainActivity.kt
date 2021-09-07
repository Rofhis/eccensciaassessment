package com.eccenscia.eccensciaassessment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eccenscia.eccensciaassessment.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            outputOneTextView.addVisibilityStateWatcher()
            outputTwoTextView.addVisibilityStateWatcher()
            outputThreeTextView.addVisibilityStateWatcher()
            sha256OutputTextView.addVisibilityStateWatcher()

            setSupportActionBar(toolbar)

            buttonOne.setOnClickListener {
                if (firstNameEditText.validate() && surnameEditText.validate()) {
                    val firstNameAndSurnameOddLetters = "${firstNameEditText.getValue().toOddLetters()} ${surnameEditText.getValue().toEvenLetters()}"
                    sha256OutputTextView.text = firstNameAndSurnameOddLetters.toSHA256()
                    outputOneTextView.text = firstNameAndSurnameOddLetters
                    Log.i(javaClass.simpleName, "Odd/Even Letters: $firstNameAndSurnameOddLetters")
                    Log.i(javaClass.simpleName, "Odd/Even Letters SHA256: ${firstNameAndSurnameOddLetters.toSHA256()}")
                }
            }

            buttonTwo.setOnClickListener {
                if (outputOneTextView.getValue().isNotEmpty()) {
                    val binaryString = outputOneTextView.getValue().toBinary()
                    sha256OutputTextView.text = binaryString.toSHA256()
                    outputTwoTextView.text = binaryString
                    Log.i(javaClass.simpleName, "Binary String: $binaryString")
                    Log.i(javaClass.simpleName, "Binary SHA256: ${binaryString.toSHA256()}")
                }
            }

            buttonThree.setOnClickListener {
                if (outputOneTextView.getValue().isNotEmpty()) {
                    val hexString = outputOneTextView.getValue().toHexadecimal()
                    sha256OutputTextView.text = hexString.toSHA256()
                    outputThreeTextView.text = hexString
                    Log.i(javaClass.simpleName, "Hexadecimal String: $hexString")
                    Log.i(javaClass.simpleName, "Hexadecimal SHA256: ${hexString.toSHA256()}")
                }
            }
        }
    }

    private fun TextView.addVisibilityStateWatcher() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                visibility = View.VISIBLE
            }
        })
    }

    private fun EditText.validate(): Boolean {
        return if (this.getValue().isNotEmpty()) {
            this.error = null
            true
        } else {
            this.error = context.getString(R.string.error_please_enter_valid_value)
            false
        }
    }

    private fun String.toEvenLetters(): String = split("").filterIndexed { index, _ -> index % 2 == 0 }.joinToString("")

    private fun String.toOddLetters(): String = split("").filterIndexed { index, _ -> index % 2 != 0 }.joinToString("")

    private fun String.toHexadecimal(): String {
        val result = StringBuilder()
        val chars = this.toCharArray()
        for (char in chars) {
            result.append(Integer.toHexString(char.code))
        }
        return result.toString()
    }

    private fun TextView.getValue(): String = this.text.toString()

    private fun String.toBinary(): String {
        val result = StringBuilder()
        val chars = this.toCharArray()
        for (char in chars) {
            result.append(String.format("%8s", Integer.toBinaryString(char.code)))
        }
        return result.toString()
    }

    private fun String.toSHA256(): String = MessageDigest
        .getInstance("SHA-256")
        .digest(this.toByteArray())
        .fold("", { str, it -> str + "%02x".format(it) })
}