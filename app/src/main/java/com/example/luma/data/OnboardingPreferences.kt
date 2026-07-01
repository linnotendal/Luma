package com.example.luma.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_prefs")

/**
 * Håller reda på om användaren har sett onboarding-flödet.
 * Visas en gång, sedan aldrig igen.
 */
class OnboardingPreferences(private val context: Context) {

    private val hasSeenOnboardingKey = booleanPreferencesKey("has_seen_onboarding")

    val hasSeenOnboarding: Flow<Boolean> = context.onboardingDataStore.data.map { prefs ->
        prefs[hasSeenOnboardingKey] ?: false
    }

    suspend fun setHasSeenOnboarding() {
        context.onboardingDataStore.edit { prefs ->
            prefs[hasSeenOnboardingKey] = true
        }
    }

    /** Nollställer flaggan så onboarding visas igen, t.ex. för att ta screenshots. */
    suspend fun resetOnboarding() {
        context.onboardingDataStore.edit { prefs ->
            prefs[hasSeenOnboardingKey] = false
        }
    }
}