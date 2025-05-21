package com.blue_marble.bwise

import retrofit2.Response
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blue_marble.bwise.DataClasses.LoginRequest
import com.blue_marble.bwise.DataClasses.LoginResponse


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


    private fun tryLogin() {
        val usernameEditText = findViewById<EditText>(R.id.user_edit_text)

        val request = LoginRequest(username = usernameEditText.text.toString())

        RetrofitClient.apiService.login(request)
            .enqueue(object : BaseCallback<LoginResponse>(this) {
                override fun handleSuccess(response: Response<LoginResponse>) {
                    val intent = Intent(this@MainActivity, GroupsOverviewActivity::class.java)
                    intent.putExtra("username", response.body()?.username)
                    startActivity(intent)
                }
            })
    }
}