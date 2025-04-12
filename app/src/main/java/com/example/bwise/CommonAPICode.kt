package com.example.bwise

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.bwise.DataClasses.CreateGroupRequest
import com.example.bwise.DataClasses.CreateGroupResponse
import com.example.bwise.DataClasses.DeleteGroupRequest
import com.example.bwise.DataClasses.DeleteGroupResponse
import com.example.bwise.DataClasses.GetUserGroupsRequest
import com.example.bwise.DataClasses.GetUserGroupsResponse
import com.example.bwise.DataClasses.JoinGroupRequest
import com.example.bwise.DataClasses.JoinGroupResponse
import com.example.bwise.DataClasses.LoginRequest
import com.example.bwise.DataClasses.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
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
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://152.67.64.149:5000")
        .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

abstract class BaseCallback<T>(private val context: Context) : Callback<T> {

    abstract fun handleSuccess(response: Response<T>)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            handleSuccess(response)
        } else {
            handleFailure("API returned an error: ${response.code()}")
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        handleFailure("Request failed: ${t.message}")
        Log.e("API_FAILURE", "Request failed", t)
    }

    private fun handleFailure(message: String) {
        Toast.makeText(context, "Failed to send/receive data", Toast.LENGTH_SHORT).show()
        Log.e("API_FAILURE", message)
    }
}