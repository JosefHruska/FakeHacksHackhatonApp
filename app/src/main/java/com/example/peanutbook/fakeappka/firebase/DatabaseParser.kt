package com.example.peanutbook.fakeappka.firebase

import com.example.peanutbook.fakeappka.model.DatabaseModel
import com.google.firebase.database.DataSnapshot
import rx.Observable
import java.math.BigDecimal


fun <T : DatabaseModel> Observable<DataSnapshot?>.toObjectObservable(type: Class<T>): Observable<T?> {
    return this.map {
        if (it == null) {
            return@map null
        }
        val data = it.getValue(type)
        data?.setId(it.key)
        if (data?.hasRequiredProperties() == true) {
            return@map data
        } else {
            return@map null
        }
    }
}

fun <T : DatabaseModel> Observable<DataSnapshot?>.toListObservable(type: Class<T>): Observable<List<T>?> {
    return this.map {
        if (it == null) {
            return@map null
        }
        it.children.map {
            val data = checkNotNull(it.getValue(type), { "Non-existing db path" })
            data.setId(it.key)
            data
        }.filter { it.hasRequiredProperties() }
    }
}

fun Observable<DataSnapshot?>.toCountObservable(): Observable<Int?> {
    return this.map {
        if (it == null) {
            return@map 0
        }
        it.childrenCount.toInt()
    }
}

fun <T> Observable<DataSnapshot?>.toPrimitiveObservable(type: Class<T>): Observable<T?> {
    return this.map {
        if (it == null) {
            return@map null
        }
        it.getValue(type)
    }
}

