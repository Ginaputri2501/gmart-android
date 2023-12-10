package com.example.gmart

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.gmart.databinding.ActivityLoginBinding
import com.example.gmart.`object`.LoginSP
import com.example.gmart.`object`.RQ
import org.json.JSONException

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var SPeditor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.itUsername
        val password = binding.itPassword

        val sharedPref = LoginSP.getSP()
        SPeditor = sharedPref.edit()

        binding.btnLogin.setOnClickListener {
            login(username.text.toString(), password.text.toString())
        }
    }

    private fun login (username : String, password : String) {

        validateUser(username, password) {
            isValid ->
            if (isValid) {
                Log.d("pzn", "login berhasil")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Log.d("pzn", "login gagal")
                Toast.makeText(this,"Username atau Password salah",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateUser (username : String, password : String,callback: (Boolean) -> Unit) {

        val url = "https://wifi-map.my.id/api/gmart_validate_user.php?username=$username&password=$password"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getJSONObject(0).getString("status")

                    when (status) {
                        "berhasil" -> {
                            SPeditor.putBoolean("status", true)
                            SPeditor.putString("userId", response.getJSONObject(1).getString("us_id"))
                            SPeditor.apply()
                            callback(true)
                        }
                        "user salah" , "password salah" -> {
                            callback(false)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())

            })

        RQ.getRQ().add(jsonArrayRequest)


    }


}