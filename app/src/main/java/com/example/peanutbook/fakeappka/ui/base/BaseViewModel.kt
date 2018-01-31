package com.example.peanutbook.fakeappka.ui.base

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.peanutbook.fakeappka.app

/**
 * Base ViewModel class
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseViewModel<C: BaseController> : AndroidViewModel(app()) {

    private var mController: C? = null
    private var mViewModelCreated = false
    private var mDoOnAttached = mutableListOf<DoOnAttachedAction>()

    fun onViewModelReady(controller: C) {
        if (!mViewModelCreated) {
            mViewModelCreated = true
            onViewModelCreated()
        }
        mController = controller
        onViewAttached()
    }

    open fun onViewAttached() {
        mDoOnAttached.forEach {
            it.action.invoke()
            mDoOnAttached.remove(it)
        }
    }

    open fun onViewModelCreated() {
        // Triggered when the ViewModel is created
    }

    fun doOnAttached(name: String? = "UNSPECIFIED", doOnAttachedAction: () -> Unit) {
        mDoOnAttached.add(DoOnAttachedAction(doOnAttachedAction, name))
    }

    protected fun getController() = checkNotNull(mController, {"Controller is not available"})

}

class ViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass!!.newInstance()
    }
}

data class DoOnAttachedAction(val action: () -> Unit, val name: String? = null)