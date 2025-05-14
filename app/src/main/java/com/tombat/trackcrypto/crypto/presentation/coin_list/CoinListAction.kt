package com.tombat.trackcrypto.crypto.presentation.coin_list

import com.tombat.trackcrypto.crypto.presentation.model.CoinUI

sealed interface CoinListAction {
    data class OnCoinClick(val coin: CoinUI): CoinListAction
    data object OnRefresh: CoinListAction
}