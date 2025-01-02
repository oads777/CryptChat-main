package com.worldsvoice.cryptchat.adpater

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.worldsvoice.cryptchat.R
import com.worldsvoice.cryptchat.model.MessagesModel
import com.worldsvoice.cryptchat.utils.CryptoUtils

class MessageAdapter(
    private val context: Context,
    private val messages: List<MessagesModel>,
    var textToSpeech: TextToSpeech
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false)
            return SenderVH(view)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val secretKey = CryptoUtils.getKeyFromString(message?.encryptKey!!)
        val decryptedMessage = CryptoUtils.decryptMessage(message?.encryptedMessage!!, secretKey)
        if (holder is ViewHolder) {
            holder.dateText.text = message.dateTime
            holder.messageText.setOnClickListener {
                if (textToSpeech.isSpeaking) {
                    textToSpeech.stop()
                } else {
                    textToSpeech.speak(decryptedMessage, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
            holder.messageText.setText(decryptedMessage)
        } else {
            (holder as SenderVH).dateText.text = message.dateTime
            holder.messageText.setOnClickListener {
                if (textToSpeech.isSpeaking) {
                    textToSpeech.stop()
                } else {
                    textToSpeech.speak(decryptedMessage, TextToSpeech.QUEUE_FLUSH, null)
                }
            }

            holder.messageText.setText(decryptedMessage)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.message_text)
        var dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    inner class SenderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.message_text)
        var dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    companion object {
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPE_RIGHT = 1
    }
}
