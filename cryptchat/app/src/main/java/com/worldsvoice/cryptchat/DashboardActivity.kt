package com.worldsvoice.cryptchat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var rvChats: RecyclerView
    private lateinit var btnAddFriend: FloatingActionButton
    private lateinit var btnCreateChat: FloatingActionButton

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<String>() // Lista dinâmica de chats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        rvChats = findViewById(R.id.rvChats)
        rvChats.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(chatList)
        rvChats.adapter = chatAdapter

        // Carregar os chats do Firebase
        loadChats()

        // Configurar botão para adicionar amigos
        btnAddFriend = findViewById(R.id.btnAddFriend)
        btnAddFriend.setOnClickListener {
            addFriend()
        }

        // Configurar botão para criar novo chat
        btnCreateChat = findViewById(R.id.btnCreateChat)
        btnCreateChat.setOnClickListener {
            createChat()
        }
    }

    // Função para carregar os chats do Firebase
    private fun loadChats() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("chats")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { documents ->
                chatList.clear()
                for (document in documents) {
                    val chatName = document.getString("name") ?: "Chat sem nome"
                    chatList.add(chatName)
                }
                chatAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar chats: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Função para adicionar um amigo
    private fun addFriend() {
        val email = "friend_email@example.com" // Aqui você pode abrir um diálogo para o usuário inserir o email do amigo
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Amigo não encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    val friendId = documents.first().id
                    val currentUserId = auth.currentUser?.uid
                    if (currentUserId != null) {
                        db.collection("friendships").add(
                            mapOf(
                                "user1" to currentUserId,
                                "user2" to friendId
                            )
                        )
                            .addOnSuccessListener {
                                Toast.makeText(this, "Amigo adicionado com sucesso", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao adicionar amigo: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar amigo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Função para criar um novo chat
    private fun createChat() {
        val chatName = "Novo Chat" // Aqui você pode abrir um diálogo para o usuário inserir o nome do chat
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val chatData = mapOf(
            "name" to chatName,
            "members" to listOf(userId)
        )
        db.collection("chats")
            .add(chatData)
            .addOnSuccessListener { documentReference ->
                chatList.add(chatName)
                chatAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Chat criado com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao criar chat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
