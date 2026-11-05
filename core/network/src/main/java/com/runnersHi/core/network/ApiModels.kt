package com.runnersHi.core.network

import kotlinx.serialization.Serializable

/** 서버 공통 응답 형식 */
@Serializable
data class ApiEnvelope<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: ApiErrorBody? = null
)

@Serializable
data class ApiErrorBody(val code: String = "", val message: String = "")

@Serializable data class KakaoLoginRequest(val accessToken: String)
@Serializable data class AppleLoginRequest(val idToken: String, val authorizationCode: String)
@Serializable data class RefreshTokenRequest(val refreshToken: String)
@Serializable data class AuthResponse(val accessToken: String, val refreshToken: String, val isNewUser: Boolean = false)
@Serializable data class TokenResponse(val accessToken: String, val refreshToken: String)

@Serializable data class AppVersionResponse(
    val forceUpdate: Boolean = false,
    val recommendUpdate: Boolean = false,
    val latestVersion: String = ""
)

@Serializable data class RegionResponse(val id: Long, val name: String)
@Serializable data class DistrictResponse(val id: Long, val regionId: Long, val name: String)
@Serializable data class RegionUpdateRequest(val regionId: Long, val districtId: Long? = null)

@Serializable data class MyProfileResponse(
    val nickname: String,
    val profileImageUrl: String? = null,
    val region: RegionResponse? = null,
    val district: DistrictResponse? = null,
    val notificationEnabled: Boolean = true
)
@Serializable data class NicknameUpdateRequest(val nickname: String)

@Serializable data class TermsResponse(
    val id: Long,
    val code: String,
    val title: String,
    val required: Boolean,
    val agreed: Boolean = false
)
@Serializable data class TermsAgreementRequest(val agreements: List<TermsAgreementItem>)
@Serializable data class TermsAgreementItem(val termsId: Long, val agreed: Boolean)

@Serializable data class RankingResponse(
    val rank: Int,
    val userId: Long,
    val nickname: String,
    val tier: String,
    val totalDistance: Int,
    val profileImageUrl: String? = null
)
@Serializable data class RankingListResponse(
    val rankings: List<RankingResponse> = emptyList(),
    val nextCursor: Long? = null,
    val hasNext: Boolean = false
)
@Serializable data class MyRankingResponse(
    val rank: Int,
    val nickname: String,
    val tier: String,
    val totalDistance: Int,
    val profileImageUrl: String? = null,
    val level: Int? = null,
    val progressPercent: Int? = null
)

@Serializable data class MissionGroupResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val type: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val missions: List<MissionResponse> = emptyList()
)
@Serializable data class MissionResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val status: String
)

@Serializable data class RunningRecordCreateRequest(
    val distance: Int,
    val duration: Int,
    val calories: Int? = null,
    val startedAt: String,
    val endedAt: String,
    val source: String
)
@Serializable data class RunningRecordResponse(val id: Long, val startedAt: String)
@Serializable data class RunningRecordListResponse(
    val records: List<RunningRecordResponse> = emptyList(),
    val nextCursor: Long? = null,
    val hasNext: Boolean = false
)

@Serializable data class NoticeSummaryResponse(val id: Long, val title: String, val createdAt: String)
@Serializable data class NoticeListResponse(
    val notices: List<NoticeSummaryResponse> = emptyList(),
    val nextCursor: Long? = null,
    val hasNext: Boolean = false
)
@Serializable data class NoticeDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: String
)

@Serializable data class ProfileImageUploadRequest(val contentType: String)
@Serializable data class ProfileImageUploadResponse(val presignedUrl: String, val objectKey: String)
@Serializable data class ProfileImageConfirmRequest(val objectKey: String)
@Serializable data class FcmTokenRequest(val fcmToken: String)
@Serializable data class NotificationSettingsRequest(val notificationEnabled: Boolean)
@Serializable data class NotificationResponse(
    val id: Long,
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val createdAt: String
)
@Serializable data class FeedbackRequest(val content: String)
