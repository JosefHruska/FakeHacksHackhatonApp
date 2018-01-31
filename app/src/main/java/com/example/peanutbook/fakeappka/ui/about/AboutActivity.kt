package com.example.peanutbook.fakeappka.ui.about

import com.example.peanutbook.fakeappka.R
import com.example.peanutbook.fakeappka.ui.base.BaseActivity

/**
 *  Activity displaying credits.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class AboutActivity : BaseActivity<AboutViewModel, AboutController>(), AboutController {

    override fun getViewModel() = AboutViewModel()

    override fun getLayoutRes() = R.layout.activity_about

    override fun getToolbarTitle() = R.string.credits



}