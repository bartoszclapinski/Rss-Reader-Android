package com.example.rssreader

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.databinding.ActivityFavoritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.util.*
import kotlin.collections.HashMap

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFavoritesBinding

    private lateinit var recyclerView: RecyclerView

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var database: DatabaseReference

    private lateinit var favoriteItems : List<Item>

    private lateinit var currentUserId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteItems = listOf()

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.buttonBack.setOnClickListener {
            startActivity(Intent(this, MainFeedActivity::class.java))
            finish()
        }

        binding.buttonLogout.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        getFavorites()

    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val email = firebaseUser.email.toString()
            binding.textUserEmail.text = email
            currentUserId = firebaseUser.uid
        }
    }

    private fun getFavorites() {

        database = FirebaseDatabase.getInstance().reference
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                var arrSnapshot = arrayListOf<Item>()

                for (snapshotItem in snapshot.children) {
                    snapshotItem.child(currentUserId).child("Favorites").children.forEach {
                        arrSnapshot.add(it.getValue(Item::class.java)!!)
                    }
                }

                favoriteItems = arrSnapshot.toList()

                val adapter = FavoritesRecyclerAdapter(favoriteItems)
                adapter.setOnItemClickListener(object : FavoritesRecyclerAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        var clickedItem = favoriteItems[position]
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(clickedItem.link))
                        startActivity(browserIntent)
                    }
                })
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}