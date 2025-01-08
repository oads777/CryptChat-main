package com.worldsvoice.cryptchat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.worldsvoice.cryptchat.utils.CryptoUtils
import android.util.Base64
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.crypto.SecretKey

// AuthRepository.kt
// AuthRepository.kt
class AuthRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dbRef: DatabaseReference

    // Método para registrar um novo usuário com nome de usuário
    fun register(username: String, email: String, password: String): Task<Void> {
        val secretKey = CryptoUtils.generateKey() // Gerar a chave secreta
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)

        // Criptografar a senha antes de salvar
        val encryptedPassword = CryptoUtils.encryptMessage(password, secretKey)

        // Salvar dados do usuário no Firestore
        val userData = hashMapOf(
            "username" to username,
            "password" to encryptedPassword, // Armazenando a senha criptografada
            "secretKey" to encodedKey
        )

        return db.collection("users").document(username).set(userData)
    }

    // Método para verificar o login de um usuário
    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        db.collection("users").document(username).get().addOnSuccessListener { document ->
            if (document != null) {
                val storedEncryptedPassword = document.getString("password") // Recuperando a senha criptografada
                val keyString = document.getString("secretKey")
                if (storedEncryptedPassword != null && keyString != null) {
                    val secretKey = CryptoUtils.getKeyFromString(keyString)

                    // Descriptografar a senha armazenada
                    val decryptedPassword = CryptoUtils.decryptMessage(storedEncryptedPassword, secretKey)

                    // Comparar a senha fornecida com a armazenada
                    if (decryptedPassword == password) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            } else {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getUserSecretKey(uid: String, callback: (SecretKey?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val secretKeyString = document.getString("secretKey") // A chave secreta armazenada
                    if (secretKeyString != null) {
                        val secretKey = CryptoUtils.getKeyFromString(secretKeyString) // Substituindo stringToSecretKey
                        callback(secretKey)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }


}
