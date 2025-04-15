package com.example.bwise

import android.app.AlertDialog
import android.graphics.Color
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
import com.example.bwise.DataClasses.AddExpenseRequest
import com.example.bwise.DataClasses.AddExpenseResponse
import com.example.bwise.DataClasses.Debt
import com.example.bwise.DataClasses.GetDebtsRequest
import com.example.bwise.DataClasses.GetDebtsResponse
import com.example.bwise.DataClasses.GetUserGroupsRequest
import com.example.bwise.DataClasses.GetUserGroupsResponse
import com.example.bwise.DataClasses.SettleUpRequest
import com.example.bwise.DataClasses.SettleUpResponse
import retrofit2.Response
import kotlin.math.min

class GroupDetailsActivity : AppCompatActivity() {

    var username: String = ""
    var group_name: String = ""
    var creator: String = ""
    var members: List<String> = emptyList()
    var debts: List<Debt> = emptyList()

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

        val addExpenseButton = findViewById<Button>(R.id.add_expense_button)
        val kickUserButton = findViewById<Button>(R.id.kick_user_button)

        addExpenseButton.setOnClickListener {
            val input = EditText(this)
            // allows decimals on a number only keyboard
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            AlertDialog.Builder(this)
                .setTitle("Enter a number")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val numberText = input.text.toString()
                    val number = numberText.toDoubleOrNull()
                    if (number != null) {
                        tryAddExpense(username, group_name, number)
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
                    // store it, use it, pass to a function etc.
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        // refresh the members list each time the activity is resumed
        tryGetDebts(username, group_name)
    }

    private fun populateMemberRows() {
        val membersTableLayout = findViewById<TableLayout>(R.id.members_table_layout)


        // clear text from all member text views
        for (i in 0 until membersTableLayout.childCount) {
            if (membersTableLayout.getChildAt(i) !is TableRow) {
                continue
            }
            val childRow = membersTableLayout.getChildAt(i) as TableRow

            val memberTextView = childRow.getChildAt(0) as TextView
            val debtTextView = childRow.getChildAt(1) as TextView
            memberTextView.text = ""
            debtTextView.text = ""
        }

        // adds the correct name to each member and their relative debt
        for (i in 0 until min(membersTableLayout.childCount, members.size)) {
            val childRow = membersTableLayout.getChildAt(i)
            if (childRow !is TableRow) {
                continue
            }
            childRow.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Settle debts with ${members[i]}?")
                    .setPositiveButton("Confirm") { _, _ ->
                        trySettleUp(username, group_name, members[i])
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            val memberTextView = childRow.getChildAt(0) as TextView
            memberTextView.text = members[i]

            // show the color debt rounded to 2 decimal places
            val debtTextView = childRow.getChildAt(1) as TextView
            for (debt in debts) {
                if (debt.username != members[i]) {
                    continue
                }
                debtTextView.text = String.format("%.2f", debt.amount)
                when (debt.status) {
                    "owes you" -> debtTextView.setTextColor(Color.GREEN)
                    "you owe" -> debtTextView.setTextColor(Color.RED)
                }
                break
            }
        }
    }


    private fun tryAddExpense(username: String, group_name: String, amount: Double) {
        val request = AddExpenseRequest(username, group_name, amount)

        RetrofitClient.apiService.addExpense(request)
            .enqueue(object : BaseCallback<AddExpenseResponse>
                (this) {
                override fun handleSuccess(response: Response<AddExpenseResponse>) {
                    tryGetDebts(username, group_name)
                }
            })
    }

    private fun tryGetDebts(username: String, group_name: String) {
        var request = GetDebtsRequest(username, group_name)

        RetrofitClient.apiService.getDebts(request)
            .enqueue(object : BaseCallback<GetDebtsResponse>(this) {
                override fun handleSuccess(response: Response<GetDebtsResponse>) {
                    debts = response.body()?.debts ?: emptyList()
                    var currentMembers = ArrayList<String>()
                    for (debt in debts) {
                        currentMembers.add(debt.username)
                    }
                    members = currentMembers
                    populateMemberRows()
                }
            })
    }

    private fun trySettleUp(username: String, group_name: String, to_user: String) {
        var request = SettleUpRequest(username, group_name, to_user)

        RetrofitClient.apiService.settleUp(request)
            .enqueue(object : BaseCallback<SettleUpResponse>(this) {
                override fun handleSuccess(response: Response<SettleUpResponse>) {
                    super.handleSuccess(response)
                    tryGetDebts(username, group_name)
                }
            })
    }
}
