package com.example.gmart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gmart.dataClass.Order
import com.example.gmart.R
import com.example.gmart.dataClass.LayananKurir
import kotlin.collections.ArrayList

class DaftarLayananKurirAdapter(private val ordersList:ArrayList<LayananKurir>, _namaKurir : String) : RecyclerView.Adapter<DaftarLayananKurirAdapter.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = ordersList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini
    var namaKurir = _namaKurir

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layanan_kurir,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentItem = list[position]
        var nama_kurir = String()
        var estimasi = String()
        when(namaKurir){
            "jne" -> {
                nama_kurir = "JNE - "
                estimasi = currentItem.estimasi + " Hari"
            }
            "pos" -> {
                nama_kurir = "POS - "
                estimasi = currentItem.estimasi
            }
            "tiki" -> {
                nama_kurir = "TIKI - "
                estimasi = currentItem.estimasi + " Hari"
            }
        }
        holder.tvNamaKurir.text = nama_kurir
        holder.tvLayananKurir.text = currentItem.nama
        holder.tvHarga.text = "Rp. " + currentItem.ongkir
        holder.tvEstimasi.text = estimasi
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(arrayOf(nama_kurir,currentItem.nama,currentItem.ongkir)
            )
        }
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNamaKurir : TextView = itemView.findViewById(R.id.tvLayananKurirNamaKurir)
        val tvLayananKurir : TextView = itemView.findViewById(R.id.tvLayananKurirNamaLayanan)
        val tvEstimasi : TextView = itemView.findViewById(R.id.tvLayananKurirEstimasi)
        val tvHarga : TextView = itemView.findViewById(R.id.tvLayananKurirHarga)
    }
}