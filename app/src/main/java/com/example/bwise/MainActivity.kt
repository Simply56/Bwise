package com.example.bwise

import android.util.Log
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            tryLogin()
        }
    }

    data class LoginRequest(
        val username: String,
    )

    data class LoginResponse(
        val username: String,
    )

    interface ApiService {
        @POST("/login")  // Change this to your endpoint
        fun login(@Body request: LoginRequest): Call<LoginResponse>
    }

    object RetrofitClient {
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://152.67.64.149:5000")
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
    }

    private fun tryLogin() {
        val usernameEditText = findViewById<EditText>(R.id.user_edit_text)

        val request = LoginRequest(username = usernameEditText.text.toString())

        RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Login successful, handle the response, move to the next activity
                    val intent = Intent(this@MainActivity, GroupsOverviewActivity::class.java)
                    intent.putExtra("username", response.body()?.username)
                    startActivity(intent)
                    finish() // This makes it so that the user can't go back to the login screen
                } else {
                    Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to send/receive data", Toast.LENGTH_SHORT)
                    .show()
                Log.e("API_FAILURE", "Request failed", t)
            }
        })


    }
}