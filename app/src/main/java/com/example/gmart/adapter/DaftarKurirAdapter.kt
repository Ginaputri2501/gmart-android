package com.example.gmart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gmart.dataClass.Order
import com.example.gmart.R
import kotlin.collections.ArrayList

class DaftarKurirAdapter(private val ordersList:ArrayList<Order>) : RecyclerView.Adapter<DaftarKurirAdapter.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = ordersList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_item,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]
        val id = "Order ID : " + currentItem.ordId
        val waktu = currentItem.ordWaktu
        val status = "Status : " + currentItem.ordStatus

        holder.tvId.text = id
        holder.tvWaktu.text = waktu
        holder.tvStatus.text = status
        holder.buttonDetail.setOnClickListener {
            onItemClick?.invoke(arrayOf(currentItem.ordId)
            )
        }
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvId : TextView = itemView.findViewById(R.id.tvOrderItemId)
        val tvWaktu : TextView = itemView.findViewById(R.id.tvOrderItemWaktu)
        val tvStatus : TextView = itemView.findViewById(R.id.tvOrderItemStatus)
        val buttonDetail : Button = itemView.findViewById(R.id.btnOrderItemLihatDetail)
    }
}