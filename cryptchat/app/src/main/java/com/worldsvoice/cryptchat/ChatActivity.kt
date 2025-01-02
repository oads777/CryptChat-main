package com.worldsvoice.cryptchat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.worldsvoice.cryptchat.adpater.MessageAdapter
import com.worldsvoice.cryptchat.model.HelperClass
import com.worldsvoice.cryptchat.model.MessagesModel
import com.worldsvoice.cryptchat.model.UserModel
import com.worldsvoice.cryptchat.utils.CryptoUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var otherUsername: TextView
    private lateinit var recyclerView: RecyclerView
    lateinit var dbRefChat: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private lateinit var otherUser: UserModel
    var chatList: ArrayList<MessagesModel> = ArrayList()
    private lateinit var adapter: MessageAdapter
    var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        otherUser = intent.getSerializableExtra("data") as UserModel

        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
        textToSpeech = TextToSpeech(
            applicationContext
        ) { i ->
            // if No error is found then only it will run
            if (i != TextToSpeech.ERROR) {
                // To Choose language of speech
                textToSpeech!!.setLanguage(Locale.US)
            }
        }


        dbRefChat = FirebaseDatabase.getInstance().getReference("Chats")

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        adapter = MessageAdapter(this, chatList, textToSpeech!!)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(adapter)

        backBtn.setOnClickListener {
            finish()
        }

        otherUsername.text = otherUser.username

        sendMessageBtn.setOnClickListener { sendMessage() }

    }

    override fun onResume() {
        super.onResume()
        loadMessages()
    }

    private fun sendMessage() {
        @SuppressLint("SimpleDateFormat") val formattedDate =
            SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa").format(Calendar.getInstance().time)
        val messageText: String = messageInput.getText().toString().trim()
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please write message", Toast.LENGTH_SHORT).show()
        } else {
            val messageId: String = dbRefChat.push().getKey().toString()
            val secretKey = CryptoUtils.generateKey() // Gerar a chave secreta
            val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
            val encryptedMessage = CryptoUtils.encryptMessage(messageText, secretKey)
            val message = MessagesModel(
                messageId,
                HelperClass.users?.userId,
                otherUser.userId,
                encryptedMessage,
                encodedKey,
                formattedDate
            )
            dbRefChat.child(messageId).setValue(message)
            messageInput.setText("")
        }
    }

    private fun loadMessages() {
        dbRefChat.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(MessagesModel::class.java)
                    if (message?.senderId
                            .equals(HelperClass.users?.userId) && message?.receiverId
                            .equals(otherUser.userId)
                    ) {
                        chatList.add(message!!)
                    } else if (message?.senderId.equals(otherUser.userId) && message?.receiverId
                            .equals(HelperClass.users?.userId)
                    ) {
                        chatList.add(message!!)
                    }
                }
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(chatList.size - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

}
