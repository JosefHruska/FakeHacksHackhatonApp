package com.example.peanutbook.fakeappka.firebase

import com.example.peanutbook.fakeappka.model.Website
import com.gojuno.koptional.Optional
import io.reactivex.Flowable

/**
 * Methods for simple reading in Firebase Database.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object DatabaseRead {

    fun websites(): Flowable<Optional<List<Website>>> {
        return DatabaseQuery().apply { path = "websites/"; orderByChild = "order" }
                .observe()
                .toListObservable(Website::class.java)
    }
}
