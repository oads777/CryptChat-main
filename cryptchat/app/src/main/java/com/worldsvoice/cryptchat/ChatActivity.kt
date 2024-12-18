package com.worldsvoice.cryptchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import android.widget.ImageButton
import com.google.firebase.Timestamp
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore
import com.worldsvoice.cryptchat.databinding.ActivityChatBinding
import com.worldsvoice.cryptchat.model.ChatroomModel
import com.worldsvoice.cryptchat.model.UserModel
import com.worldsvoice.cryptchat.utils.AndroidUtil
import javax.crypto.SecretKey
import com.worldsvoice.cryptchat.utils.CryptoUtils
import com.worldsvoice.cryptchat.utils.FirebaseUtil

class ChatActivity : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var otherUsername: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var otherUser: UserModel
    private lateinit var chatroomId: String

    private lateinit var chatroomModel: ChatroomModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        /*otherUser = AndroidUtil.getUserModelFromIntent(intent)
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId())

        // Find views by ID
        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
*/
        // Set back button click listener
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        /*// Set username text
        otherUsername.text = otherUser.getUsername()

        getOrCreateChatroomModel()*/
    }

    private fun getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var chatroomModel = task.result?.toObject(ChatroomModel::class.java)
                if (chatroomModel == null) {
                    // Primeiro chat: cria um novo modelo de Chatroom
                    chatroomModel = ChatroomModel(
                        chatroomId,
                        listOf(FirebaseUtil.currentUserId(), otherUser.getUserId()), // Lista de IDs
                        com.google.firebase.Timestamp.now(), // Timestamp do Firebase
                        "", // lastMessageSenderId vazio
                        "" // lastMessage vazio
                    )
                }
            }
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_chat)
 
         // Inicialização dos componentes
         authRepo = AuthRepository() // Seu repositório de autenticação
         recyclerView = findViewById(R.id.search_user_rycler_view) // RecyclerView (ID corrigido)
         messageAdapter = MessageAdapter(messages)
         recyclerView.layoutManager = LinearLayoutManager(this) // Configurar o layout
         recyclerView.adapter = messageAdapter
 
         // Recuperando o chatId do Intent
         chatId = intent.getStringExtra("CHAT_ID") ?: ""
         if (chatId.isNotEmpty()) {
             // Recuperar a chave secreta do usuário após o login
             val username = intent.getStringExtra("username")
             if (username != null) {
                 authRepo.getUserSecretKey(username) { key ->
                     if (key != null) {
                         secretKey = key
                         Toast.makeText(this, "Chave secreta encontrada", Toast.LENGTH_SHORT).show() // Notificação de chave secreta encontrada
                         loadMessages() // Carregar mensagens após a chave secreta ser carregada
                     } else {
                         Toast.makeText(this, "Erro ao carregar chave secreta", Toast.LENGTH_SHORT).show()
                     }
                 }
             }
         } else {
             Toast.makeText(this, "Erro: chatId não encontrado", Toast.LENGTH_SHORT).show()
         }
 
         /*val btnSend = findViewById<Button>(R.id.main_layout_frame)
         val etMessage = findViewById<EditText>(R.id.tvMessage)
 
         btnSend.setOnClickListener {
             val message = etMessage.text.toString().trim()
             if (message.isNotEmpty() && ::secretKey.isInitialized) {
                 // Criptografar a mensagem antes de salvar
                 val encryptedMessage = CryptoUtils.encryptMessage(message, secretKey)
                 // Enviar para o Firestore
                 sendMessageToFirestore(encryptedMessage)
                 etMessage.text.clear()
             } else {
                 Toast.makeText(this, "A chave secreta não está carregada", Toast.LENGTH_SHORT).show()
             }
         }*/
     }
 
     private fun sendMessageToFirestore(encryptedMessage: String) {
         val db = FirebaseFirestore.getInstance()
         val messageData = hashMapOf(
             "message" to encryptedMessage,
             "timestamp" to System.currentTimeMillis(),
             "senderId" to FirebaseAuth.getInstance().currentUser?.uid // ID do usuário que enviou a mensagem
         )
 
         db.collection("chats")
             .document(chatId) // Agora estamos usando o chatId corretamente
             .collection("messages") // Subcoleção para mensagens de um chat específico
             .add(messageData)
             .addOnSuccessListener {
                 // Mensagem enviada com sucesso
                 loadMessages() // Recarregar as mensagens
             }
             .addOnFailureListener {
                 // Erro ao enviar mensagem
                 Toast.makeText(this, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show()
             }
     }
 
     private fun loadMessages() {
         val db = FirebaseFirestore.getInstance()
         db.collection("chats")
             .document(chatId) // Agora estamos usando o chatId corretamente
             .collection("messages") // Subcoleção de mensagens
             .orderBy("timestamp") // Ordenar por timestamp
             .get()
             .addOnSuccessListener { documents ->
                 messages.clear()
                 for (document in documents) {
                     val encryptedMessage = document.getString("message") ?: ""
                     // Descriptografar a mensagem antes de exibir
                     val decryptedMessage = CryptoUtils.decryptMessage(encryptedMessage, secretKey)
                     messages.add(decryptedMessage)
                 }
                 messageAdapter.notifyDataSetChanged()
             }
             .addOnFailureListener {
                 Toast.makeText(this, "Erro ao carregar mensagens", Toast.LENGTH_SHORT).show()
             }
     }*/
}
