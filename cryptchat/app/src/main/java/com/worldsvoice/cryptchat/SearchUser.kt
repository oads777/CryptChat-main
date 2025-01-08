package com.worldsvoice.cryptchat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.worldsvoice.cryptchat.model.UserModel

class SearchUser : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    lateinit var dbRefUsers: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private lateinit var adapter: SearchUsersAdapter
    var list: ArrayList<UserModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        searchInput = findViewById(R.id.search_username_input)
        searchButton = findViewById(R.id.search_user_btn)
        backButton = findViewById(R.id.back_btn)
        recyclerView = findViewById(R.id.search_user_rycler_view)
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users")

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        getDataFromDatabase()

        searchInput.requestFocus()

        backButton.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            val searchTerm = searchInput.text.toString()
            if (searchTerm.isEmpty() || searchTerm.length < 3) {
                searchInput.error = "Invalid Username"
                searchInput.requestFocus() // Foca no campo para corrigir o erro
                return@setOnClickListener
            }
            setupSearchRecyclerView(searchTerm)
        }
    }

    private fun getDataFromDatabase() {
        progressDialog.show()
        dbRefUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()
                    progressDialog.dismiss()
                    for (ds in snapshot.children) {
                        try {
                            val model: UserModel? = ds.getValue(UserModel::class.java)
                            if (model?.userId != HelperClass.users?.userId) {
                                list.add(model!!)
                            }
                        } catch (e: DatabaseException) {
                            e.printStackTrace()
                        }
                    }

                } else {
                    progressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@SearchUser, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setupSearchRecyclerView(searchTerm: String) {
        var usersList: ArrayList<UserModel> = ArrayList()
        usersList.clear()
        list.forEach {
            if (it.username?.contains(searchTerm) == true) {
                usersList.add(it)
            }
        }
        adapter = SearchUsersAdapter(usersList, applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}