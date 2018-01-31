package com.example.peanutbook.fakeappka.extensions

/**
 * String extensions
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun String.formatSiteName() = replace("-", ".").substringBefore("_").replace("___TECKA___", ".")

fun String.formatSiteRating() = replace("-", ".").substringAfter("_")

fun String.parseUrl(): String {
    var url = this
    if (url.contains("http://")) {
        url = url.substringAfter("http://")
    } else if (url.contains("https://")) {
        url = url.substringAfter("https://")
    }

    url = url.substringBefore("/")
    val sequence = url.split(".").takeLast(2)
    return sequence[0] + "." + sequence[1]
}
