package com.worldsvoice.cryptchat

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity(), ChatAdapter.OnItemClickListener {

    private lateinit var rvChats: RecyclerView
    private lateinit var btnAddFriend: FloatingActionButton
    private lateinit var btnCreateChat: FloatingActionButton
    private val db = FirebaseFirestore.getInstance()
    private val chatList = mutableListOf<Chat>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Configurar RecyclerView
        rvChats = findViewById(R.id.rvChats)
        rvChats.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(chatList, this)
        rvChats.adapter = chatAdapter

        // Botões da dashboard
        btnAddFriend = findViewById(R.id.btnAddFriend)
        btnCreateChat = findViewById(R.id.btnCreateChat)

        btnAddFriend.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de adicionar amigo em breve!", Toast.LENGTH_SHORT).show()
        }

        btnCreateChat.setOnClickListener {
            showCreateChatDialog()
        }

        // Carregar os chats existentes
        loadChats()
    }

    private fun loadChats() {
        db.collection("chats")
            .get()
            .addOnSuccessListener { documents ->
                chatList.clear()
                for (doc in documents) {
                    val chatId = doc.id
                    val chatName = doc.getString("name") ?: "Chat sem Nome"
                    chatList.add(Chat(chatId, chatName))
                }
                chatAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar chats", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCreateChatDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Criar Novo Chat")

        // Campo para inserir o nome do chat
        val input = EditText(this)
        input.hint = "Nome do Chat"
        dialog.setView(input)

        dialog.setPositiveButton("Criar") { _, _ ->
            val chatName = input.text.toString().trim()
            if (chatName.isNotEmpty()) {
                createChat(chatName)
            } else {
                Toast.makeText(this, "Nome do chat não pode estar vazio", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton("Cancelar", null)
        dialog.show()
    }

    private fun createChat(chatName: String) {
        val chatData = hashMapOf(
            "name" to chatName,
            "members" to listOf<String>() // Adicionar IDs de membros posteriormente
        )

        db.collection("chats")
            .add(chatData)
            .addOnSuccessListener {
                Toast.makeText(this, "Chat criado com sucesso!", Toast.LENGTH_SHORT).show()
                loadChats()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao criar chat", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onItemClick(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("CHAT_ID", chatId)
        startActivity(intent)
    }
}
