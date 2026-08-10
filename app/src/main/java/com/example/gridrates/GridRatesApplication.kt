package com.example.gridrates

import android.app.Application
import com.example.gridrates.data.local.AppDatabase
import com.example.gridrates.data.local.UserPreferences
import com.example.gridrates.data.repository.RateRepository
import com.example.gridrates.ui.widget.RateWidget
import com.example.gridrates.ui.widget.RateWidgetKeys
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GridRatesApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val userPreferences by lazy { UserPreferences(this) }
    val repository by lazy { RateRepository(database.rateDao(), userPreferences) }

    override fun onCreate() {
        super.onCreate()
        
        repository.currentActiveRate.onEach { rate ->
            val manager = GlanceAppWidgetManager(this)
            val ids = manager.getGlanceIds(RateWidget::class.java)
            ids.forEach { id ->
                updateAppWidgetState(this, id) { prefs ->
                    prefs[RateWidgetKeys.RATE] = rate?.rate ?: 0.0
                    prefs[RateWidgetKeys.LABEL] = rate?.label ?: "Unknown"
                }
                RateWidget().update(this, id)
            }
        }.launchIn(applicationScope)
    }
}
