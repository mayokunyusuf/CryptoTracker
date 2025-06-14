package com.tombat.trackcrypto.crypto.presentation.coin_detail

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationVector
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tombat.trackcrypto.R
import com.tombat.trackcrypto.crypto.presentation.coin_detail.components.InfoCard
import com.tombat.trackcrypto.crypto.presentation.coin_list.components.CoinListState
import com.tombat.trackcrypto.crypto.presentation.coin_list.components.previewCoin
import com.tombat.trackcrypto.crypto.presentation.model.toDisplayableNumber
import com.tombat.trackcrypto.ui.theme.TrackCryptoTheme
import com.tombat.trackcrypto.ui.theme.greenBackground

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoinDetailScreen (
    state: CoinListState,
    modifier: Modifier = Modifier
){
    val contentColor = if(isSystemInDarkTheme()) {
        Color.Black
    } else {
        Color.White
    }
    if(state.isLoading){
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    } else if (state.selectedCoin != null) {
        val coin = state.selectedCoin
        Column (
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = coin.iconRes
                ),
                contentDescription = coin.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )
            Text (
                text = coin.name,
                color = Color.White,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black
            )
            Text (
                text = coin.symbol,
                color = contentColor,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                InfoCard(
                    title = stringResource(id = R.string.market_cap),
                    formattedText = "$ ${coin.marketCapUsd.formatted}",
                    icon = ImageVector.vectorResource(R.drawable.stock),
                    modifier = Modifier
                )
                InfoCard(
                    title = stringResource(id = R.string.price),
                    formattedText = "$ ${coin.priceUsd.formatted}",
                    icon = ImageVector.vectorResource(R.drawable.dollar),
                    modifier = Modifier
                )
                val absoluteChangeFormatted =
                    (coin.priceUsd.value * (coin.changePercent24Hr.value / 100))
                        .toDisplayableNumber()
                val isPositive = coin.changePercent24Hr.value > 0.0
                val changeColor = if (isPositive) {
                    if (isSystemInDarkTheme()) {
                        Color.Green
                    } else greenBackground
                    Color.Green
                } else {
                    MaterialTheme.colorScheme.error
                }
                InfoCard(
                    title = stringResource(id = R.string.change_last_24h),
                    formattedText = absoluteChangeFormatted.formatted,
                    icon = if(isPositive) {
                        ImageVector.vectorResource(id = R.drawable.trending)
                    } else {
                        ImageVector.vectorResource(id = R.drawable.trending_down)
                    }
                )
            }
            AnimatedVisibility(
                visible = coin.coinPriceHistory.isNotEmpty()
            ) {
                var selectedDataPoint by remember {
                    mutableStateOf<DataPoint?>(null)
                }
                var labelWidth by remember {
                    mutableStateOf(0f)
                }
                var totalChartWidth by remember {
                    mutableStateOf(0f)
                }
                val amountOfVisibleDataPoints = if(labelWidth > 0){
                    ((totalChartWidth - 2.5 * labelWidth) / labelWidth).toInt()
                } else {
                    0
                }
                val startIndex = (coin.coinPriceHistory.lastIndex - amountOfVisibleDataPoints)
                    .coerceAtLeast(0)
                LineChart(
                    dataPoints = coin.coinPriceHistory,
                    style = ChartStyle(
                        chartLineColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.secondary,
                        selectedColor = MaterialTheme.colorScheme.primary,
                        helperLinesThicknessPx = 5f,
                        axisLineThicknessPx = 5f,
                        labelFontSize = 14.sp,
                        minYLabelSpacing = 25.dp,
                        verticalPadding = 10.dp,
                        horizontalPadding = 10.dp,
                        axisLabelSpacing = 10.dp
                    ),
                    visibleDataPointsIndices = startIndex..coin.coinPriceHistory.lastIndex,
                    unit = "$",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .onSizeChanged { totalChartWidth = it.width.toFloat() },
                    selectedDataPoint = selectedDataPoint,
                    onSelectedDataPoint =  {
                        selectedDataPoint = it
                    },
                    onXLabelWidthChange = {
                        labelWidth = it
                    }
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun CoinDetailScreenPreview() {
    TrackCryptoTheme {
        CoinDetailScreen(
            state = CoinListState (
                selectedCoin = previewCoin
            ),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.background
            )
        )
    }
}









