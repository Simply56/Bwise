package com.example.bwise

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.bwise.DataClasses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.bwise.DataClasses.BaseResponse
import com.google.gson.Gson


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

    @POST("/add_expense")
    fun addExpense(@Body request: AddExpenseRequest): Call<AddExpenseResponse>

    @POST("/get_debts")
    fun getDebts(@Body request: GetDebtsRequest): Call<GetDebtsResponse>

    @POST("/settle_up")
    fun settleUp(@Body request: SettleUpRequest): Call<SettleUpResponse>
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://152.67.64.149:5000")
        .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

/**
 * Base class for Retrofit callbacks.
 * It provides default methods for handling successful and failed responses.
 * These methods can be overridden in subclasses to customize the behavior.
 */
abstract class BaseCallback<T : BaseResponse>(private val context: Context) : Callback<T> {


    /**
     * Called if API call got response
     */
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            handleSuccess(response)
        } else {
            handleFailure(response)
        }
    }

    /**
     * Called if API call got response and it was successful
     */
    open fun handleSuccess(response: Response<T>) {
        val msg = response.body()?.message
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        Log.i("API_SUCCESS", msg ?: "")
    }

    /**
     * Called if API call got response but it was **not** successful
     */
    open fun handleFailure(response: Response<T>) {
        val errorBody = response.errorBody()?.string()
        var message = "Failure: ${response.code()}"

        try {
            val gson = Gson()
            val errorMap = gson.fromJson(errorBody, Map::class.java)
            if (errorMap != null && errorMap.containsKey("message")) {
                message = errorMap["message"].toString()
            }
        } catch (e: Exception) {
            Log.e("API_FAILURE", "Failed to parse error response", e)
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    /**
     * Called if API call failed
     */
    override fun onFailure(call: Call<T>, t: Throwable) {
        Toast.makeText(context, "Failed to send/receive data", Toast.LENGTH_LONG).show()
        Log.e("API_FAILURE", "Request failed", t)
    }

}