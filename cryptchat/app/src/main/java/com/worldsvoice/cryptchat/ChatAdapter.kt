package com.worldsvoice.cryptchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val chatList: List<Chat>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(chatId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvChatName: TextView = itemView.findViewById(R.id.tvChatName)

        fun bind(chat: Chat) {
            tvChatName.text = chat.name
            itemView.setOnClickListener {
                listener.onItemClick(chat.id)
            }
        }
    }
}
