package com.example.gmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gmart.databinding.ActivityRegisterBinding
import com.example.gmart.`object`.RQ
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = "https://wifi-map.my.id/api/gmart_register_user.php"
//        val url = "https://gmartxxx.requestcatcher.com"

        val params = JSONObject()

        val loginIntent = Intent(this, LoginActivity::class.java)



        binding.btnDaftar.setOnClickListener {
            params.put("username", binding.itUsername.text.toString())
            params.put("nama", binding.itNama.text.toString())
            params.put("tlp", binding.itTlp.text.toString())
            params.put("sandi", binding.itSandi.text.toString())
            params.put("alamat", binding.itAlamat.text.toString())
            Log.d("pzn register","button clik")
            Log.d("pzn register",params.toString())


            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, params,
                { response ->
                    val status = response.getString("status")
                    Log.d("pzn register",status)
                    if (status == "berhasil"){
                        Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_LONG).show()
                        startActivity(loginIntent)
                    } else {
                        Toast.makeText(this, "Gagal, $status", Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Log.d("pzn register err",error.toString())

                    Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_LONG).show()
                })

            RQ.getRQ().add(jsonObjectRequest)
        }

    }
}