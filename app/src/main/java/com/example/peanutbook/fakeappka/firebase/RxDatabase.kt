package com.example.peanutbook.fakeappka.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Utilities related to RX and Database.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun DatabaseQuery.observe(): Observable<DataSnapshot?> {
    return rx.Observable.create<DataSnapshot?>({
        val query = this.build()
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                it.onNext(null) // Data no longer exist or permission denied
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                it.onNext(dataSnapshot)
            }
        })
        it.setCancellation {
            query.removeEventListener(listener)
        }
    }, rx.Emitter.BackpressureMode.BUFFER)
            .observeOn(rx.schedulers.Schedulers.computation())
}

fun DatabaseQuery.observeOnce(): Observable<DataSnapshot?> {
    return rx.Observable.create<DataSnapshot?>({
        val query = this.build()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                it.onNext(null) // Data no longer exist or permission denied
                it.onCompleted()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                it.onNext(dataSnapshot)
                it.onCompleted()
            }
        })
    }, rx.Emitter.BackpressureMode.BUFFER)
            .observeOn(rx.schedulers.Schedulers.computation())
}

fun DatabaseQuery.exists(): Observable<Boolean?> = rx.Observable.create<Boolean>({
    val query = this.build()
    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            it.onNext(false)
            it.onCompleted()
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            it.onNext(dataSnapshot.value != null)
            it.onCompleted()
        }
    })
}, rx.Emitter.BackpressureMode.BUFFER)
        .observeOn(rx.schedulers.Schedulers.computation())

fun DatabaseQuery.observeCountOnce(): Observable<Long> = rx.Observable.create<Long>({
    val query = this.build()
    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            it.onNext(0)
            it.onCompleted()
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            it.onNext(dataSnapshot.childrenCount)
            it.onCompleted()
        }
    })
}, rx.Emitter.BackpressureMode.BUFFER)
        .observeOn(rx.schedulers.Schedulers.computation())

inline fun <T> Observable<T>.subscribeOnce(crossinline success: (T) -> Unit, crossinline error: (Throwable) -> Unit): Subscription =
        take(1) // Automatically unsubscribe after first value
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ success(it) }, { error(it) })

inline fun <T> Observable<T>.subscribeTwice(crossinline success: (T) -> Unit, crossinline error: (Throwable) -> Unit): Subscription =
        take(2) // Automatically unsubscribe after second value
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ success(it) }, { error(it) })

/**
 * Source: http://stackoverflow.com/questions/32292509/rxjava-consolidating-multiple-infinite-observablelistt
 */
inline fun <A, B> Observable<List<A>?>.mapSubQueries(crossinline subQuery: (A) -> Observable<B>): Observable<List<Observable<B>>> =
        this.flatMap {
            if (it != null && it.isNotEmpty()) {
                return@flatMap Observable.from(it).map { subQuery(it) }.toList()
            } else {
                return@flatMap Observable.just(listOf<Observable<B>>())
            }
        }

@Suppress("UNCHECKED_CAST")
fun <T> Observable<List<Observable<T?>>>.joinSubQueries(): Observable<List<T>?> = this.flatMap {
    if (it.isNotEmpty()) {
        return@flatMap Observable.combineLatest(it, {
            val list = mutableListOf<T>()
            it.forEach {
                if (it != null) {
                    list.add(it as T)
                }
            }
            list
        })
    } else {
        return@flatMap Observable.just(listOf<T>())
    }
}

inline fun <T1, T2, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, crossinline merge: (T1, T2) -> R, nullValue: R? = null): Observable<R?> =
        Observable.combineLatest(observable1, observable2) { value1, value2 ->
            var result: R? = null
            if (value1 != null && value2 != null) {
                result = merge(value1, value2)
            } else if (nullValue != null) {
                result = nullValue
            }
            result
        }

inline fun <T1, T2, T3, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, crossinline merge: (T1, T2, T3) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3) { value1, value2, value3 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null) {
                result = merge(value1, value2, value3)
            }
            result
        }

inline fun <T1, T2, T3, R> combineLatestValue3Nullable(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, crossinline merge: (T1, T2, T3?) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3) { value1, value2, value3 ->
            var result: R? = null
            if (value1 != null && value2 != null) {
                result = merge(value1, value2, value3)
            }
            result
        }

inline fun <T1, T2, T3, T4, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, crossinline merge: (T1, T2, T3, T4) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3, observable4) { value1, value2, value3, value4 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null && value4 != null) {
                result = merge(value1, value2, value3, value4)
            }
            result
        }

inline fun <T1, T2, T3, T4, T5, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, crossinline merge: (T1, T2, T3, T4, T5) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3, observable4, observable5) { value1, value2, value3, value4, value5 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null) {
                result = merge(value1, value2, value3, value4, value5)
            }
            result
        }

inline fun <T1, T2, T3, T4, T5, T6, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, crossinline merge: (T1, T2, T3, T4, T5, T6) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6) { value1, value2, value3, value4, value5, value6 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null) {
                result = merge(value1, value2, value3, value4, value5, value6)
            }
            result
        }

inline fun <T1, T2, T3, T4, T5, T6, T7, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, observable7: Observable<T7?>, crossinline merge: (T1, T2, T3, T4, T5, T6, T7) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6, observable7) { value1, value2, value3, value4, value5, value6, value7 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null && value7 != null) {
                result = merge(value1, value2, value3, value4, value5, value6, value7)
            }
            result
        }

inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, observable7: Observable<T7?>, observable8: Observable<T8?>, crossinline merge: (T1, T2, T3, T4, T5, T6, T7, T8) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6, observable7, observable8) { value1, value2, value3, value4, value5, value6, value7, value8 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null && value7 != null && value8 != null) {
                result = merge(value1, value2, value3, value4, value5, value6, value7, value8)
            }
            result
        }

inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, observable7: Observable<T7?>, observable8: Observable<T8?>, observable9: Observable<T9?>, crossinline merge: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R): Observable<R?> =
        Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6, observable7, observable8, observable9) { value1, value2, value3, value4, value5, value6, value7, value8, value9 ->
            var result: R? = null
            if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null && value7 != null && value8 != null && value9 != null) {
                result = merge(value1, value2, value3, value4, value5, value6, value7, value8, value9)
            }
            result
        }