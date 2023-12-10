package com.example.gmart

import android.R
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gmart.databinding.ActivityOrderConfirmationBinding
import com.example.gmart.`object`.KeranjangItems
import com.example.gmart.`object`.LoggedInUser
import com.example.gmart.`object`.RQ
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class OrderConfirmationActivity : AppCompatActivity() {
    lateinit var binding : ActivityOrderConfirmationBinding
    lateinit var selectedCityId : String
    val provinceNames = ArrayList<String>()
    val cityNames = ArrayList<String>()
    val cityIds = ArrayList<String>()
    val REQUEST_CODE = 1
    val REQUEST_CODE_POS = 2
    val REQUEST_CODE_TIKI = 3
    var selectedKurir = String()
    var selectedLayananKurir = String()
    var selectedKurirOngkir = String()
    var selectedProvinsi = String()
    var selectedKabupaten = String()
    var totalHargaProduk = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Populate your provinceNames ArrayList (sample data)

        totalHargaProduk = intent.getStringExtra("totalHargaProduk").toString()
        binding.tvTotalHargaProduk.text = "Harga produk : Rp. $totalHargaProduk"
        binding.tvTotalHargaPlusOngkir.text = "Total : Rp. $totalHargaProduk"

        val spinnerProvinsi: Spinner = binding.spinnerProvinsi
        val spinnerKabupaten: Spinner = binding.spinnerKabupaten
        val adapterProvinsi = ArrayAdapter(this, R.layout.simple_spinner_item, provinceNames)
        adapterProvinsi.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerProvinsi.adapter = adapterProvinsi
        val adapterKabupaten = ArrayAdapter(this, R.layout.simple_spinner_item, cityNames)
        adapterKabupaten.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerKabupaten.adapter = adapterKabupaten

        getAllProvince {
            when(it){
                true ->  {
                    adapterProvinsi.notifyDataSetChanged()
                }
                false -> {
                    Toast.makeText(this, "Gagal mengambil data provinsi", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Menangani perubahan pilihan pada spinner
        spinnerProvinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                cityNames.clear()
                selectedProvinsi = provinceNames[position]
                val idProvinsi = position + 1
                getCity(idProvinsi.toString()){
                    when(it){
                        true ->  {
                            adapterKabupaten.notifyDataSetChanged()
                            clearSelectedKurir()
                            Log.d("spinners",cityNames.toString())
                        }
                        false -> {
                            showToast("Gagal mengambil data kabupaten")
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("spinner","noitemselected")
            }
        }

        spinnerKabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCityId = cityIds[position]
                selectedKabupaten = cityNames[position]
                clearSelectedKurir()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("spinner","noitemselected")
            }
        }

        binding.btnJNE.setOnClickListener {
            val intent = Intent(this, DaftarLayananKurirActivity::class.java)
            if (!selectedCityId.isNullOrBlank()){
                intent.putExtra("idCity", selectedCityId)
                intent.putExtra("kurir","jne")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                showToast("Kota tujuan belum di pilih")
            }
        }

        binding.btnPos.setOnClickListener {
            val intent = Intent(this, DaftarLayananKurirActivity::class.java)
            if (!selectedCityId.isNullOrBlank()){
                intent.putExtra("idCity", selectedCityId)
                intent.putExtra("kurir","pos")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                showToast("Kota tujuan belum di pilih")
            }
        }

        binding.btnTIKI.setOnClickListener {
            val intent = Intent(this, DaftarLayananKurirActivity::class.java)
            if (!selectedCityId.isNullOrBlank()){
                intent.putExtra("idCity", selectedCityId)
                intent.putExtra("kurir","tiki")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                showToast("Kota tujuan belum di pilih")
            }
        }

        binding.btnGmartKurir.setOnClickListener {
            if (selectedCityId == "188"){
                selectedKurir = "gmart"
                selectedLayananKurir = "COD"
                selectedKurirOngkir = "15000"
                clearSelectedKurir()
                displaySelecterKurir(selectedKurir, selectedLayananKurir, selectedKurirOngkir)
                binding.tvHargaOngkir.text = "Ongkos kirim : Rp. $selectedKurirOngkir"
                val hargaProdukPlusOngkir = totalHargaProduk.toInt() + selectedKurirOngkir.toInt()
                binding.tvTotalHargaPlusOngkir.text = "Total : Rp. $hargaProdukPlusOngkir"
            } else {
                showToast("Hanya berlaku di kota Tomohon")
            }
        }

        binding.btnKonfirmasiPesanan.setOnClickListener {
            if (binding.inputNama.text contentEquals  "" ||
                binding.inputAlamat.text contentEquals  "" ||
                binding.inputTelepon.text contentEquals  "" ||
                selectedKurir.isNullOrBlank()
            ){
                Toast.makeText(this, "Data Tidak Lengkap", Toast.LENGTH_LONG).show()
            }else {
                val metodePembayaran = when (selectedKurir) {
                    "gmart" -> "cod"
                    else -> "transfer"
                }
                val orderStatus = when (selectedKurir) {
                    "gmart" -> 3
                    else -> 1
                }
                binding.progressBar.visibility = View.VISIBLE
                val radioId = binding.radioGroup.checkedRadioButtonId
                val radio : RadioButton = findViewById(radioId)
                addOrderToDB(metodePembayaran, orderStatus)
            }
        }
    }

//    dijalankan saat kembali dari pilihan layanan kurir
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            // Hasil dari Activity B
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    selectedKurir = data.getStringExtra("nama_kurir").toString()
                    selectedLayananKurir = data.getStringExtra("nama_layanan").toString()
                    selectedKurirOngkir = data.getStringExtra("ongkir").toString()
                    clearSelectedKurir()
                    displaySelecterKurir(selectedKurir, selectedLayananKurir, selectedKurirOngkir)
                    binding.tvHargaOngkir.text = "Ongkos kirim : Rp. $selectedKurirOngkir"
                    val hargaProdukPlusOngkir = totalHargaProduk.toInt() + selectedKurirOngkir.toInt()
                    binding.tvTotalHargaPlusOngkir.text = "Total : Rp. $hargaProdukPlusOngkir"
                }
            } else {
                // Penaccanan jika Activity B tidak mengirimkan hasil yang benar
            }
        }
    }

    private fun displaySelecterKurir(kurirKey: String, layananKurir: String, kurirOngkir: String) {
        when (kurirKey){
            "jne" -> {
                binding.tvJne.text = "JNE - $layananKurir Rp. $kurirOngkir"
                binding.selectedJne.visibility = View.VISIBLE
            }
            "pos" -> {
                binding.tvPos.text = "Pos - $layananKurir Rp. $kurirOngkir"
                binding.selectedPos.visibility = View.VISIBLE
            }
            "tiki" -> {
                binding.tvTiki.text = "TIKI - $layananKurir Rp. $kurirOngkir"
                binding.selectedTiki.visibility = View.VISIBLE
            }
            "gmart" -> {
                binding.tvGmartKurir.text = "Gmart - $layananKurir Rp. $kurirOngkir"
                binding.selectedGmartKurir.visibility = View.VISIBLE
            }
        }
    }

    private fun clearSelectedKurir(){
        binding.tvJne.text = "JNE"
        binding.tvPos.text = "Pos"
        binding.tvTiki.text = "TIKI"
        binding.tvGmartKurir.text = "Gmart COD"
        binding.selectedJne.visibility = View.GONE
        binding.selectedPos.visibility = View.GONE
        binding.selectedTiki.visibility = View.GONE
        binding.selectedGmartKurir.visibility = View.GONE
        binding.tvHargaOngkir.text = "Ongkos kirim : "
        binding.tvTotalHargaPlusOngkir.text = "Total : Rp. $totalHargaProduk"
    }

    private fun getAllProvince(callback: (Boolean) -> Unit) {
        binding.progressBar.visibility = View.VISIBLE
        val url = "https://api.rajaongkir.com/starter/province"
        val apiKey = "4faeb1ba6d27c3552e27c3a30b8c7be6"
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener<JSONObject> { response ->
                binding.progressBar.visibility = View.GONE
                val status = response.getJSONObject("rajaongkir").getJSONObject("status").getString("code")
                if (status.toInt() == 200){
                    val resultsArray = response.getJSONObject("rajaongkir").getJSONArray("results")
                    // Mengambil nama provinsi dari setiap objek dalam array results
                    for (i in 0 until resultsArray.length()) {
                        val provinceObject = resultsArray.getJSONObject(i)
                        val provinceName = provinceObject.getString("province")
                        provinceNames.add(provinceName)
                        Log.d("spinner json", provinceNames.toString())
                    }
                    callback(true)
                } else {
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                binding.progressBar.visibility = View.GONE
                // Handle error jika terjadi
                callback(false)
                Log.e("VolleyError", "Error: ${error.message}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["key"] = apiKey
                return headers
            }
        }
        RQ.getRQ().add(jsonObjectRequest)
    }

    private fun getCity(idProvinsi : String, callback: (Boolean) -> Unit) {
        binding.progressBar.visibility = View.VISIBLE
        val url = "https://api.rajaongkir.com/starter/city?province=$idProvinsi"
        val apiKey = "4faeb1ba6d27c3552e27c3a30b8c7be6"
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener<JSONObject> { response ->
                binding.progressBar.visibility = View.GONE
                Log.d("spinners json url", url)
                Log.d("spinners json respon", response.toString())
                val status = response.getJSONObject("rajaongkir").getJSONObject("status").getString("code")
                if (status.toInt() == 200){
                    val resultsArray = response.getJSONObject("rajaongkir").getJSONArray("results")
                    // Mengambil nama city dari setiap objek dalam array results
                    for (i in 0 until resultsArray.length()) {
                        val cityObject = resultsArray.getJSONObject(i)
                        val cityName = cityObject.getString("city_name")
                        val cityId = cityObject.getString("city_id")
                        cityNames.add(cityName)
                        cityIds.add(cityId)
                        Log.d("spinners json", cityNames.toString())
                    }
                    callback(true)
                } else {
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                binding.progressBar.visibility = View.GONE
                // Handle error jika terjadi
                callback(false)
                Log.e("VolleyError", "Error: ${error.message}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["key"] = apiKey
                return headers
            }
        }
        RQ.getRQ().add(jsonObjectRequest)
    }

    fun getHarga(destination: String, onResponseListener: (String) -> Unit) {
        val url = "https://api.rajaongkir.com/starter/cost"

        val requestBody = "origin=485&destination=$destination&weight=1000&courier=jne"

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, null,
            Response.Listener<JSONObject> { response ->
                onResponseListener(response.toString())
            },
            Response.ErrorListener { error ->
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

    fun addOrderToDB(metodePembayaran: String, orderStatus: Int){
        val alamatUrl = "https://wifi-map.my.id/api/gmart_add_alamat_pengiriman.php"
//        val hapusUrl = "https://gmartxxx.requestcatcher.com/test"
        val orderUrl = "https://wifi-map.my.id/api/gmart_add_order.php"
        val hapusUrl = "https://wifi-map.my.id/api/gmart_delete_keranjang_item.php"

        val orderParams = JSONArray()
        val alamatParams = JSONObject()
        val hapusParams = JSONObject()

        val orderId = Random.nextInt(900000) + 100000
        val alamatId = Random.nextInt(900000) + 100000
        for (i in 0 until KeranjangItems._kerItems.length()){
            val list = KeranjangItems._kerItems.getJSONObject(i)
            val item = JSONObject()

            item.put("ord_id", orderId)
            item.put("prd_id", list.get("ker_prd_id"))
            item.put("user_id", LoggedInUser.loggedInUser.id)
            item.put("ord_jumlah", list.get("ker_prd_jumlah"))
            item.put("ord_status", orderStatus)
            item.put("ord_alamat_id", alamatId)
            item.put("metode_pembayaran", metodePembayaran)
            item.put("kurir", " $selectedKurir $selectedLayananKurir")
            item.put("ongkir", selectedKurirOngkir)
            orderParams.put(item)
        }

        alamatParams.put("id",alamatId)
        alamatParams.put("nama",binding.inputNama.text)
        alamatParams.put("alamat", "$selectedProvinsi, $selectedKabupaten, " + binding.inputAlamat.text)
        alamatParams.put("telepon", binding.inputTelepon.text)

        hapusParams.put("user_id", LoggedInUser.loggedInUser.id)

        Log.d("pzn order",orderParams.toString())
        Log.d("pzn order",alamatParams.toString())

        val hapusKeranjangRequest = JsonObjectRequest(
            Request.Method.POST, hapusUrl, hapusParams,
            { response ->
                val status = response.getString("status")
                Log.d("pzn hapus keranjang",status)
                if (status == "berhasil"){
                    binding.progressBar.visibility = View.GONE
                    KeranjangItems.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragmentToStart", "ProductsFragment")
                    startActivity(intent)
                }
                else {
                }
            },
            { error ->
                Log.d("pzn hapus keranjang err",error.toString())
            })

        val orderRequest = JsonArrayRequest(
            Request.Method.POST, orderUrl, orderParams,
            { response ->
                val _status : JSONObject = response.getJSONObject(0)
                val status = _status.getString("status")
                Log.d("pzn add order",status)
                if (status == "berhasil"){
                    RQ.getRQ().add(hapusKeranjangRequest)
                }
                else {
                }
            },
            { error ->
                Log.d("pzn add order err",error.toString())
            })

        val alamatRequest = JsonObjectRequest(
                Request.Method.POST, alamatUrl, alamatParams,
            { response ->
                val status = response.getString("status")
                Log.d("pzn alamat",status)
                if (status == "berhasil"){
                    RQ.getRQ().add(orderRequest)
                }
                else {
                }
            },
            { error ->
                Log.d("pzn alamat err",error.toString())
            })

        RQ.getRQ().add(alamatRequest)

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}