package com.example.bwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.math.min

class GroupsOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_groups_overview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var username = intent.getStringExtra("username")
        if (username == null) {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@GroupsOverviewActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            username = "this will never happen"
        }
        tryGetUserGroups(username)
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


    data class GetUserGroupsRequest(
        val username: String
    )

    data class GetUserGroupsResponse(
        val groups: List<Group>
    )

    data class Group(
        val creator: String,
        val members: List<String>,
        val name: String,
        val transactions: List<Transaction>
    )

    data class Transaction(
        val from_user: String,
        val to_user: String,
        val amount: Int,
    )

    interface ApiService {
        @POST("/get_user_groups")
        fun getUserGroups(@Body request: GetUserGroupsRequest): Call<GetUserGroupsResponse>

        @POST("/create_group")
        fun createGroup(@Body request: CreateGroupRequest): Call<CreateGroupResponse>

        @POST("/join_group")
        fun joinGroup(@Body request: JoinGroupRequest): Call<JoinGroupResponse>

        @POST("/delete_group")
        fun deleteGroup(@Body request: DeleteGroupRequest): Call<DeleteGroupResponse>
    }

    object RetrofitClient {
        private val retrofit = Retrofit.Builder().baseUrl("http://152.67.64.149:5000")
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
    }

    /**
     * Gets the user's groups from the API and displays them in the UI.
     */
    private fun tryGetUserGroups(username: String) {
        // call API to get user groups
        val request = GetUserGroupsRequest(username = username)

        RetrofitClient.apiService.getUserGroups(request)
            .enqueue(object : Callback<GetUserGroupsResponse> {
                override fun onResponse(
                    call: Call<GetUserGroupsResponse>, response: Response<GetUserGroupsResponse>
                ) {
                    if (response.isSuccessful) {
                        displayGroups(response.body()?.groups ?: emptyList(), username)
                    } else {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Failed to get user groups",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("API_ERROR", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetUserGroupsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@GroupsOverviewActivity,
                        "Failed to send/receive data",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_FAILURE", "Request failed", t)
                }
            })
    }

    private fun displayGroups(groups: List<Group>, username: String) {
        val groupLinearLayout = findViewById<LinearLayout>(R.id.group_linear_layout)

        // clear text from children
        for (i in 0 until groupLinearLayout.childCount) {
            val childView = groupLinearLayout.getChildAt(i)
            if (childView is TextView) {
                childView.text = ""
            }
        }

        for (i in 0 until min(groupLinearLayout.childCount, groups.size)) {
            val childView = groupLinearLayout.getChildAt(i)
            if (childView is TextView) {
                childView.text = groups[i].name
                childView.setOnClickListener {

                    val intent =
                        Intent(this@GroupsOverviewActivity, GroupDetailsActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("group_name", groups[i].name)
                    intent.putExtra("creator", groups[i].creator)
                    // TODO: THIS SHOWS OUTDATED DATA IF USER JOINED AFTER LOADING GROUPS
                    intent.putStringArrayListExtra("members", ArrayList(groups[i].members))
                    startActivity(intent)

                    Toast.makeText(this, "Group clicked: ${groups[i].name}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    data class CreateGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class CreateGroupResponse(
        val group: Group,
    )

    private fun tryCreateGroup(username: String, newGroupName: String) {
        val request = CreateGroupRequest(
            group_name = newGroupName, username = username
        )

        RetrofitClient.apiService.createGroup(request)
            .enqueue(object : Callback<CreateGroupResponse> {
                override fun onResponse(
                    call: Call<CreateGroupResponse>, response: Response<CreateGroupResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Creating group $newGroupName",
                            Toast.LENGTH_SHORT
                        ).show()
                        tryGetUserGroups(username)
                    } else {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Failed to create group",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("API_ERROR", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CreateGroupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@GroupsOverviewActivity,
                        "Failed to send/receive data",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_FAILURE", "Request failed", t)
                }
            })
    }


    data class JoinGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class JoinGroupResponse(
        val group: Group,
    )

    private fun tryJoinGroup(username: String, newGroupName: String) {
        val request = JoinGroupRequest(
            group_name = newGroupName, username = username
        )

        RetrofitClient.apiService.joinGroup(request)
            .enqueue(object : Callback<JoinGroupResponse> {
                override fun onResponse(
                    call: Call<JoinGroupResponse>, response: Response<JoinGroupResponse>
                ) {
                    if (response.isSuccessful) {
                        tryGetUserGroups(username)
                    } else {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Failed to join group",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("API_ERROR", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<JoinGroupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@GroupsOverviewActivity,
                        "Failed to send/receive data",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_FAILURE", "Request failed", t)
                }
            })
    }

    data class DeleteGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class DeleteGroupResponse(
        val error: String
    )

    private fun tryDeleteGroup(username: String, groupToDelete: String) {
        val request = DeleteGroupRequest(
            group_name = groupToDelete, username = username
        )

        RetrofitClient.apiService.deleteGroup(request)
            .enqueue(object : Callback<DeleteGroupResponse> {
                override fun onResponse(
                    call: Call<DeleteGroupResponse>, response: Response<DeleteGroupResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Deleting group $groupToDelete",
                            Toast.LENGTH_SHORT
                        ).show()
                        tryGetUserGroups(username) // show the updated group list
                    } else {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Failed to delete group",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("API_ERROR", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<DeleteGroupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@GroupsOverviewActivity,
                        "Failed to send/receive data",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_FAILURE", "Request failed", t)
                }
            })
    }
}