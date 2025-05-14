package com.tombat.trackcrypto.crypto.presentation.coin_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tombat.trackcrypto.core.domain.util.onError
import com.tombat.trackcrypto.core.domain.util.onSuccess
import com.tombat.trackcrypto.crypto.domain.CoinDataSource
import com.tombat.trackcrypto.crypto.presentation.coin_detail.DataPoint
import com.tombat.trackcrypto.crypto.presentation.coin_list.components.CoinListState
import com.tombat.trackcrypto.crypto.presentation.model.CoinUI
import com.tombat.trackcrypto.crypto.presentation.model.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
): ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state.asStateFlow()
        .onStart {
            loadCoins()
        }
        .stateIn(
            viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = CoinListState()
        )

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAction(action: CoinListAction){
        when(action){
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coin)
            }
            is CoinListAction.OnRefresh -> loadCoins()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectCoin(coinUi: CoinUI){
        _state.update { it.copy(
            selectedCoin = coinUi
        ) }

        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                   val dataPoints = history
                       .sortedBy { it.dateTime }
                       .map {
                           DataPoint(
                               x = it.dateTime.hour.toFloat(),
                               y = it.priceUsd.toFloat(),
                               xLabel = DateTimeFormatter
                                   .ofPattern("ha\nM/d")
                                   .format(it.dateTime)
                           )
                       }

                    _state.update {
                        it.copy(
                            selectedCoin = it.selectedCoin?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }
                }
                .onError {
                    _events.send(CoinListEvent.Error(it))
                }
        }
    }

    private fun loadCoins(){
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }

            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    _state.update { it.copy(
                        isLoading = false,
                        coins = coins.map { it.toCoinUi() }
                    ) }
                }
                .onError {
                    _state.update { it.copy(
                        isLoading = false
                    ) }
                    _events.send(CoinListEvent.Error(it))
                }
        }
    }
}