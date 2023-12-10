package com.example.gmart

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.gmart.dataClass.OrderDetailProducts
import com.example.gmart.databinding.ActivityOrderDetailBinding
import com.example.gmart.`object`.OrderDetail
import com.example.gmart.`object`.RQ
import com.example.gmart.uploadBuktiBayar.MyAPI
import com.example.gmart.uploadBuktiBayar.UploadRequestBody
import com.example.gmart.uploadBuktiBayar.UploadResponse
import com.example.gmart.uploadBuktiBayar.getFileName
import com.example.gmart.uploadBuktiBayar.snackbar
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    lateinit var binding : ActivityOrderDetailBinding

    private lateinit var orderDetailProductsAdapter : OrderDetailProductsAdapter
    private lateinit var recyclerView: RecyclerView
    private var orderDetailProductsArrayList = ArrayList<OrderDetailProducts>()

    private var selectedImage: Uri? = null

    var order_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = binding.orderDetailRV
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        orderDetailProductsAdapter = OrderDetailProductsAdapter(orderDetailProductsArrayList)
        recyclerView.adapter = orderDetailProductsAdapter

        OrderDetail.liveOrderDetail.observe(this, {
            val orderDetailArray : ArrayList<OrderDetailProducts> = OrderDetail.getOrderProductArrayList()
            orderDetailProductsAdapter.list = orderDetailArray
            orderDetailProductsAdapter.notifyDataSetChanged()

            val data = OrderDetail.orderDetail.getJSONObject(0)
            binding.tvOrderId.text = "Order ID : " + data.getString("ord_id")
            order_id = data.getString("ord_id")
            binding.tvOrderWaktu.text = "Waktu Pemesanan : " + data.getString("ord_waktu")
            binding.tvNamaPenerima.text = "Nama Penerima : " + data.getString("alamat_nama")
            binding.tvNomorTlp.text = "Nomor Telepon : " + data.getString("alamat_tlp")
            binding.tvAlamat.text = "Alamat : " + data.getString("alamat_alamat")
            binding.tvKurir.text = "Kurir : " + data.getString("ord_kurir")
            binding.tvNomorResi.text = "Nomor resi :" + data.getString("ord_resi")

//            when (binding.tvNomorResi.text) {
//                "Nomor resi :" -> {
//                    binding.tvStatusPengiriman.text = "Status Pengiriman : Belum Dikirim"
//                    binding.btnSelesaiPesanan.visibility = View.GONE
//                }
//                else -> {
//                    binding.tvStatusPengiriman.text = "Status Pengiriman : Sedang Dikirim"
//                    binding.btnSelesaiPesanan.visibility = View.VISIBLE
//                }
//            }

            when(data.getString("ord_metode_bayar")){
                "transfer" , "Transfer" -> {
                    when (data.getString("ord_status")){
                        "Menunggu Pembayaran" -> {
                            binding.tvOrderStatus.text = "Status : Menuggu Pembayaran"
                            binding.inputBuktiBayar.visibility = View.VISIBLE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Belum Dikirim"
                            binding.btnSelesaiPesanan.visibility = View.GONE
                        }
                        "Menunggu Konfirmasi Pembayaran" -> {
                            binding.tvOrderStatus.text = "Status : Menunggu Konfirmasi Pembayaran"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Belum Dikirim"
                            binding.btnSelesaiPesanan.visibility = View.GONE
                        }
                        "Menunggu Dikirim" -> {
                            binding.tvOrderStatus.text = "Status : Pembayaran Berhasil"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Belum Dikirim"
                            binding.btnSelesaiPesanan.visibility = View.GONE
                        }
                        "Sedang Dikirim" -> {
                            binding.tvOrderStatus.text = "Status : Pembayaran Berhasil"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Sedang Dikirim"
                            binding.btnSelesaiPesanan.visibility = View.VISIBLE
                        }
                        "Selesai" -> {
                            binding.tvOrderStatus.text = "Status : Pembayaran Berhasil"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Pesanan telah tiba"
                            binding.btnSelesaiPesanan.visibility = View.GONE
                        }
                    }
                }
                "COD","cod" -> {
                    when (data.getString("ord_status")){
                        "Menunggu Dikirim" -> {
                            binding.tvOrderStatus.text = "Status : Pembayaran COD"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Belum Dikirim"
                            binding.btnSelesaiPesanan.visibility = View.GONE
                        }
                        "Sedang Dikirim" -> {
                            binding.tvOrderStatus.text = "Status : Pembayaran COD"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Sedang Dikirim"
                            binding.btnSelesaiPesanan.visibility = View.VISIBLE
                        }
                        "Selesai" -> {
                            binding.tvOrderStatus.text = "Status : Pembayaran COD"
                            binding.inputBuktiBayar.visibility = View.GONE
                            binding.tvStatusPengiriman.text = "Status Pengiriman : Pesanan telah tiba"
                            binding.btnSelesaiPesanan.visibility = View.GONE
                        }
                    }
                }
            }

            if (data.getString("ord_status") == "Selesai"){
                binding.btnSelesaiPesanan.visibility = View.GONE
            }

            var total = data.getString("ongkir").toString().toInt()
            binding.tvOngkir.text = "Ongkos kirim : Rp. $total"
            for (i in 0 until orderDetailArray.size){
                val data = orderDetailArray[i]
                val _total = data.prd_harga * data.prd_jumlah
                total += _total
                if (i == (orderDetailArray.size - 1) ){
                    val __total = NumberFormat.getNumberInstance(Locale.getDefault()).format(total)
                    binding.tvTotalPembayaran.text = "Subtotal : Rp. $__total"
                }
            }
        })

        binding.ivBuktiBayar.setOnClickListener {
            openImagePicker()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
        }
        binding.btnSelesaiPesanan.setOnClickListener {
            selesaikanPesanan(order_id)
        }










//        binding.selectImageButton.setOnClickListener {
//            openImagePicker()
//        }


    }
    fun selesaikanPesanan(id:String){
        val url="https://wifi-map.my.id/api/gmart_selesaikan_pesanan.php?id=$id"
        val url2="https://webhook.site/75736aea-5ec0-4d1f-90cc-67fa91b32602?id=$id"
        val request=StringRequest(Request.Method.GET,url,
            {response ->
                if(response=="berhasil") {
                    OrderDetail.getOrderDetailFromDB(order_id)
                    Toast.makeText(this, "Pesanan Telah diselesaikan", Toast.LENGTH_LONG).show()
                }
             },
            {error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            })
        RQ.getRQ().add(request)
    }

    private fun uploadImage() {
        val data = OrderDetail.orderDetail.getJSONObject(0)
        val ord_id = data.getString("ord_id")
        val params = JSONObject().put("id", ord_id)

        if (selectedImage == null){
            binding.root.snackbar("Select an image first")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressBar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        MyAPI().uploadImage(
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                body
            )
        ).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.d("upload", "error")
                binding.root.snackbar(t.message!!)
                binding.progressBar.progress = 0
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                Log.d("upload", response.body().toString())
                response.body()?.let {
                    binding.root.snackbar(it.message)
                    binding.progressBar.progress = 100
                    params.put("url", response.body()!!.image)
                    insertToDB(params)
                }
                Log.d("upload", params.toString())

            }
        })

    }

    fun insertToDB (params : JSONObject) {

        val url = "https://wifi-map.my.id/api/gmart_add_bukti_bayar_db.php"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,url,params,
                {response ->
                    val status = response.getString("status")
                    if (status == "berhasil"){
                        Toast.makeText(this, "Upload berhasil", Toast.LENGTH_LONG).show()
                        OrderDetail.getOrderDetailFromDB(order_id)
                    } else {
                        Toast.makeText(this, "Upload berhasil, DB Gagal", Toast.LENGTH_LONG).show()
                    }
                },
                {error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            })
        RQ.getRQ().add(jsonObjectRequest)
    }

    fun openImagePicker(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpg","image/png","image/jpeg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when (requestCode){
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data?.data
                    binding.ivBuktiBayar.setImageURI(selectedImage)
                }
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.progressBar.progress = percentage
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}
