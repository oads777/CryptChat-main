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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity(), ChatAdapter.OnItemClickListener {

    private lateinit var rvChats: RecyclerView
    private lateinit var btnAddFriend: FloatingActionButton
    private lateinit var btnCreateChat: FloatingActionButton
    private val db = FirebaseFirestore.getInstance()
    private val chatList = mutableListOf<Chat>()
    private lateinit var chatAdapter: ChatAdapter

    // Obter o usuário logado
    private val auth = FirebaseAuth.getInstance()

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
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: "" // Pegue o ID do usuário logado

        // Filtrar os chats em que o usuário logado está na lista de membros
        db.collection("chats")
            .whereArrayContains("members", currentUser)  // Filtra chats onde o usuário está na lista de membros
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

        // Campos para inserir o nome do chat e o nome do usuário
        val inputLayout = layoutInflater.inflate(R.layout.dialog_create_chat, null)
        val etChatName = inputLayout.findViewById<EditText>(R.id.etChatName)
        val etFriendUsername = inputLayout.findViewById<EditText>(R.id.etFriendUsername)

        dialog.setView(inputLayout)

        dialog.setPositiveButton("Criar") { _, _ ->
            val chatName = etChatName.text.toString().trim()
            val friendUsername = etFriendUsername.text.toString().trim()

            if (chatName.isNotEmpty() && friendUsername.isNotEmpty()) {
                createChat(chatName, friendUsername)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton("Cancelar", null)
        dialog.show()
    }

    private fun createChat(chatName: String, friendUsername: String) {
        // Verificar se o usuário existe antes de criar o chat
        checkUserExists(friendUsername) { exists ->
            if (exists) {
                // Adiciona o nome do chat e os membros (usuário atual e o amigo)
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: "" // ID do usuário logado
                val members = listOf(currentUser, friendUsername)

                // Gerar uma chave secreta para o chat
                val secretKey = generateSecretKey() // Função que gera a chave secreta

                val chatData = hashMapOf(
                    "name" to chatName,
                    "members" to members,
                    "secretKey" to secretKey  // Salvando a chave secreta
                )

                // Salva o novo chat no Firestore
                db.collection("chats")
                    .add(chatData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Chat criado com sucesso!", Toast.LENGTH_SHORT).show()
                        loadChats() // Recarregar a lista de chats
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao criar chat", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateSecretKey(): String {
        // Exemplo simplificado de geração de chave secreta
        return "chaveSecreta123" // Você pode substituir isso por um algoritmo de geração de chave real
    }




    private fun checkUserExists(username: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(username).get()
            .addOnSuccessListener { document ->
                callback(document.exists()) // Retorna verdadeiro se o documento existir
            }
            .addOnFailureListener {
                callback(false) // Retorna falso em caso de erro
            }
    }

    override fun onItemClick(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("CHAT_ID", chatId)
        startActivity(intent)
    }
}
