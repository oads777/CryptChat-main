package com.worldsvoice.cryptchat.utils

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

// CryptoUtils.kt
object CryptoUtils {
    // Função para gerar a chave secreta (usada no registro)
    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256) // 256-bit AES encryption
        return keyGenerator.generateKey()
    }

    // Função para obter a chave secreta a partir de uma string
    fun getKeyFromString(keyString: String): SecretKey {
        val decodedKey = Base64.decode(keyString, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    // Função para criptografar uma mensagem
    fun encryptMessage(message: String, secretKey: SecretKey): String {
        // Cria um objeto Cipher para realizar a criptografia usando o algoritmo AES
        val cipher = Cipher.getInstance("AES")

        // Inicializa o Cipher em modo de criptografia com a chave secreta
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        // Converte a mensagem em uma sequência de bytes usando UTF-8 e aplica a criptografia
        val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))

        // Codifica os bytes criptografados em Base64 para uma string
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }


    // Função para descriptografar uma mensagem
    fun decryptMessage(encryptedMessage: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
