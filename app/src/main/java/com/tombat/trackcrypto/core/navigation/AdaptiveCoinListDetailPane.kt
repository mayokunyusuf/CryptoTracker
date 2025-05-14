package com.tombat.trackcrypto.core.navigation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tombat.trackcrypto.core.presentation.util.ObserveAsEvents
import com.tombat.trackcrypto.core.presentation.util.toString
import com.tombat.trackcrypto.crypto.presentation.coin_detail.CoinDetailScreen
import com.tombat.trackcrypto.crypto.presentation.coin_list.CoinListAction
import com.tombat.trackcrypto.crypto.presentation.coin_list.CoinListEvent
import com.tombat.trackcrypto.crypto.presentation.coin_list.CoinListViewModel
import com.tombat.trackcrypto.crypto.presentation.coin_list.components.CoinListScreen
import com.tombat.trackcrypto.crypto.presentation.coin_list.components.CoinListState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveCoinListDetailPane (
    viewModel: CoinListViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            is CoinListEvent.Error -> {
                Toast.makeText(context, event.error.toString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    val coroutineScope = rememberCoroutineScope() // ✅ Correct scope

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                CoinListScreen(
                    coinListState = state,
                    onAction = { action ->
                        viewModel.onAction(action)

                        when (action) {
                            is CoinListAction.OnCoinClick -> {
                                coroutineScope.launch { // ✅ Use Compose scope here
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Detail
                                    )
                                }
                            }

                            CoinListAction.OnRefresh -> {
                                // Handle refresh here if needed
                            }
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailScreen(state = state)
            }
        },
        modifier = modifier
    )
}