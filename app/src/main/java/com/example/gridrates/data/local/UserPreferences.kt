package com.example.gridrates.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    companion object {
        val PROVIDER_ID = stringPreferencesKey("provider_id")
        val PLAN_ID = stringPreferencesKey("plan_id")
    }

    val providerId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PROVIDER_ID]
        }

    val planId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PLAN_ID]
        }

    suspend fun saveSelection(providerId: String, planId: String) {
        context.dataStore.edit { preferences ->
            preferences[PROVIDER_ID] = providerId
            preferences[PLAN_ID] = planId
        }
    }
}
