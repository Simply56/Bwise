package com.example.bwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val username = intent.getStringExtra("username")
        if (username != null) {
            tryGetUserGroups(username)
        } else {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
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
    }

    object RetrofitClient {
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://152.67.64.149:5000")
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
    }

    // Makes gets user groups and calls function to display them if successful
    private fun tryGetUserGroups(username: String) {
        // call API to get user groups
        val request = GetUserGroupsRequest(username = username)

        RetrofitClient.apiService.getUserGroups(request)
            .enqueue(object : Callback<GetUserGroupsResponse> {
                override fun onResponse(
                    call: Call<GetUserGroupsResponse>,
                    response: Response<GetUserGroupsResponse>
                ) {
                    if (response.isSuccessful) {
                        displayGroups(response.body()?.groups ?: emptyList())
                    } else {
                        Toast.makeText(
                            this@GroupsOverviewActivity,
                            "Login failed",
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
                    )
                        .show()
                    Log.e("API_FAILURE", "Request failed", t)
                }
            })
    }

    private fun displayGroups(groups: List<Group>) {
        val groupTextViews = listOf(
            findViewById<TextView>(R.id.group_text_view_0),
            findViewById<TextView>(R.id.group_text_view_1),
            findViewById<TextView>(R.id.group_text_view_2),
            findViewById<TextView>(R.id.group_text_view_3),
            findViewById<TextView>(R.id.group_text_view_4),
            findViewById<TextView>(R.id.group_text_view_5),
            findViewById<TextView>(R.id.group_text_view_6),
            findViewById<TextView>(R.id.group_text_view_7),
            findViewById<TextView>(R.id.group_text_view_8),
            findViewById<TextView>(R.id.group_text_view_9),
            findViewById<TextView>(R.id.group_text_view_10),

            )
        for (i in groups.indices) {
            groupTextViews[i].text = groups[i].name
        }
    }
}