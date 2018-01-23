package com.example.peanutbook.fakeappka.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.database.Exclude

/**
 * Created by PeanutBook on 19.01.2018.
 */
abstract class DatabaseModel {

    @Exclude
    @JsonIgnore
    private var id: String? = null // It needs to be nullable for writing new data to database

    @Exclude
    @JsonIgnore
    fun getId() = checkNotNull(id, { "Missing id for ${this.javaClass.name}" })

    fun setId(key: String) {
        id = key
    }

    fun hasId() = id != null

    override fun equals(other: Any?): Boolean {
        if (other is DatabaseModel) {
            return id == other.id
        }
        return super.equals(other)
    }

    override fun hashCode() = getId().hashCode()

    @Suppress("UNCHECKED_CAST")
    fun toMap() =
            ObjectMapper().convertValue(this, Map::class.java) as Map<String, Any>

    @Exclude
    @JsonIgnore
    open fun requiredProperties(): List<String> = emptyList()

    @Exclude
    @JsonIgnore
    open fun hasRequiredProperties() = true
}