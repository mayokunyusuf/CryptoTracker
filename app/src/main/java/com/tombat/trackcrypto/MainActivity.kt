package com.tombat.trackcrypto

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tombat.trackcrypto.core.navigation.AdaptiveCoinListDetailPane
import com.tombat.trackcrypto.core.presentation.util.ObserveAsEvents
import com.tombat.trackcrypto.core.presentation.util.toString
import com.tombat.trackcrypto.crypto.presentation.coin_detail.CoinDetailScreen
import com.tombat.trackcrypto.crypto.presentation.coin_list.CoinListEvent
import com.tombat.trackcrypto.crypto.presentation.coin_list.CoinListViewModel
import com.tombat.trackcrypto.crypto.presentation.coin_list.components.CoinListScreen
import com.tombat.trackcrypto.ui.theme.TrackCryptoTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackCryptoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdaptiveCoinListDetailPane(
                        viewModel = koinViewModel<CoinListViewModel>(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackCryptoTheme {
        Greeting("Android")
    }
}