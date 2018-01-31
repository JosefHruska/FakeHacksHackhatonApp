package com.example.peanutbook.fakeappka.extensions

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional

/**
 * Various utils related to nullable stuff.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun <T: Any> Optional<T>.isNullOrNone(): Boolean {
    return (this == None || this.toNullable() == null) // Use only for already checked Optional
}

fun <T : Any> Optional<T>.toSome(): T {
    return this.toNullable()!!
}