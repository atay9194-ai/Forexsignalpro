
package com.forexsignalpro

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.Instant
import java.util.concurrent.TimeUnit

class BiquoteRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val base = "https://biquote.io/api"

    val pairs = listOf(
        "EURUSD", "GBPUSD", "USDJPY", "USDCHF",
        "AUDUSD", "USDCAD", "NZDUSD"
    )

    suspend fun getCandles(
        pair: String,
        interval: String
    ): List<Candle> = withContext(Dispatchers.IO) {

        require(pair in pairs)
        require(interval in listOf("15m", "1h", "4h"))

        val url = "$base/$pair/ohlc?interval=$interval&limit=150"

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->

            check(response.isSuccessful) {
                "Market service error: ${response.code}"
            }

            val json = JSONObject(
                response.body?.string()
                    ?: error("Empty response")
            )

            val bars = json.getJSONArray("bars")
            val result = mutableListOf<Candle>()

            for (i in 0 until bars.length()) {
                val bar = bars.getJSONObject(i)

                result.add(
                    Candle(
                        open = bar.getDouble("open"),
                        high = bar.getDouble("high"),
                        low = bar.getDouble("low"),
                        close = bar.getDouble("close"),
                        timestamp = Instant.parse(
                            bar.getString("openTime")
                        ).toEpochMilli(),
                        complete = !bar.optBoolean(
                            "isOpen", false
                        )
                    )
                )
            }

            result.sortedBy { it.timestamp }
        }
    }
}
