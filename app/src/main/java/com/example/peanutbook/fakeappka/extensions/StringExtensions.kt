package com.example.peanutbook.fakeappka.extensions

/**
 * String extensions
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun String.formatSiteName() = replace("-", ".").substringBefore("_").replace("___TECKA___", ".")

fun String.formatSiteRating() = replace("-", ".").substringAfter("_")
