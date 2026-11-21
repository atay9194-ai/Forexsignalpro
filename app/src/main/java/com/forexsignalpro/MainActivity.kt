
package com.forexsignalpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF101D2D)
private val CardBlue = Color(0xFF20364A)
private val Yellow = Color(0xFFE9BD57)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ForexDashboard() }
    }
}

@Composable
fun ForexDashboard() {
    val pairs = listOf(
        "EUR/USD", "GBP/USD", "USD/JPY",
        "USD/CHF", "AUD/USD", "USD/CAD", "NZD/USD"
    )

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy)
                .padding(18.dp)
        ) {
            Text(
                "FOREX SIGNAL PRO",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "FREE EDITION • CONSERVATIVE",
                color = Yellow,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Market Dashboard",
                color = Color.White,
                fontSize = 22.sp
            )

            Text(
                "15M  |  1H  |  4H",
                color = Color.LightGray
            )

            Spacer(Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pairs) { pair ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = CardBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Text(
                                pair,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "WAIT",
                                color = Yellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Text(
                "Scanning frequency: Every 5 minutes",
                color = Color.White,
                fontSize = 13.sp
            )

            Text(
                "Market feed not connected yet",
                color = Yellow,
                fontSize = 12.sp
            )
        }
    }
}
