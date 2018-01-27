package com.example.peanutbook.fakeappka.logic

import com.example.peanutbook.fakeappka.model.Website



object WebsiteHelper {
    var mWebsites: List<Website> = listOf()

    fun getWebsites() = mWebsites

    fun setWebsites(websites: List<Website>) {
        mWebsites = websites
    }
}
