package com.example.peanutbook.fakeappka.firebase

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

/**
 * Utilities related to RX and Database.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun DatabaseQuery.observe(): Flowable<Optional<DataSnapshot>> {
    return Flowable.create<Optional<DataSnapshot>>({
        val query = this.build()
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                val snap: Optional<DataSnapshot> = None
                it.onNext(snap) // Data no longer exist or permission denied
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                it.onNext(dataSnapshot.toOptional())
            }
        })
        it.setCancellable {
            query.removeEventListener(listener)
        }
    }, BackpressureStrategy.BUFFER)
            .observeOn(Schedulers.computation())
}

fun DatabaseQuery.exists(): Single<Optional<Boolean>> {
    return Single.create({ emitter: SingleEmitter<Optional<Boolean>> ->
        val query = this.build()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                emitter.onSuccess(false.toOptional())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exists = dataSnapshot.value != null
                emitter.onSuccess(exists.toOptional())
            }
        })
    })
            .observeOn(Schedulers.computation())
}

