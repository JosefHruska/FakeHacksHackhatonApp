package com.example.peanutbook.fakeappka.extensions

import com.example.peanutbook.fakeappka.model.Website

/**
 * Model extensions
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

// value of ids where they are not known (sometimes is better to create instance with unknown id than a nullable instance)
const val UNKNOWN = "UNKNOWN"

// value of fields which are required for models. If model contains this value, it's treated as null
const val UNDEFINED = "UnDeF"

fun List<Website>.findByName(url: String?) = this.find { it.getId().substringBefore("_").replace("-", ".") == url }