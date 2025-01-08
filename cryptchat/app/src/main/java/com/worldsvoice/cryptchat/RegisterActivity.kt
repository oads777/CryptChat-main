package com.worldsvoice.cryptchat

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.worldsvoice.cryptchat.model.UserModel
import com.worldsvoice.cryptchat.utils.CryptoUtils

// RegisterActivity.kt
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var dbRefUsers: DatabaseReference
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBackToLogin = findViewById<Button>(R.id.btnBackToLogin)

        auth = FirebaseAuth.getInstance()
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users")

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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

            if (password != confirmPassword) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.show()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult: AuthResult? ->
                    val secretKey = CryptoUtils.generateKey() // Gerar a chave secreta
                    val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
                    // Criptografar a senha antes de salvar
                    val encryptedPassword = CryptoUtils.encryptMessage(password, secretKey)

                    val model =
                        UserModel(
                            auth.currentUser!!.uid,
                            username,
                            email,
                            encryptedPassword,
                            encodedKey
                        )

                    dbRefUsers.child(auth.currentUser!!.uid).setValue(model)
                        .addOnCompleteListener { task: Task<Void?>? ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "Registro concluído", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener { e: Exception ->
                            progressDialog.dismiss()
                            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener { e: Exception ->
                    progressDialog.dismiss()
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }

        btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}
