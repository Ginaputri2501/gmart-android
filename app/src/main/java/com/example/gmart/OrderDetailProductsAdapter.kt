package com.example.gmart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gmart.dataClass.OrderDetailProducts
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.ArrayList

class OrderDetailProductsAdapter(private val productList:ArrayList<OrderDetailProducts>) : RecyclerView.Adapter<OrderDetailProductsAdapter.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_detail_product_item,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]

        val _totalHarga = currentItem.prd_harga * currentItem.prd_jumlah
        val harga = NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.prd_harga)
        val totalHarga = NumberFormat.getNumberInstance(Locale.getDefault()).format(_totalHarga)

        holder.tvNama.text = currentItem.prd_nama
        holder.tvHarga.text = "Rp. $harga"
        holder.tvJumlah.text = " x " + currentItem.prd_jumlah.toString()
        holder.tvTotalHarga.text = "Total harga : Rp. $totalHarga"
        Picasso.get()
            .load("https://wifi-map.my.id" + currentItem.prd_foto_url)
            .placeholder(R.drawable.ic_gmart_logo)
            .into(holder.ivFoto)
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNama : TextView = itemView.findViewById(R.id.odpiNamaProduk)
        val tvHarga : TextView = itemView.findViewById(R.id.odpiHarga)
        val tvJumlah : TextView = itemView.findViewById(R.id.odpiJumlah)
        val tvTotalHarga : TextView = itemView.findViewById(R.id.odpiTotalHarga)
        val ivFoto : ImageView = itemView.findViewById(R.id.odpiImageView)
    }
}