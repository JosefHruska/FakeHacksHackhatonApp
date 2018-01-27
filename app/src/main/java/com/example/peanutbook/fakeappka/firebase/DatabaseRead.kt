package com.example.peanutbook.fakeappka.firebase

import com.example.peanutbook.fakeappka.model.Website
import rx.Observable

/**
 * Methods for simple reading in Firebase Database.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object DatabaseRead {

    fun websites(): Observable<List<Website>?> {
        return DatabaseQuery().apply { path = "websites/"; orderByChild = "order" }
                .observe()
                .toListObservable(Website::class.java)
    }
}
