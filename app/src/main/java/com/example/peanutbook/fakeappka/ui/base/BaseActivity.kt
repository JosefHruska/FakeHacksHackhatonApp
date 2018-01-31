package com.example.peanutbook.fakeappka.ui.base

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.peanutbook.fakeappka.R
import com.example.peanutbook.fakeappka.app

/**
 * Base activity for all child activities
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseActivity<VM: BaseViewModel<C>, C: BaseController>: AppCompatActivity(), BaseController {

    protected lateinit var mViewModel: VM

    abstract fun getViewModel(): VM

    abstract fun getLayoutRes(): Int

    abstract fun getToolbarTitle(): Int

    open fun getOptionsMenuRes() = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        setupViewModel()
        setupToolbar()
        initUi()
    }

    open fun initUi() {
        // setup UI components here
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, ViewModelFactory()).get((getViewModel()::class.java))
        mViewModel.onViewModelReady(this as C)
    }

    open fun showCloseButton() = false

    @SuppressLint("PrivateResource")
    open fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.vToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = app().getString(getToolbarTitle())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getOptionsMenuRes()
        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }
}