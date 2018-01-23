package com.example.peanutbook.fakeappka.firebase

import com.google.firebase.database.FirebaseDatabase
import java.math.BigDecimal

/**
 * Main access point to Firebase Real-time Database. Handles initialization.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
object Database {

    private var mInitialized = false

    fun get(): FirebaseDatabase {
        val db = FirebaseDatabase.getInstance()
        if (!mInitialized) {
            db.setPersistenceEnabled(true)
            mInitialized = true
            //sync()
        }
        return db
    }

    fun connect() {
        get().goOnline()
    }

    fun disconnect() = get().goOffline()

    fun stopSync(groupId: String) {
        syncGroup(groupId, false)
    }

    private fun syncGroup(groupId: String, synced: Boolean) {
        keepSynced("groups/$groupId", synced)
        keepSynced("members/$groupId", synced)
        keepSynced("transactions/$groupId", synced)
        keepSynced("changes/$groupId", synced)
        keepSynced("permissions/$groupId", synced)
    }

    private fun keepSynced(path: String, synced: Boolean) {
        get().reference.child(path).keepSynced(synced)
    }
}

private var gLatestExchangeRates: Map<String, BigDecimal> = mapOf()

fun latestExchangeRates(): Map<String, BigDecimal> = gLatestExchangeRates
