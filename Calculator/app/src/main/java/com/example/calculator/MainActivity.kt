package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var leftOperandEt: EditText
    private lateinit var rightOperandEt: EditText
    private lateinit var signTv: TextView
    private lateinit var resultTv: TextView
    private lateinit var equallyBt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        leftOperandEt = findViewById(R.id.etLeftOperand)
        leftOperandEt.requestFocus()
        rightOperandEt = findViewById(R.id.etRightOperand)
        signTv = findViewById(R.id.tvOperation)
        resultTv = findViewById(R.id.tvResult)

        val btnPlus: Button = findViewById(R.id.buttonPlus)
        val btnMinus: Button = findViewById(R.id.buttonMinus)
        val btnMultiply: Button=findViewById(R.id.buttonMultiply)
        val btnDivide: Button=findViewById(R.id.buttonDivide)
        val btnClear: Button=findViewById(R.id.buttonClear)
        val btnBack: Button=findViewById(R.id.buttonBack)

        btnBack.setOnClickListener{removeDigit()}
        btnClear.setOnClickListener{clear()}
        btnPlus.setOnClickListener{inputOperation("+")}
        btnMinus.setOnClickListener{inputOperation("-")}
        btnMultiply.setOnClickListener{inputOperation("*")}
        btnDivide.setOnClickListener{inputOperation("/")}

        equallyBt = findViewById(R.id.buttonEqually)
        equallyBt.setOnClickListener{equallyButton()}

        val b1: Button=findViewById(R.id.button1)
        val b2: Button=findViewById(R.id.button2)
        val b3: Button=findViewById(R.id.button3)
        val b4: Button=findViewById(R.id.button4)
        val b5: Button=findViewById(R.id.button5)
        val b6: Button=findViewById(R.id.button6)
        val b7: Button=findViewById(R.id.button7)
        val b8: Button=findViewById(R.id.button8)
        val b9: Button=findViewById(R.id.button9)
        val b0: Button=findViewById(R.id.button0)
        val bDot: Button=findViewById(R.id.buttonDot)
        b1.setOnClickListener{numButton("1")}
        b2.setOnClickListener{numButton("2")}
        b3.setOnClickListener{numButton("3")}
        b4.setOnClickListener{numButton("4")}
        b5.setOnClickListener{numButton("5")}
        b6.setOnClickListener{numButton("6")}
        b7.setOnClickListener{numButton("7")}
        b8.setOnClickListener{numButton("8")}
        b9.setOnClickListener{numButton("9")}
        b0.setOnClickListener{numButton("0")}
        bDot.setOnClickListener{numButton(".")}
    }

    private fun removeDigit() {
        val focusedEt = if(leftOperandEt.isFocused) leftOperandEt else rightOperandEt
        if (focusedEt.text.isNotBlank()) {
            val newValue = focusedEt.text.dropLast(1)
            focusedEt.setText(newValue)
            focusedEt.setSelection(focusedEt.text.length)
        }
    }

    private fun clear() {
        leftOperandEt.setText("")
        rightOperandEt.setText("")
        signTv.text = ""
        signTv.hint = ""
        resultTv.text = ""
        leftOperandEt.error= null
        rightOperandEt.error=null

        leftOperandEt.requestFocus()
    }

    private fun inputOperation(op: String) {

        if (resultTv.text.isNotBlank()) {
            if(resultTv.text != "Математику учил?")
                leftOperandEt.setText(resultTv.text)
            resultTv.text = ""
        }

        if(op == "-") {
            if(leftOperandEt.text.isBlank()) {
                leftOperandEt.requestFocus()
                numButton("-")
                return
            }
            if(signTv.text.isNotBlank() && rightOperandEt.text.isBlank()) {
                rightOperandEt.requestFocus()
                numButton("-")
                return
            }
        }

        if (leftOperandEt.text.isNotBlank() &&
            rightOperandEt.text.isNotBlank() && signTv.text.isNotBlank()) {
            val result = calculate(signTv.text.toString())
            if(result == "Математику учил?" || result == null || result == "") {
                resultTv.text = "Математику учил?"
                leftOperandEt.setText("")
                signTv.text = ""
                rightOperandEt.setText("")
                leftOperandEt.requestFocus()
                return
            }
            leftOperandEt.setText(result)
            rightOperandEt.setText("")
        }

        signTv.text = op
        rightOperandEt.requestFocus()
    }

    private fun equallyButton() {
        if (leftOperandEt.text.isBlank() || leftOperandEt.text.toString() == "."
            ||  leftOperandEt.text.toString() == "-") {
            leftOperandEt.error = "Введите число"
        }
        else if (signTv.text.isBlank()) {
            signTv.hint = "Введите \nоперацию"
        }
        else if ( rightOperandEt.text.isBlank() || rightOperandEt.text.toString() == "."
            || rightOperandEt.text.toString() == "-") {
            rightOperandEt.error = "Введите число"
        } else {
            resultTv.text = calculate(signTv.text.toString())
            leftOperandEt.setText("")
            rightOperandEt.setText("")
            leftOperandEt.requestFocus()
            signTv.text = ""
            signTv.hint = ""
        }
    }

    private fun numButton(digit: String) {
        val focusedEt = if(leftOperandEt.isFocused) leftOperandEt else rightOperandEt
        val text = focusedEt.text.toString()

        if(text.contains(".") && digit == ".")
            return

        if((text == "0" || text == "-0") && digit != ".")
        {
            focusedEt.setText(focusedEt.text.dropLast(1))
            focusedEt.setSelection(focusedEt.text.length)
        }

        if((text.isBlank() || text == "-") && digit == ".")
            focusedEt.text.append("0")

        focusedEt.text.append(digit)

        if(resultTv.text.isNotBlank())
            resultTv.text = ""
    }

    private fun calculate(op : String) : String? {
        val leftNum = leftOperandEt.text.toString().toBigDecimalOrNull()
        val rightNum = rightOperandEt.text.toString().toBigDecimalOrNull()

        if (leftNum == null || rightNum == null)
            return null

        val result = when (op) {
            "+" -> leftNum + rightNum
            "-" -> leftNum - rightNum
            "*" -> leftNum * rightNum
            "/" -> {
                if (rightNum.toDouble() != 0.0)
                    leftNum.toDouble() / rightNum.toDouble()
                else
                    return "Математику учил?"
            }
            else -> Double.NaN
        }

        if(result.toInt().toDouble() == result.toDouble())
            return result.toInt().toString()
        return result.toString()
    }
}