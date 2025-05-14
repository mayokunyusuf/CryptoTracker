package com.tombat.trackcrypto.crypto.data.networking

import android.os.Build
import androidx.annotation.RequiresApi
import com.tombat.trackcrypto.core.data.networking.constructUrl
import com.tombat.trackcrypto.core.data.networking.safeCall
import com.tombat.trackcrypto.core.domain.util.NetworkError
import com.tombat.trackcrypto.core.domain.util.Result
import com.tombat.trackcrypto.core.domain.util.map
import com.tombat.trackcrypto.crypto.data.mappers.toCoin
import com.tombat.trackcrypto.crypto.data.mappers.toCoinPrice
import com.tombat.trackcrypto.crypto.data.networking.dto.CoinHistoryDto
import com.tombat.trackcrypto.crypto.data.networking.dto.CoinResponseDto
import com.tombat.trackcrypto.crypto.domain.Coin
import com.tombat.trackcrypto.crypto.domain.CoinDataSource
import com.tombat.trackcrypto.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
): CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets?apiKey=d958d23b5aefa1a4f88d4fb3a7dffb3761fd7fff7476e6f0f917a7fb7195bc75")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start
            .withZoneSameInstant(java.time.ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val endMillis = end
            .withZoneSameInstant(java.time.ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = "https://rest.coincap.io/v3/assets/$coinId/history"
            ) {
                parameter("interval", "h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
                header("Authorization", "Bearer d958d23b5aefa1a4f88d4fb3a7dffb3761fd7fff7476e6f0f917a7fb7195bc75")
            }
        }
            .map{
            response -> response.data.map { it.toCoinPrice() }
        }
    }
}