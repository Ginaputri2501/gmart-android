package com.example.gmart.`object`

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gmart.dataClass.KeranjangItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object KeranjangItems {

    var _kerItems = JSONArray()

    private val _liveKerItems = MutableLiveData<JSONArray>().apply {
        _kerItems
    }
    val liveKerItems: LiveData<JSONArray> = _liveKerItems

    fun getItemsFromDB(usId : String) {
        val requestQueue: RequestQueue = RQ.getRQ()
        val url = "https://wifi-map.my.id/api/gmart_get_cart.php?user_id=$usId"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ambil data pertama dari JSON Array
                    _kerItems = response
                    _liveKerItems.postValue(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())
            })

        requestQueue.add(jsonArrayRequest)
    }

    fun plusButtonPressed (id : Int, jumlah : Int){
        val params = JSONObject()
        val url = "https://wifi-map.my.id/api/gmart_change_cart_item.php"
        val requestQueue: RequestQueue = RQ.getRQ()

        params.put("action","plus")
        params.put("cart_id",id)
        params.put("jumlah",jumlah)


        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                val status = response.getString("status")
                Log.d("pzn add keranjang item",status)
                Log.d("pzn add keranjang item",params.toString())

            },
            { error ->
                Log.d("pzn add keranjang item err",error.toString())
                Log.d("pzn add keranjang item err",params.toString())

            })

        requestQueue.add(jsonObjectRequest)
    }

    fun minusButtonPressed (id : Int, jumlah : Int){
        val params = JSONObject()
        val url = "https://wifi-map.my.id/api/gmart_change_cart_item.php"
        val requestQueue: RequestQueue = RQ.getRQ()

        params.put("action","minus")
        params.put("cart_id",id)
        params.put("jumlah",jumlah)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                val status = response.getString("status")
                Log.d("pzn min keranjang item",status)
                Log.d("pzn min keranjang item",params.toString())

            },
            { error ->
                Log.d("pzn min keranjang item err",error.toString())
                Log.d("pzn min keranjang item err",params.toString())

            })

        requestQueue.add(jsonObjectRequest)

    }

    fun addToDB(prd_id : Int, user_id : Int, url_foto : String, context : Context){
        val params = JSONObject()
        val url = "https://wifi-map.my.id/api/gmart_add_cart_item.php"
        val requestQueue: RequestQueue = RQ.getRQ()

        params.put("user_id",user_id)
        params.put("prd_id", prd_id)
        params.put("url_foto",url_foto)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                val status = response.getString("status")
                Log.d("pzn add keranjang item",status)
                if (status == "berhasil"){
                    Toast.makeText(context, "Produk berhasi ditambahkan ke keranjang", Toast.LENGTH_LONG).show()
                    getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                } else {
                    Toast.makeText(context, "Gagal, $status", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.d("pzn add keranjang item err",error.toString())
                Toast.makeText(context, "eroor", Toast.LENGTH_LONG).show()
            })

        requestQueue.add(jsonObjectRequest)
    }

    fun getkeranjangItemsArrayList() : ArrayList<KeranjangItem> {
        var list = ArrayList<KeranjangItem>()
        Log.d("pzn ker", _liveKerItems.toString())
        Log.d("pzn ker", liveKerItems.toString())
        try {
            for( i in 0 until _kerItems.length()){
                val item = _kerItems.getJSONObject(i)
                val id = item.getString("ker_id").toInt()
                val prdId = item.getString("ker_prd_id").toInt()
                val nama = item.getString("ker_prd_nama")
                val harga = item.getString("ker_prd_harga").toInt()
                val urlFoto = item.getString("ker_url_foto")
                val prdJumlah = item.getString("ker_prd_jumlah").toInt()
                list.add(KeranjangItem(id, prdId, nama, harga, urlFoto, prdJumlah))
//                Log.d("pzn ker try", list.toString())

            }
        }
        catch (e : Exception){
            Log.d("pzn ker catch", e.toString())

        }

        return list
    }

    fun hapusKeranjangItems(){
        _kerItems = JSONArray()
        _liveKerItems.postValue(_kerItems)
    }

}