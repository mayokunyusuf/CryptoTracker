package com.tombat.trackcrypto.crypto.presentation.coin_list

import com.tombat.trackcrypto.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}