package com.runnersHi.domain.model

data class AppConfig(
    val minVersion: String,
    val latestVersion: String,
    val maintenanceMode: Boolean,
    val maintenanceMessage: String
)
