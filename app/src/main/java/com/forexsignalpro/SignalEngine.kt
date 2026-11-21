
package com.forexsignalpro

import kotlin.math.abs

data class Candle(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val timestamp: Long,
    val complete: Boolean = true
)

data class SignalResult(
    val pair: String,
    val decision: String,
    val entry: Double?,
    val stopLoss: Double?,
    val takeProfit: Double?,
    val confidence: Int,
    val reason: String
)

object SignalEngine {

    private fun ema(
        values: List<Double>,
        period: Int
    ): Double? {
        if (values.size < period) return null

        val multiplier = 2.0 / (period + 1)
        var result = values.take(period).average()

        for (value in values.drop(period)) {
            result = value * multiplier +
                result * (1 - multiplier)
        }
        return result
    }

    private fun rsi(values: List<Double>): Double? {
        if (values.size < 15) return null

        val changes = values.zipWithNext { a, b -> b - a }
        val recent = changes.takeLast(14)

        val gains = recent.filter { it > 0 }.sum() / 14
        val losses = recent.filter { it < 0 }.sum() / 14

        if (losses == 0.0) return 100.0

        val rs = gains / losses
        return 100 - (100 / (1 + rs))
    }

    private fun atr(candles: List<Candle>): Double? {
        if (candles.size < 15) return null

        val ranges = candles.zipWithNext { previous, current ->
            maxOf(
                current.high - current.low,
                abs(current.high - previous.close),
                abs(current.low - previous.close)
            )
        }

        return ranges.takeLast(14).average()
    }

    private fun trend(candles: List<Candle>): Int {
        if (candles.size < 100) return 0

        val closes = candles.map { it.close }

        val fast = ema(closes, 20) ?: return 0
        val medium = ema(closes, 50) ?: return 0
        val slow = ema(closes, 100) ?: return 0
        val momentum = rsi(closes) ?: return 0

        return when {
            fast > medium && medium > slow &&
                momentum in 52.0..68.0 -> 1

            fast < medium && medium < slow &&
                momentum in 32.0..48.0 -> -1

            else -> 0
        }
    }

    fun analyse(
        pair: String,
        candles15m: List<Candle>,
        candles1h: List<Candle>,
        candles4h: List<Candle>
    ): SignalResult {

        val datasets = listOf(
            candles15m, candles1h, candles4h
        )

        if (datasets.any { data ->
            data.size < 100 ||
                data.any { !it.complete || it.close <= 0 }
        }) {
            return wait(pair, "Insufficient market data")
        }

        val directions = datasets.map { trend(it) }
        val volatility = atr(candles15m) ?: return wait(
            pair, "ATR unavailable"
        )

        val price = candles15m.last().close

        if (volatility <= 0 || price <= 0) {
            return wait(pair, "Invalid market price")
        }

        if (directions.all { it == 1 }) {
            return SignalResult(
                pair, "BUY", price,
                price - 2 * volatility,
                price + 3 * volatility,
                75,
                "Three timeframe bullish confirmation"
            )
        }

        if (directions.all { it == -1 }) {
            return SignalResult(
                pair, "SELL", price,
                price + 2 * volatility,
                price - 3 * volatility,
                75,
                "Three timeframe bearish confirmation"
            )
        }

        return wait(pair, "Timeframes disagree")
    }

    private fun wait(
        pair: String,
        reason: String
    ) = SignalResult(
        pair, "WAIT", null, null, null, 0, reason
    )
}
