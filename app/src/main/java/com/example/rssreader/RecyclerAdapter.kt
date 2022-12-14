package com.example.rssreader

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    private lateinit var itemListener : onItemClickListener


    interface onItemClickListener {
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener) {
        itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.cardview_row,parent,false)


        return RecyclerViewHolder(itemView, itemListener)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = items[position]

        holder.title.text = currentItem.title
        holder.pubDate.text = currentItem.pubDate

        holder.desc.text = currentItem.desc

        Picasso.with(holder.image.context)
            .load(Uri.parse(currentItem.imageUrl))
            .into(holder.image)

        val database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser

        holder.addToFavorites.setOnClickListener {

            database
                .child("RSS_DATABASE")
                .child(user?.uid.toString())
                .child("Favorites")
                .child(currentItem.pubDate.toString())
                .setValue(currentItem)

        }

    }

    override fun getItemCount(): Int {
       return items.size
    }

    class RecyclerViewHolder(itemView : View, listener : onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textTitle)
        val pubDate = itemView.findViewById<TextView>(R.id.textDate)
        val desc = itemView.findViewById<TextView>(R.id.textContent)
        val image = itemView.findViewById<ShapeableImageView>(R.id.recyclerViewImage)
        val addToFavorites = itemView.findViewById<Button>(R.id.buttonAddToFavorites)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
                itemView.findViewById<TextView>(R.id.textHasBeenRead).visibility = View.VISIBLE
            }

        }
    }
}