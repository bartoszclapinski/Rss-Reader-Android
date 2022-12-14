package com.example.rssreader

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class ParserHandler {

    private val items = ArrayList<Item>()
    private var item: Item? = null
    private var text: String? = null


    fun parse(inputStream: InputStream): List<Item> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, Charsets.UTF_8.name())
            var eventType = parser.eventType
            var foundItem = false
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagName.contains("item", ignoreCase = true)) {
                        foundItem = true
                        item = Item()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagName.contains("item", ignoreCase = true)) {
                        item?.let { items.add(it) }
                        foundItem = false
                    } else if (foundItem && tagName.contains("title", ignoreCase = true)) {
                        item!!.title = text.toString()
                    } else if (foundItem && tagName.contains("link", ignoreCase = true)) {
                        item!!.link = text.toString()
                    } else if (foundItem && tagName.contains("pubDate", ignoreCase = true)) {
                        item!!.pubDate = text.toString()
                    } else if (foundItem && tagName.contains("enclosure", ignoreCase = true)) {
                        item!!.imageUrl = parser.getAttributeValue("", "url").toString()
                    } else if (foundItem && tagName.contains("description", ignoreCase = true)) {
                        item!!.desc = text.toString()
                            .substring(text.toString().indexOf('>', 0, false) + 1)
                    }
                    else -> {
                    }
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return items
    }
}