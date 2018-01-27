package com.example.peanutbook.fakeappka.model

import com.example.peanutbook.fakeappka.extensions.UNDEFINED
import net.alhazmy13.wordcloud.WordCloud

/**
 * Model representing single website
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */


class Website : DatabaseModel() {

    var houses: ArrayList<String> = arrayListOf()
    var established = UNDEFINED
    var people: ArrayList<String> = arrayListOf()

    var topic: ArrayList<String> = arrayListOf()

    var following: HashMap<String, Double> = hashMapOf()
    var followers: HashMap<String, Double> = hashMapOf()

    var related_sites: ArrayList<String> = arrayListOf()
    var shareRate: Double = 0.0

    var tags: HashMap<String, Double> = hashMapOf()

    fun getWords() = tags.map {
        WordCloud(it.key, it.value.times(1000).toInt())
    }

}