package com.example.gridrates.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gridrates.data.local.entity.RateSchedule
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.compose.ui.tooling.preview.Preview
import com.example.gridrates.data.local.entity.DayType
import com.example.gridrates.ui.theme.GridRatesTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSettings: () -> Unit
) {
    val provider by viewModel.selectedProvider.collectAsStateWithLifecycle()
    val plan by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val currentRate by viewModel.currentActiveRate.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
    val dailySchedules by viewModel.dailySchedules.collectAsStateWithLifecycle()

    var previewTime by remember { mutableStateOf<LocalTime?>(null) }

    val displayRate = remember(previewTime, currentRate, dailySchedules) {
        if (previewTime != null) {
            dailySchedules.find { 
                val start = it.startTime.toSecondOfDay()
                val end = if (it.endTime == LocalTime.MIDNIGHT) 24 * 60 * 60 else it.endTime.toSecondOfDay()
                val now = previewTime!!.toSecondOfDay()
                now >= start && now < end
            }
        } else {
            currentRate
        }
    }

    val displayTime = remember(previewTime, currentTime) {
        if (previewTime != null) {
            currentTime.with(previewTime)
        } else {
            currentTime
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Column {
                        Text(
                            text = provider?.name ?: "Loading...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plan?.name ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    if (previewTime != null) {
                        IconButton(onClick = { previewTime = null }) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Reset to now"
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(innerPadding),
            currentRate = displayRate,
            currentTime = displayTime,
            dailySchedules = dailySchedules,
            isPreview = previewTime != null,
            previewTime = previewTime ?: currentTime.toLocalTime(),
            onPreviewTimeChange = { previewTime = it }
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    currentRate: RateSchedule?,
    currentTime: LocalDateTime,
    dailySchedules: List<RateSchedule>,
    isPreview: Boolean = false,
    previewTime: LocalTime,
    onPreviewTimeChange: (LocalTime) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600

    if (isWideScreen) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CurrentRateCard(currentRate, currentTime, isPreview)
            }
            Box(modifier = Modifier.weight(1f)) {
                TimelineCard(dailySchedules, previewTime, onPreviewTimeChange)
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                CurrentRateCard(currentRate, currentTime, isPreview)
            }
            item {
                TimelineCard(dailySchedules, previewTime, onPreviewTimeChange)
            }
        }
    }
}

@Composable
fun CurrentRateCard(rateSchedule: RateSchedule?, currentTime: LocalDateTime, isPreview: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            if (isPreview) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = "PREVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 62.sp,
                    letterSpacing = (-2).sp
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    text = rateSchedule?.label ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary, // Light blue text for label
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format(Locale.US, "%.2f", rateSchedule?.rate ?: 0.0),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 74.sp,
                        letterSpacing = (-4).sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.padding(bottom = 32.dp, start = 12.dp),
                    text = "$/kWh",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TimelineCard(
    schedules: List<RateSchedule>,
    displayTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Rate Timeline",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Drag or tap to preview rates",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            
            TimelineView(schedules, displayTime, onTimeChange)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimelineView(
    schedules: List<RateSchedule>,
    displayTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val colorMap = mapOf(
        "Peak" to MaterialTheme.colorScheme.error,
        "Off-Peak" to Color(0xFF8F99E0), // Matching screenshot Blue
        "Shoulder" to Color(0xFFB4C5FF),
        "Super Off-Peak" to Color(0xFF512598) // Matching screenshot Pink
    )
    val indicatorColor = MaterialTheme.colorScheme.onSurface
    val tickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    val fallbackColor = MaterialTheme.colorScheme.outlineVariant

    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Increased height for better interaction
                    .clip(RoundedCornerShape(40.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val timeSeconds = (offset.x / size.width) * (24 * 60 * 60)
                            onTimeChange(LocalTime.ofSecondOfDay(timeSeconds.toLong().coerceIn(0, 86399)))
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val timeSeconds = (change.position.x / size.width) * (24 * 60 * 60)
                            onTimeChange(LocalTime.ofSecondOfDay(timeSeconds.toLong().coerceIn(0, 86399)))
                        }
                    }
            ) {
                val totalSeconds = 24 * 60 * 60f
                
                schedules.forEach { schedule ->
                    val start = schedule.startTime.toSecondOfDay().toFloat() / totalSeconds
                    val endSeconds = if (schedule.endTime == LocalTime.MIDNIGHT) {
                        24 * 60 * 60f
                    } else {
                        schedule.endTime.toSecondOfDay().toFloat()
                    }
                    val end = endSeconds / totalSeconds
                    
                    drawRect(
                        color = colorMap[schedule.label] ?: fallbackColor,
                        topLeft = Offset(x = start * size.width, y = 0f),
                        size = androidx.compose.ui.geometry.Size(width = (end - start) * size.width, height = size.height)
                    )
                }

                // Draw ticks for every 6 hours
                for (i in (0..24) step 6) {
                    val pos = i / 24f
                    drawLine(
                        color = tickColor,
                        start = Offset(x = pos * size.width, y = 0f),
                        end = Offset(x = pos * size.width, y = size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                
                // Draw current/preview indicator
                val indicatorPos = displayTime.toSecondOfDay().toFloat() / totalSeconds
                drawLine(
                    color = indicatorColor,
                    start = Offset(x = indicatorPos * size.width, y = 0f),
                    end = Offset(x = indicatorPos * size.width, y = size.height),
                    strokeWidth = 6.dp.toPx()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("12 AM", "6 AM", "12 PM", "6 PM", "12 AM").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Legend
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            schedules.map { it.label }.distinct().forEach { label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colorMap[label] ?: fallbackColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    val sampleSchedules = listOf(
        RateSchedule(ratePlanId = "plan1", dayType = DayType.WEEKDAY, startTime = LocalTime.MIDNIGHT, endTime = LocalTime.of(8, 0), rate = 0.15, label = "Off-Peak"),
        RateSchedule(ratePlanId = "plan1", dayType = DayType.WEEKDAY, startTime = LocalTime.of(8, 0), endTime = LocalTime.of(16, 0), rate = 0.25, label = "Shoulder"),
        RateSchedule(ratePlanId = "plan1", dayType = DayType.WEEKDAY, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(21, 0), rate = 0.45, label = "Peak"),
        RateSchedule(ratePlanId = "plan1", dayType = DayType.WEEKDAY, startTime = LocalTime.of(21, 0), endTime = LocalTime.MIDNIGHT, rate = 0.15, label = "Off-Peak")
    )

    GridRatesTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreenContent(
                currentRate = sampleSchedules[2],
                currentTime = LocalDateTime.of(2026, 8, 9, 18, 30),
                dailySchedules = sampleSchedules,
                isPreview = false,
                previewTime = LocalTime.of(18, 30),
                onPreviewTimeChange = {}
            )
        }
    }
}
