package com.runnersHi.domain.health.model

data class HealthPermissionStatus(
    val isAvailable: Boolean,
    val hasReadPermission: Boolean,
    val needsPermissionRequest: Boolean
)
