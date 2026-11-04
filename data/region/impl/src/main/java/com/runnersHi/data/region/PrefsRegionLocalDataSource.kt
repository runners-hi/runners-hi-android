package com.runnersHi.data.region

import android.content.Context
import android.content.SharedPreferences
import com.runnersHi.domain.region.model.Region
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PrefsRegionLocalDataSource(context: Context) : RegionLocalDataSource {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("runnershi_region", Context.MODE_PRIVATE)

    private val selected = MutableStateFlow(read())

    override fun observeSelectedRegion(): Flow<Region?> = selected

    override suspend fun saveSelectedRegion(region: Region) {
        prefs.edit().putString(KEY_ID, region.id).putString(KEY_NAME, region.name).apply()
        selected.value = region
    }

    override suspend fun clearSelectedRegion() {
        prefs.edit().clear().apply()
        selected.value = null
    }

    private fun read(): Region? {
        val id = prefs.getString(KEY_ID, null) ?: return null
        val name = prefs.getString(KEY_NAME, null) ?: return null
        return Region(id, name)
    }

    private companion object {
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
    }
}
