package com.worldsvoice.cryptchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.worldsvoice.cryptchat.adpater.SearchUserRyclerAdpater
import com.worldsvoice.cryptchat.utils.FirebaseUtil
import com.worldsvoice.cryptchat.model.UserModel

class SearchUser : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: SearchUserRyclerAdpater


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        searchInput = findViewById(R.id.search_username_input)
        searchButton = findViewById(R.id.search_user_btn)
        backButton = findViewById(R.id.back_btn)
        recyclerView = findViewById(R.id.search_user_rycler_view)

        searchInput.requestFocus()

        backButton.setOnClickListener {
           onBackPressed()
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

    fun setupSearchRecyclerView(searchTerm: String) {
        val query = FirebaseUtil.allUserCollectionReference()
            .whereGreaterThanOrEqualTo("username", searchTerm)

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .build()

        adapter = SearchUserRyclerAdpater(options,applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::adapter.isInitialized) {
            adapter.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }
}