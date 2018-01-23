package com.example.peanutbook.fakeappka

/**
 * Created by PeanutBook on 23.01.2018.
 */

fun String.formatSiteName() = replace("-", ".").substringBefore("_").replace("___TECKA___", ".")

fun String.formatSiteRating() = replace("-", ".").substringAfter("_")
