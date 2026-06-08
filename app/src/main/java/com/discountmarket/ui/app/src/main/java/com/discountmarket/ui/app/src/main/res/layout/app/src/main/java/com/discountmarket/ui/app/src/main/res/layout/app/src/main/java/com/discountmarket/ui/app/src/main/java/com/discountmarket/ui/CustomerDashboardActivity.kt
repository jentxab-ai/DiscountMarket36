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

class CustomerDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val walletBalanceText = findViewById<TextView>(R.id.walletBalanceText)
        val uniqueIdText = findViewById<TextView>(R.id.uniqueIdText)
        val marketplaceBtn = findViewById<Button>(R.id.marketplaceBtn)
        val walletBtn = findViewById<Button>(R.id.walletBtn)
        val reportBtn = findViewById<Button>(R.id.reportBtn)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val recycler = findViewById<RecyclerView>(R.id.myDiscountsRecycler)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(userId)
            .addSnapshotListener { doc, _ ->
                if (doc != null) {
                    welcomeText.text = "${doc.getString("name")} 👤"
                    walletBalanceText.text = "کیف پول: ${doc.getLong("walletBalance") ?: 0} تومان"
                    uniqueIdText.text = "شناسه: ${doc.getString("uniqueId")}"
                }
            }

        recycler.layoutManager = LinearLayoutManager(this)

        firestore.collection("discounts")
            .whereEqualTo("customerId", userId)
            .whereEqualTo("status", "ACTIVE")
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val list = snapshots.documents.map { doc ->
                        "${doc.getString("transactionId")} | ${doc.getLong("amount")} تومان | ${doc.getString("storeName")}"
                    }
                    recycler.adapter = SimpleDiscountAdapter(list)
                }
            }

        marketplaceBtn.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java))
        }

        walletBtn.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }

        reportBtn.setOnClickListener {
            Toast.makeText(this, "بخش آمار در نسخه بعدی", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

class SimpleDiscountAdapter(private val items: List<String>) :
    RecyclerView.Adapter<SimpleDiscountAdapter.ViewHolder>() {

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
