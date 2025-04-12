package com.example.bwise

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.min

class GroupDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_group_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // HACK: this will crash if the any of the extras are null (not found)
        var username: String = intent.getStringExtra("username")!!
        var group_name: String = intent.getStringExtra("group_name")!!
        var creator: String = intent.getStringExtra("creator")!!
        // TODO: THIS SHOWS OUTDATED DATA
        var members: List<String> = intent.getStringArrayListExtra("members")!!


        val groupNameTextView = findViewById<TextView>(R.id.group_name_text_view)
        groupNameTextView.text = group_name

        displayData(members.toList())

        val addExpenseButton = findViewById<Button>(R.id.add_expense_button)
        val kickUserButton = findViewById<Button>(R.id.kick_user_button)

        addExpenseButton.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER  // Numeric-only input

            AlertDialog.Builder(this)
                .setTitle("Enter a number")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val numberText = input.text.toString()
                    val number = numberText.toIntOrNull()
                    if (number != null) {
                        // TODO: ADD API CALL
                        // ✅ Use your number here
                        Toast.makeText(this, "You entered: $number", Toast.LENGTH_SHORT).show()
                        // store it, use it, pass to a function etc.
                    } else {
                        Toast.makeText(this, "Invalid number!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        kickUserButton.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            AlertDialog.Builder(this)
                .setTitle("Enter a username")
                .setView(input).setPositiveButton("OK") { _, _ ->
                    val username = input.text.toString()
                    // TODO: ADD API CALL
                    // ✅ Use your username here
                    Toast.makeText(this, "You entered: $username", Toast.LENGTH_SHORT).show()
                    // store it, use it, pass to a function etc.
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun displayData(members: List<String>) {
        val membersTableLayout = findViewById<TableLayout>(R.id.members_table_layout)
        for (i in 0 until min(membersTableLayout.childCount, members.size)) {
            val childRow = membersTableLayout.getChildAt(i)
            if (childRow is TableRow) {
                val memberTextView = childRow.getChildAt(0) as TextView
                memberTextView.text = members[i]
                childRow.setOnClickListener {
                    Toast.makeText(this, "Member clicked: ${members[i]}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}