package com.discountmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.discountmarket.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StoreDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_dashboard)

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val generateBtn = findViewById<Button>(R.id.generateDiscountBtn)
        val marketplaceBtn = findViewById<Button>(R.id.marketplaceBtn)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val recycler = findViewById<RecyclerView>(R.id.discountsRecycler)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                welcomeText.text = "خوش آمدید، ${doc.getString("name")} 🏪"
            }

        recycler.layoutManager = LinearLayoutManager(this)

        FirebaseFirestore.getInstance()
            .collection("discounts")
            .whereEqualTo("storeId", userId)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val list = snapshots.documents.map { doc ->
                        "${doc.getString("transactionId")} | ${doc.getLong("amount")} تومان | ${doc.getString("status")}"
                    }
                    recycler.adapter = SimpleAdapter(list)
                }
            }

        generateBtn.setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        marketplaceBtn.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

class SimpleAdapter(private val items: List<String>) : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val tv = TextView(parent.context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(32, 16, 32, 16)
            textSize = 14f
            setTextColor(0xFF333333.toInt())
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    override fun getItemCount() = items.size
}
