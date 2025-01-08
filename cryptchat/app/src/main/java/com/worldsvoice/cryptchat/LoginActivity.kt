package com.worldsvoice.cryptchat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.worldsvoice.cryptchat.model.HelperClass
import com.worldsvoice.cryptchat.model.UserModel
import com.worldsvoice.cryptchat.utils.CryptoUtils


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var dbRefUsers: DatabaseReference
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        auth = FirebaseAuth.getInstance()
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users")

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this,
                    "Por favor, insira o e-mail que deve estar no formato correto",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            progressDialog.show()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dbRefUsers.child(auth.currentUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val model: UserModel? =
                                        snapshot.getValue(UserModel::class.java)
                                    HelperClass.users = model
                                    progressDialog.dismiss()
                                    val intent =
                                        Intent(this@LoginActivity, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Os dados do usuário não existem",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    progressDialog.dismiss()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@LoginActivity,
                                    error.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
