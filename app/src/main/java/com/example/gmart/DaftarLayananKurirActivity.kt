package com.example.gmart

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gmart.adapter.DaftarLayananKurirAdapter
import com.example.gmart.dataClass.LayananKurir
import com.example.gmart.databinding.ActivityDaftarLayananKurirBinding
import com.example.gmart.`object`.Orders
import com.example.gmart.`object`.RQ
import org.json.JSONObject
import kotlin.math.cos

class DaftarLayananKurirActivity : AppCompatActivity() {
    lateinit var binding : ActivityDaftarLayananKurirBinding
    lateinit var kurirKey : String
    lateinit var destinationCityId : String
    lateinit var kurirInfo : JSONObject
    lateinit var recyclerView: RecyclerView
    lateinit var daftarLayananKurirAdapter: DaftarLayananKurirAdapter
    val daftarLayananKurir = ArrayList<LayananKurir>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarLayananKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kurirKey = intent.getStringExtra("kurir").toString()
        destinationCityId = intent.getStringExtra("idCity").toString()
        recyclerView = binding.layananKurirRV
        recyclerView.layoutManager = LinearLayoutManager(this)
        daftarLayananKurirAdapter = DaftarLayananKurirAdapter(daftarLayananKurir, kurirKey)
        recyclerView.adapter = daftarLayananKurirAdapter

        getHarga(destinationCityId, kurirKey){
            Log.d("harga", it.toString())

            val results = kurirInfo.getJSONObject("rajaongkir").getJSONArray("results").getJSONObject(0)
            val costs = results.getJSONArray("costs")
            for (i in 0 until costs.length()){
                val data = costs.getJSONObject(i)
                val nama = data.getString("service")
                val ongkir = data.getJSONArray("cost").getJSONObject(0).getString("value")
                val estimasi = data.getJSONArray("cost").getJSONObject(0).getString("etd")
                daftarLayananKurir.add(LayananKurir(nama, ongkir, estimasi))
            }
            daftarLayananKurirAdapter.list = daftarLayananKurir
            daftarLayananKurirAdapter.notifyDataSetChanged()
            daftarLayananKurirAdapter.onItemClick = {
                val intent = Intent()
                intent.putExtra("nama_kurir", kurirKey)
                intent.putExtra("nama_layanan", it[1])
                intent.putExtra("ongkir", it[2])
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            Log.d("daftar", daftarLayananKurir.toString())
        }
    }

    fun getHarga(destination: String, kurirKey : String, responseCallback : (String) -> Unit) {
        binding.progressBar.visibility = View.VISIBLE
        val url = "https://api.rajaongkir.com/starter/cost"

        val requestBody = "origin=485&destination=$destination&weight=1000&courier=$kurirKey"

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, null,
            Response.Listener<JSONObject> { response ->
                binding.progressBar.visibility = View.GONE
                kurirInfo = response
                responseCallback(response.toString())
            },
            Response.ErrorListener { error ->
                binding.progressBar.visibility = View.GONE
                // Handle erro
                error.printStackTrace()
            }) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charsets.UTF_8)
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["key"] = "4faeb1ba6d27c3552e27c3a30b8c7be6" // Ganti dengan API key Anda
                return headers
            }
        }

        RQ.getRQ().add(jsonObjectRequest)
    }
    private val REQUEST_CODE = 1
}