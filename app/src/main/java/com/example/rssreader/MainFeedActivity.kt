package com.example.rssreader

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.databinding.ActivityMainFeedBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class MainFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainFeedBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var recyclerView : RecyclerView

    private var items: List<Item>? = null

    private val urlPoland: String = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.htm"
    private val urlWorld: String = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_swiat.htm"
    var urlFinal: String? = null

    private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locateUser()

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.buttonFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonLogout.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


    }

    private fun locateUser() {
        val task = fusedLocationClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val country = geocoder.getFromLocation(it.latitude, it.longitude, 1)[0].countryCode
                println(country)
                urlFinal = if (country.equals("PL")) {
                    urlPoland
                } else {
                    urlWorld
                }
                println(urlFinal)
                ReadData().execute(urlFinal)
            }
        }
    }

    inner class ReadData : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String): String {
            val httpDataHandler = HTTPDataHandler()


            return httpDataHandler.GetHttpDataHandler(p0[0])
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            items = ParserHandler().parse(result.byteInputStream())

            val adapter = RecyclerAdapter(items!!)
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener(object : RecyclerAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    var clickedItem = items!![position]
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(clickedItem.link))
                    startActivity(browserIntent)
                }
            })
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val email = firebaseUser.email.toString()
            binding.textUserEmail.text = email
        }
    }


}