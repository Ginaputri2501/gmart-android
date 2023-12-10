package com.example.gmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gmart.databinding.ActivityDaftarKurirBinding

class DaftarKurirActivity : AppCompatActivity() {
    lateinit var binding : ActivityDaftarKurirBinding
    lateinit var selectedCityId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedCityId = intent.getStringExtra("idCity").toString()
        val intent = Intent(this, DaftarLayananKurirActivity::class.java)
        intent.putExtra("idCity", selectedCityId)

        binding.btnJNE.setOnClickListener{
            intent.putExtra("kurir","jne")
            startActivity(intent)
        }
        binding.btnPos.setOnClickListener {
            intent.putExtra("kurir","pos")
            startActivity(intent)
        }
        binding.btnTIKI.setOnClickListener {
            intent.putExtra("kurir","tiki")
            startActivity(intent)
        }
    }
}