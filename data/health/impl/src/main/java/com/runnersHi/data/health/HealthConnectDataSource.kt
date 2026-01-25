package com.runnersHi.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.model.HealthPermissionStatus
import java.time.Instant
import javax.inject.Inject

class HealthConnectDataSource @Inject constructor(
    private val context: Context
) : HealthDataSource {

    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val requiredPermissions = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
    )

    override suspend fun checkPermissionStatus(): HealthPermissionStatus {
        val client = healthConnectClient
            ?: return HealthPermissionStatus(
                isAvailable = false,
                hasReadPermission = false,
                needsPermissionRequest = false
            )

        return try {
            val granted = client.permissionController.getGrantedPermissions()
            val hasAllPermissions = requiredPermissions.all { it in granted }

            HealthPermissionStatus(
                isAvailable = true,
                hasReadPermission = hasAllPermissions,
                needsPermissionRequest = !hasAllPermissions
            )
        } catch (e: Exception) {
            HealthPermissionStatus(
                isAvailable = false,
                hasReadPermission = false,
                needsPermissionRequest = false
            )
        }
    }

    override suspend fun getExerciseRecords(
        startTime: Instant,
        endTime: Instant
    ): List<ExerciseRecord> {
        val client = healthConnectClient ?: return emptyList()

        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            response.records
                .filter { it.exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING }
                .map { session ->
                    val distance = getDistanceForSession(client, session.startTime, session.endTime)
                    val calories = getCaloriesForSession(client, session.startTime, session.endTime)

                    ExerciseRecord(
                        id = session.metadata.id,
                        startTime = session.startTime,
                        endTime = session.endTime,
                        exerciseType = ExerciseType.RUNNING,
                        distanceMeters = distance,
                        durationMillis = java.time.Duration.between(
                            session.startTime,
                            session.endTime
                        ).toMillis(),
                        calories = calories
                    )
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getDistanceForSession(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): Double? {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = DistanceRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.sumOf { it.distance.inMeters }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getCaloriesForSession(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): Double? {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.sumOf { it.energy.inKilocalories }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun getRequiredPermissions(): Set<String> = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )
    }
}
