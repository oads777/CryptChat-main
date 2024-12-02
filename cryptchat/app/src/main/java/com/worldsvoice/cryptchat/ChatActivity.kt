package com.worldsvoice.cryptchat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.worldsvoice.cryptchat.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val db = FirebaseFirestore.getInstance()
    private val messages = mutableListOf<String>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatId = intent.getStringExtra("CHAT_ID")

        // Configurar RecyclerView
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messages)
        binding.rvMessages.adapter = adapter

        // Carregar mensagens
        loadMessages(chatId)

        // Enviar nova mensagem
        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(chatId, message)
            } else {
                Toast.makeText(this, "Mensagem vazia não pode ser enviada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMessages(chatId: String?) {
        if (chatId == null) {
            Toast.makeText(this, "Erro ao carregar mensagens: Chat ID inválido", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Erro ao carregar mensagens", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                messages.clear()
                for (doc in snapshots!!) {
                    val message = doc.getString("message")
                    message?.let { messages.add(it) }
                }
                adapter.notifyDataSetChanged()
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
    }

    private fun sendMessage(chatId: String?, message: String) {
        if (chatId == null) {
            Toast.makeText(this, "Erro ao enviar mensagem: Chat ID inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val chatMessage = hashMapOf(
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("chats").document(chatId).collection("messages")
            .add(chatMessage)
            .addOnSuccessListener {
                binding.etMessage.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show()
            }
    }
}
