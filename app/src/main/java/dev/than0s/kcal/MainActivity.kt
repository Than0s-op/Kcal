package dev.than0s.kcal

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.than0s.kcal.ui.theme.KcalTheme
import java.util.Stack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            KcalTheme {
                Content(this)
            }
        }
    }
}

//@Preview(showSystemUi = true)
@Composable
fun Content(context: Context?) {
    val expression = remember { mutableStateOf("") }
    Column() {
        TextField(
            value = expression.value,
            onValueChange = { expression.value = it },
            singleLine = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
        )
        CreateRow(sequence = arrayOf("6", "7", "8", "9"), expression = expression)
        CreateRow(sequence = arrayOf("3", "4", "5"), expression = expression)
        CreateRow(sequence = arrayOf("0", "1", "2"), expression = expression)
        CreateRow(sequence = arrayOf("+", "-", "*", "/"), expression = expression)
        CalButton(text = "=") {
            try {
                expression.value = evaluate(expression.value)
            } catch (e: Exception) {
                println("message: ${e.message}")
                Toast.makeText(context, "Invalid expression", Toast.LENGTH_LONG).show()
            }
        }
        CalButton(text = "->") {
            if (expression.value.isNotEmpty()) {
                expression.value = expression.value.substring(0, expression.value.length - 1)
            }
        }
    }
}

fun evaluate(expression: String): String {
    val op = arrayOf('+', '*', '-', '/')
    val priority = mapOf('+' to 1, '-' to 2, '*' to 3, '/' to 4)
    val operator = Stack<Char>()
    val operand = Stack<String>()
    val temp = mutableStateOf("")
    for (i in expression) {
        if (i in op) {
            operand.push(temp.value)
            temp.value = ""
            if (operator.isEmpty()) {
                operator.push(i)
            } else {
                while (operator.isNotEmpty() && priority[operator.peek()]!! >= priority[i]!!) {
                    val top = operator.pop()
                    if (operand.size > 1) {
                        val first = operand.pop().toInt()
                        val second = operand.pop().toInt()
                        when (top) {
                            '*' -> operand.push((first * second).toString())
                            '/' -> operand.push((first / second).toString())
                            '-' -> operand.push((first - second).toString())
                            '+' -> operand.push((first + second).toString())
                        }

                    } else {
                        throw Exception("first")
                    }
                }
                operator.push(i)
            }
        } else {
            temp.value += i
        }
    }
    if (temp.value.isNotEmpty()) operand.push(temp.value)
    while (operator.isNotEmpty()) {
        val top = operator.pop()
        if (operand.size > 1) {
            val second = operand.pop().toInt()
            val first = operand.pop().toInt()
            when (top) {
                '*' -> operand.push((first * second).toString())
                '/' -> operand.push((first / second).toString())
                '-' -> operand.push((first - second).toString())
                '+' -> operand.push((first + second).toString())
            }
        } else {
            throw Exception("second")
        }
    }
    return operand.peek()
}

fun join(expression: MutableState<String>, s: String) {
    val operator = arrayOf("+", "*", "-", "/")

    val numbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    if (s in operator) {
        if (expression.value.isNotEmpty() &&
            expression.value.last().toString() in numbers
        ) expression.value += s
    } else expression.value += s
}

@Composable
fun CreateRow(sequence: Array<String>, expression: MutableState<String>) {
    Row {
        for (i in sequence) {
            CalButton(text = i) {
                join(expression, i)
            }
        }
    }
}

@Composable
fun CalButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape
    ) {
        Text(text = text)
    }
}

@Preview(showSystemUi = true)
@Composable
fun ContentPreview() {
    Content(context = null)
}