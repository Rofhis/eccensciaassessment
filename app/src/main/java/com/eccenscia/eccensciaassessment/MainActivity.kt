package com.eccenscia.eccensciaassessment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

            buttonOne.setOnClickListener {
                if (firstNameEditText.validate() && surnameEditText.validate()) {
                    val firstNameAndSurnameOddLetters = "${firstNameEditText.getValue().toOddLetters()} ${surnameEditText.getValue().toEvenLetters()}"
                    sha256OutputTextView.text = firstNameAndSurnameOddLetters.toSHA256()
                    outputOneTextView.text = firstNameAndSurnameOddLetters
                }
            }

            buttonTwo.setOnClickListener {
                val binary = outputOneTextView.getValue().toBinary()
                sha256OutputTextView.text = binary.toSHA256()
                outputTwoTextView.text = binary
            }

            buttonThree.setOnClickListener {
                val hexString = outputOneTextView.getValue().toHexString()
                sha256OutputTextView.text = hexString.toSHA256()
                outputThreeTextView.text = hexString
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

    private fun String.toEvenLetters(): String {
        val thisString = this
        return StringBuilder().apply {
            thisString.split("").forEachIndexed { index, s ->
                if (index % 2 == 0) {
                    append(s)
                }
            }
        }.toString()
    }

    private fun String.toOddLetters(): String {
        val thisString = this
        return StringBuilder().apply {
            thisString.split("").forEachIndexed { index, s ->
                if (index % 2 != 0) {
                    append(s)
                }
            }
        }.toString()
    }

    private fun String.toHexString(): String {
        val md5 = MessageDigest.getInstance("md5")
        md5.update(this.toByteArray())
        return md5.digest().joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
    }

    private fun TextView.getValue(): String {
        return this.text.toString().trim()
    }

    private fun String.toBinary(): String {
        val result = StringBuilder()
        val chars: CharArray = this.toCharArray()
        for (char in chars) {
            result.append(
                String.format("%8s", Integer.toBinaryString(char.code)).replace(" ".toRegex(), "0") // zero pads
            )
        }
        return result.toString()
    }

    private fun String.toSHA256(): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}