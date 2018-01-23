package com.example.peanutbook.fakeappka.model

import com.example.peanutbook.fakeappka.UNDEFINED
import net.alhazmy13.wordcloud.WordCloud

/**
 * Created by PeanutBook on 19.01.2018.
 */

class Website : DatabaseModel() {

    var houses: ArrayList<String> = arrayListOf()
    var established = UNDEFINED
    var people: ArrayList<String> = arrayListOf()

    var topic: ArrayList<String> = arrayListOf()

    var network: HashMap<String, Network> = hashMapOf()
    var following: HashMap<String, Double> = hashMapOf()
    var followers: HashMap<String, Double> = hashMapOf()

    var related_sites: ArrayList<String> = arrayListOf()
    var shareRate: Double = 0.0

    var tags: HashMap<String, Double> = hashMapOf()

    fun getWords() = tags.map {
        WordCloud(it.key, it.value.times(1000).toInt())
    }

}