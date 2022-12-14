package com.example.rssreader

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.io.Serializable

class FavoritesRecyclerAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<FavoritesRecyclerAdapter.RecyclerViewHolder>() {

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
            .inflate(R.layout.cardview_row_favs,parent,false)

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        holder.removeButton.setOnClickListener {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot
                        .child("RSS_DATABASE")
                        .child(userId.toString())
                        .child("Favorites")
                        .child(currentItem.pubDate.toString())
                        .ref.removeValue()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class RecyclerViewHolder(itemView : View, listener : onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val pubDate: TextView = itemView.findViewById(R.id.textDate)
        val desc: TextView = itemView.findViewById(R.id.textContent)
        val image: ShapeableImageView = itemView.findViewById(R.id.recyclerViewImage)
        val removeButton: Button = itemView.findViewById(R.id.buttonRemove)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

        }
    }
}