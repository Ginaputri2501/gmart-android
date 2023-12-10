package com.example.gmart.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gmart.dataClass.KeranjangItem
import com.example.gmart.R
import com.example.gmart.`object`.KeranjangItems
import com.example.gmart.`object`.LoggedInUser
import com.example.gmart.`object`.RQ
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.ArrayList

class KeranjangAdapter(private val keranjangList:ArrayList<KeranjangItem>) : RecyclerView.Adapter<KeranjangAdapter.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null
    var total = 0

    public var list = keranjangList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.keranjang_item,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]

        val cartId = currentItem.id
        val prdId = currentItem.prdId
        val nama = if (currentItem.prdNama.length > 24){
                            currentItem.prdNama.substring(0, 24) + "..."
                        } else {
                            currentItem.prdNama
                        }
        val urlFoto = currentItem.prdFoto.toString()
        val _harga = currentItem.prdHarga
        total += _harga
        val harga = NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.prdHarga)
        val jumlah = currentItem.prdJumlah
        val totalHarga = NumberFormat.getNumberInstance(Locale.getDefault()).format((_harga * jumlah))

        holder.cartPlusButton.setOnClickListener {
            val params = JSONObject()
            val url = "https://wifi-map.my.id/api/gmart_change_cart_item.php"
            val requestQueue: RequestQueue = RQ.getRQ()
            params.put("action","plus")
            params.put("cart_id",cartId)
            params.put("jumlah",jumlah)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, params,
                { response ->
                    val status = response.getString("status")
                    Log.d("pzn add keranjang item",status)
                    Log.d("pzn add keranjang item",params.toString())

                    KeranjangItems.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                    list = KeranjangItems.getkeranjangItemsArrayList()
                    notifyItemChanged(position)
                },
                { error ->
                    Log.d("pzn add keranjang item err",error.toString())
                    Log.d("pzn add keranjang item err",params.toString())
                })
            requestQueue.add(jsonObjectRequest)
        }
        holder.cartMinusButton.setOnClickListener {
            val params = JSONObject()
            val url = "https://wifi-map.my.id/api/gmart_change_cart_item.php"
            val requestQueue: RequestQueue = RQ.getRQ()
            params.put("action","minus")
            params.put("cart_id",cartId)
            params.put("jumlah",jumlah)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, params,
                { response ->
                    val status = response.getString("status")
                    Log.d("pzn min keranjang item",status)
                    Log.d("pzn min keranjang item",params.toString())
                    KeranjangItems.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                    list = KeranjangItems.getkeranjangItemsArrayList()
                    notifyItemChanged(position)
                },
                { error ->
                    Log.d("pzn min keranjang item err",error.toString())
                    Log.d("pzn min keranjang item err",params.toString())
                })
            requestQueue.add(jsonObjectRequest)
        }

        holder.cartKuantitas.text = jumlah.toString()
        holder.cartNama.text = nama
        holder.cartHarga.text = "Rp. $harga"
        holder.cartTotalHarga.text = "Rp. $totalHarga"
        Picasso.get()
            .load("https://wifi-map.my.id$urlFoto")
            .placeholder(R.drawable.ic_gmart_logo)
            .into(holder.cartImage)
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val cartImage : ImageView = itemView.findViewById(R.id.cartItemImageView)
        val cartNama : TextView = itemView.findViewById(R.id.cartItemNamaProduk)
        val cartHarga : TextView = itemView.findViewById(R.id.cartItemHarga)
        val cartKuantitas : TextView = itemView.findViewById(R.id.cartItemKuantitas)
//        val cartPlusButton : ImageView = itemView.findViewById(R.id.cartItemTambahJumlahBtn)
//        val cartMinusButton : ImageView = itemView.findViewById(R.id.cartItemKurangiJumlahBtn)
        val cartTotalHarga : TextView = itemView.findViewById(R.id.cartItemTotalHarga)


        val cartPlusButton : Button = itemView.findViewById(R.id.cartItemTambahJumlahBtn)
        val cartMinusButton : Button = itemView.findViewById(R.id.cartItemKurangiJumlahBtn)

    }

}

