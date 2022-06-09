package com.fankonnect.app.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    suspend fun <T> hitApiCall(apiCall: suspend () -> T): T {

        return withContext(Dispatchers.IO) {
            apiCall.invoke()
        }
    }
}