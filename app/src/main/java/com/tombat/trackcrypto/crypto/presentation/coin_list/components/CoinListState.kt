package com.tombat.trackcrypto.crypto.presentation.coin_list.components

import androidx.compose.runtime.Immutable
import com.tombat.trackcrypto.crypto.presentation.model.CoinUI

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUI> = emptyList(),
    val selectedCoin: CoinUI? = null
)
