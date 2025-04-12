package com.example.bwise

import android.app.AlertDialog
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
import com.example.bwise.DataClasses.GetUserGroupsRequest
import com.example.bwise.DataClasses.GetUserGroupsResponse
import retrofit2.Response
import kotlin.math.min

class GroupDetailsActivity : AppCompatActivity() {

    var username: String = ""
    var group_name: String = ""
    var creator: String = ""
    var members: List<String> = emptyList()

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
        username = intent.getStringExtra("username")!!
        group_name = intent.getStringExtra("group_name")!!
        creator = intent.getStringExtra("creator")!!


        val groupNameTextView = findViewById<TextView>(R.id.group_name_text_view)
        groupNameTextView.text = group_name

        populateMembers(members.toList())

        val addExpenseButton = findViewById<Button>(R.id.add_expense_button)
        val kickUserButton = findViewById<Button>(R.id.kick_user_button)

        addExpenseButton.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER  // Positive numeric-only input

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

    override fun onResume() {
        super.onResume()
        // refresh the members list each time the activity is resumed
        tryGetUserGroups(username)
    }


    private fun populateMembers(members: List<String>) {
        val membersTableLayout = findViewById<TableLayout>(R.id.members_table_layout)

        // clears text in each group member
        for (i in 0 until membersTableLayout.childCount) {
            if (membersTableLayout.getChildAt(i) !is TableRow) {
                continue
            }
            val childRow = membersTableLayout.getChildAt(i) as TableRow
            val memberTextView = childRow.getChildAt(0) as TextView
            memberTextView.text = ""
        }


        // adds the correct name to each member
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

    /**
     * Gets the user's groups from the API and displays them in the UI.
     */
    private fun tryGetUserGroups(username: String) {
        val request = GetUserGroupsRequest(username = username)

        RetrofitClient.apiService.getUserGroups(request)
            .enqueue(object : BaseCallback<GetUserGroupsResponse>(this) {
                override fun handleSuccess(response: Response<GetUserGroupsResponse>) {
                    // update members
                    for (group in response.body()?.groups ?: emptyList()) {
                        if (group.name == group_name) {
                            members = group.members
                            populateMembers(group.members)
                            break
                        }
                    }
                }

            })
    }
}
