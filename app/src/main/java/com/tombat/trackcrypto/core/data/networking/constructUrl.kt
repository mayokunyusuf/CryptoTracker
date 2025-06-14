package com.tombat.trackcrypto.core.data.networking

import com.tombat.trackcrypto.BuildConfig
import io.ktor.http.Url

fun constructUrl(url: String) : String {
    return when {
        url.contains(BuildConfig.BASE_URL) -> url
        url.startsWith("/") -> BuildConfig.BASE_URL + url.drop(1)
        else -> BuildConfig.BASE_URL + url
    }
}