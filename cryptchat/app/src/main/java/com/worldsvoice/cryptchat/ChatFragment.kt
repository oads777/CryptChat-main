package com.worldsvoice.cryptchat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.worldsvoice.cryptchat.adpater.SearchUsersAdapter
import com.worldsvoice.cryptchat.model.HelperClass
import com.worldsvoice.cryptchat.model.MessagesModel
import com.worldsvoice.cryptchat.model.UserModel

class ChatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var dbRefUsers: DatabaseReference
    lateinit var dbRefChat: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private lateinit var adapter: SearchUsersAdapter
    var listOfUsers: ArrayList<UserModel> = ArrayList()
    var listOfMessages: ArrayList<MessagesModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        return view.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.search_user_rycler_view)
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users")
        dbRefChat = FirebaseDatabase.getInstance().getReference("Chats")

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

    }

    override fun onResume() {
        super.onResume()

        progressDialog.show()
        dbRefUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listOfUsers.clear()
                    progressDialog.dismiss()
                    for (ds in snapshot.children) {
                        try {
                            val model: UserModel? = ds.getValue(UserModel::class.java)
                            if (model?.userId != HelperClass.users?.userId) {
                                listOfUsers.add(model!!)
                            }
                        } catch (e: DatabaseException) {
                            e.printStackTrace()
                        }
                    }

                } else {
                    progressDialog.dismiss()
                }

                if (listOfUsers.isNotEmpty()) {
                    getAllMessages()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getAllMessages() {
        dbRefChat.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listOfMessages.clear()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(MessagesModel::class.java)
                    if (message?.senderId
                            .equals(HelperClass.users?.userId) || message?.receiverId
                            .equals(HelperClass.users?.userId)
                    ) {
                        listOfMessages.add(message!!)
                    }
                }

                val filteredUsers =
                    getUsersWithChat(listOfUsers, listOfMessages, HelperClass.users?.userId!!)
                adapter = SearchUsersAdapter(filteredUsers, requireContext())
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun getUsersWithChat(
        listOfUsers: List<UserModel>,
        listOfMessages: List<MessagesModel>,
        myUserId: String
    ): List<UserModel> {
        val usersWithMessages = listOfMessages
            .filter { it.senderId == myUserId || it.receiverId == myUserId }
            .flatMap { listOf(it.senderId, it.receiverId) }
            .filterNotNull()
            .toSet()

        return listOfUsers.filter { it.userId in usersWithMessages }
    }

}