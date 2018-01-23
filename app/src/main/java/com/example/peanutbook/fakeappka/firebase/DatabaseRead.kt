package com.example.peanutbook.fakeappka.firebase

import android.provider.ContactsContract
import com.example.peanutbook.fakeappka.model.Website
import rx.Observable

/**
 * Created by PeanutBook on 19.01.2018.
 */


object DatabaseRead {

    // Users:

    fun website(): Observable<Website?> = DatabaseQuery().apply { path = "websites/" }
            .observe()
            .toObjectObservable(Website::class.java)


    fun websites(): Observable<List<Website>?> {
        return DatabaseQuery().apply { path = "websites/"; orderByChild = "order" }
                .observe()
                .toListObservable(Website::class.java)
    }
}
