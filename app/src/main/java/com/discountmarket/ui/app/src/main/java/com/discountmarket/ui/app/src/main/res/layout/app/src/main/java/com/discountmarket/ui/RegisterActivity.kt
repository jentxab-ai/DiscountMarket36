package com.discountmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.discountmarket.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val typeGroup = findViewById<RadioGroup>(R.id.typeGroup)
        val registerBtn = findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val type = if (typeGroup.checkedRadioButtonId == R.id.storeRadio) "store" else "customer"

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "رمز عبور حداقل ۶ کاراکتر باشد", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = "${phone}@discountmarket.app"
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result?.user?.uid ?: ""
                        val uniqueCode = "DM-${phone.takeLast(4)}-${(10000..99999).random()}"

                        val userMap = hashMapOf(
                            "id" to userId,
                            "name" to name,
                            "phone" to phone,
                            "type" to type,
                            "uniqueId" to uniqueCode,
                            "walletBalance" to 0L,
                            "createdAt" to System.currentTimeMillis()
                        )

                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "ثبت‌نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "خطا: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "خطا در ثبت‌نام: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
