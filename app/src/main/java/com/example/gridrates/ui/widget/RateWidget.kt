package com.example.gridrates.ui.widget

import android.content.Context
import androidx.compose.foundation.background as composeBackground
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background as glanceBackground
import androidx.glance.currentState
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Box as GlanceBox
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.fillMaxSize as glanceFillMaxSize
import androidx.glance.layout.padding as glancePadding
import androidx.glance.text.FontWeight as GlanceFontWeight
import androidx.glance.text.Text as GlanceText
import androidx.glance.text.TextStyle as GlanceTextStyle
import androidx.glance.state.PreferencesGlanceStateDefinition

object RateWidgetKeys {
    val RATE = doublePreferencesKey("current_rate")
    val LABEL = stringPreferencesKey("current_label")
}

class RateWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
                val rate = prefs[RateWidgetKeys.RATE] ?: 0.0
                val label = prefs[RateWidgetKeys.LABEL].orEmpty().ifBlank { "Loading..." }
                WidgetContent(rate, label)
            }
        }
    }

    @Composable
    internal fun WidgetContent(rate: Double, label: String) {
        val color = when (label) {
            "Peak" -> Color(0xFFB00020)
            "Off-Peak" -> Color(0xFF8F99E0)
            "Shoulder" -> Color(0xFFB4C5FF)
            "Super Off-Peak" -> Color(0xFF7B1FA2)
            else -> Color(0xFF1B1D2A)
        }

        val onColor = GlanceTheme.colors.onPrimaryContainer
        val borderColor = color.value.toInt()
        val innerBackgroundColor = Color.Black.value.toInt()

        GlanceBox(
            modifier = GlanceModifier
                .glanceFillMaxSize()
                .glanceBackground(borderColor)
                .glancePadding(2.dp),
            contentAlignment = GlanceAlignment.Center
        ) {
            GlanceBox(
                modifier = GlanceModifier
                    .glanceFillMaxSize()
                    .glanceBackground(innerBackgroundColor)
                    .glancePadding(10.dp),
                contentAlignment = GlanceAlignment.Center
            ) {
                GlanceColumn(horizontalAlignment = GlanceAlignment.CenterHorizontally) {
                    GlanceText(
                        text = label,
                        style = GlanceTextStyle(
                            fontSize = 8.sp,
                            fontWeight = GlanceFontWeight.Bold,
                            color = onColor
                        )
                    )
                    GlanceText(
                        text = rate.toString(),
                        style = GlanceTextStyle(
                            fontSize = 14.sp,
                            fontWeight = GlanceFontWeight.Bold,
                            color = onColor
                        )
                    )
                    GlanceText(
                        text = "$ /kWh",
                        style = GlanceTextStyle(
                            fontSize = 8.sp,
                            color = onColor
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 160, heightDp = 100)
@Composable
fun WidgetContentPreview() {
    val sampleLabel = "Off-Peak"
    val sampleRate = 0.23
    val color = Color(0xFF8F99E0)

    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 100.dp)
            .clip(RoundedCornerShape(16.dp))
            .composeBackground(color)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .composeBackground(Color.Black)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.Text(
                    text = sampleLabel,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.Text(
                    text = sampleRate.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                androidx.compose.material3.Text(
                    text = "$/kWh",
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 1f)
                )
            }
        }
    }
}
