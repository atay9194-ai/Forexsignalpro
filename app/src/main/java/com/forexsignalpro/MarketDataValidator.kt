
package com.forexsignalpro

object MarketDataValidator {

    fun validateCandles(
        candles: List<Candle>,
        minimum: Int = 100
    ): String? {

        if (candles.size < minimum)
            return "Insufficient candle history"

        if (candles.any { !it.complete })
            return "Incomplete candle data"

        if (candles.any {
            val values = listOf(
                it.open, it.high, it.low, it.close
            )

            values.any { v -> !v.isFinite() || v <= 0 } ||
            it.high < maxOf(it.open, it.close, it.low) ||
            it.low > minOf(it.open, it.close, it.high)
        }) return "Invalid price data"

        if (candles.zipWithNext().any {
            it.first.timestamp >= it.second.timestamp
        }) return "Invalid candle order"

        return null
    }
}
