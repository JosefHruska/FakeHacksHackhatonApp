package com.example.peanutbook.fakeappka.ui

import android.arch.lifecycle.MutableLiveData
import com.example.peanutbook.fakeappka.extensions.findByName
import com.example.peanutbook.fakeappka.firebase.DatabaseRead
import com.example.peanutbook.fakeappka.model.Website
import com.example.peanutbook.fakeappka.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * View model for [MainActivity]
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainViewModel : BaseViewModel<MainController>() {

    private val mWebsites = MutableLiveData<List<Website>>()
    private var mSelectedWebsiteUrl: String? = null
    private var mSelectedWebsite = MutableLiveData<Website>()

    override fun onViewAttached() {
        loadWebsites()
    }

    fun getWebsites(): MutableLiveData<List<Website>> = mWebsites

    fun getSelectedWebsite(): MutableLiveData<Website> = mSelectedWebsite

    fun websiteSelected(selectedWebsite: String) {
        mSelectedWebsiteUrl = selectedWebsite
        mSelectedWebsite.value = mWebsites.value?.findByName(selectedWebsite)
    }

    private fun loadWebsites() {
        DatabaseRead.websites().observeOn(AndroidSchedulers.mainThread()).subscribe {
            mWebsites.value = it.toNullable()
            mSelectedWebsite.value = it.toNullable()?.findByName(mSelectedWebsiteUrl)
        }
    }
}