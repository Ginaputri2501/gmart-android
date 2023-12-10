package com.example.gmart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gmart.databinding.ActivityProductDetailBinding
import com.example.gmart.`object`.KeranjangItems
import com.example.gmart.`object`.LoggedInUser
import com.example.gmart.`object`.LoginSP
import com.squareup.picasso.Picasso

class ProductDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prdId = intent.getStringArrayExtra("prdInfo")
        val url = prdId?.get(3)

        Picasso.get()
            .load("https://wifi-map.my.id$url")
            .placeholder(R.drawable.ic_gmart_logo)
            .into(binding.ivProduct)
        binding.tvNama.text = prdId?.get(1)
        binding.tvHarga.text = prdId?.get(2)
        binding.tvDesk.text = prdId?.get(4)
        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }

        binding.btnTambahKeranjang.setOnClickListener {
            if (LoginSP.getLoginStatus()){
                if (prdId !== null){
                    val _prdId = prdId[0].toInt()
                    val urlFoto = prdId[3]
                    val usId = LoggedInUser.loggedInUser.id!!
                    KeranjangItems.addToDB(_prdId, usId,urlFoto,this )
                }
            } else {
                Toast.makeText(this, "Anda Belum Login",Toast.LENGTH_LONG).show()
            }
        }
    }
}