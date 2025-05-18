package com.example.bwise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bwise.DataClasses.CreateGroupRequest
import com.example.bwise.DataClasses.CreateGroupResponse
import com.example.bwise.DataClasses.DeleteGroupRequest
import com.example.bwise.DataClasses.DeleteGroupResponse
import com.example.bwise.DataClasses.GetUserGroupsRequest
import com.example.bwise.DataClasses.GetUserGroupsResponse
import com.example.bwise.DataClasses.Group
import com.example.bwise.DataClasses.JoinGroupRequest
import com.example.bwise.DataClasses.JoinGroupResponse
import retrofit2.Response
import kotlin.math.min

class GroupsOverviewActivity : AppCompatActivity() {
    private var username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_groups_overview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val createGroupButton = findViewById<Button>(R.id.create_group_button)
        val joinGroupButton = findViewById<Button>(R.id.join_group_button)
        val deleteGroupButton = findViewById<Button>(R.id.delete_group_button)
        val groupNameInput = findViewById<EditText>(R.id.group_name_edit_text)

        createGroupButton.setOnClickListener {
            tryCreateGroup(username, groupNameInput.text.toString())
        }
        joinGroupButton.setOnClickListener {
            tryJoinGroup(username, groupNameInput.text.toString())
        }
        deleteGroupButton.setOnClickListener {
            tryDeleteGroup(username, groupNameInput.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        // HACK: this will crash if the username is null (not found)
        username = intent.getStringExtra("username")!!
        tryGetUserGroups(username)
    }


    private fun populateGroupList(groups: List<Group>, username: String) {
        val groupLinearLayout = findViewById<LinearLayout>(R.id.group_linear_layout)

        // clear text from all group text views
        for (i in 0 until groupLinearLayout.childCount) {
            val childView = groupLinearLayout.getChildAt(i)
            if (childView is TextView) {
                childView.text = ""
            }
        }

        for (i in 0 until min(groupLinearLayout.childCount, groups.size)) {
            val childView = groupLinearLayout.getChildAt(i)
            if (childView !is TextView) {
                continue
            }
            childView.text = groups[i].name
            childView.setOnClickListener {
                val intent =
                    Intent(this@GroupsOverviewActivity, GroupDetailsActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("group_name", groups[i].name)
                intent.putExtra("creator", groups[i].creator)
                startActivity(intent)
            }
        }
    }

    /**
     * Gets the user's groups from the API and displays them in the UI.
     */
    private fun tryGetUserGroups(username: String) {
        val request = GetUserGroupsRequest(username)

        RetrofitClient.apiService.getUserGroups(request)
            .enqueue(object : BaseCallback<GetUserGroupsResponse>(this) {
                override fun handleSuccess(response: Response<GetUserGroupsResponse>) {
                    populateGroupList(response.body()?.groups ?: emptyList(), username)
                }
            })
    }


    private fun tryCreateGroup(username: String, newGroupName: String) {
        val request = CreateGroupRequest(username, newGroupName)

        RetrofitClient.apiService.createGroup(request)
            .enqueue(object : BaseCallback<CreateGroupResponse>(this) {
                override fun handleSuccess(response: Response<CreateGroupResponse>) {
                    tryGetUserGroups(username)
                }
            })
    }


    private fun tryJoinGroup(username: String, newGroupName: String) {
        val request = JoinGroupRequest(username, newGroupName)

        RetrofitClient.apiService.joinGroup(request)
            .enqueue(object : BaseCallback<JoinGroupResponse>(this) {
                override fun handleSuccess(response: Response<JoinGroupResponse>) {
                    tryGetUserGroups(username)
                }
            })
    }


    private fun tryDeleteGroup(username: String, groupToDelete: String) {
        val request = DeleteGroupRequest(username = username, group_name = groupToDelete)

        RetrofitClient.apiService.deleteGroup(request)
            .enqueue(object : BaseCallback<DeleteGroupResponse>(this) {
                override fun handleSuccess(response: Response<DeleteGroupResponse>) {
                    tryGetUserGroups(username) // show the updated group list
                }
            })
    }

}