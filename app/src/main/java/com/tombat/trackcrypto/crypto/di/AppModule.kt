package com.tombat.trackcrypto.crypto.di

import com.tombat.trackcrypto.core.data.networking.HttpClientFactory
import com.tombat.trackcrypto.crypto.data.networking.RemoteCoinDataSource
import com.tombat.trackcrypto.crypto.domain.Coin
import com.tombat.trackcrypto.crypto.domain.CoinDataSource
import com.tombat.trackcrypto.crypto.presentation.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()

    viewModelOf(::CoinListViewModel)
}