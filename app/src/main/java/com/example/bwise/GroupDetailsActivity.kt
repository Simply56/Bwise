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


        var username = intent.getStringExtra("username")
        var group_name = intent.getStringExtra("group_name")
        var creator = intent.getStringExtra("creator")
        var members = intent.getStringArrayListExtra("members")

        if (username == null) {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@GroupDetailsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            username = "this will never happen"
        }
        if (group_name == null) {
            Toast.makeText(this, "No group_name provided", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@GroupDetailsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            group_name = "this will never happen"
        }

        if (creator == null) {
            Toast.makeText(this, "No creator provided", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@GroupDetailsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            creator = "this will never happen"
        }

        if (members == null) {
            Toast.makeText(this, "No members provided", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@GroupDetailsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            members = arrayListOf("this will never happen")
        }

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

//                    val intent =
//                        Intent(this, GroupDetailsActivity::class.java)
//                    intent.putExtra("username", username)
//                    intent.putExtra("group_name", members[i].name)
//                    intent.putExtra("creator", members[i].creator)
//                    intent.putStringArrayListExtra("members", ArrayList(members[i].members))
//                    startActivity(intent)

                    Toast.makeText(this, "Member clicked: ${members[i]}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}