package com.discountmarket.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(mainLooper).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        when (doc.getString("type")) {
                            "store" -> startActivity(Intent(this, StoreDashboardActivity::class.java))
                            "customer" -> startActivity(Intent(this, CustomerDashboardActivity::class.java))
                            "admin" -> startActivity(Intent(this, AdminPanelActivity::class.java))
                        }
                    }
            }
            finish()
        }, 1500)
    }
}
