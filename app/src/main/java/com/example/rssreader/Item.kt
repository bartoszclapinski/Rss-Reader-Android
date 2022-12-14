package com.example.rssreader

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Item (
    var title: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var imageUrl: String? = null,
    var desc: String? = null
        )  {

}