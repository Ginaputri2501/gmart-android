package com.example.gmart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gmart.dataClass.Product
import com.example.gmart.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.ArrayList

class ProductsAdapter(private val productsList:ArrayList<Product>) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productsList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = productsList[position]
        val id = currentItem.id.toString()
        val nama = currentItem.nama.toString()
        val urlFoto = currentItem.urlFoto.toString()
        val harga = NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.harga)
        val desk = currentItem.desk.toString()


        holder.itemView.setOnClickListener {
            onItemClick?.invoke(arrayOf(id,nama,harga,urlFoto,desk)
            )
        }
        holder.productJudul.text = currentItem.nama
        holder.productHarga.text = "Rp. $harga"
        Picasso.get()
            .load("https://wifi-map.my.id${currentItem.urlFoto}")
            .placeholder(R.drawable.ic_gmart_logo)
            .into(holder.productImage)
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val productImage : ImageView = itemView.findViewById(R.id.productItemImageView)
        val productJudul : TextView = itemView.findViewById(R.id.productItemTextJudul)
        val productHarga : TextView = itemView.findViewById(R.id.productItemTextHarga)
    }
}