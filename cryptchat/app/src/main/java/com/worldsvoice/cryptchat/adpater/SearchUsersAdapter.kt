package com.worldsvoice.cryptchat.adpater

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.worldsvoice.cryptchat.ChatActivity
import com.worldsvoice.cryptchat.R
import com.worldsvoice.cryptchat.model.UserModel


class SearchUsersAdapter(var list: List<UserModel>, var context: Context) :
    RecyclerView.Adapter<SearchUsersAdapter.Vh?>() {

    fun setUserList(list: List<UserModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view =
            LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false)
        return Vh(view)
    }

    override fun onBindViewHolder(holder: Vh, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]
        holder.usernameText.text = model.username

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("data", model)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var usernameText: TextView = itemView.findViewById(R.id.user_name_text)
    }
}
