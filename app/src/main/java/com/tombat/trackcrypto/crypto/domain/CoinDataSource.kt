package com.tombat.trackcrypto.crypto.domain

import com.tombat.trackcrypto.core.domain.util.NetworkError
import com.tombat.trackcrypto.core.domain.util.Result
import java.time.ZonedDateTime

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>
    suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError>
}