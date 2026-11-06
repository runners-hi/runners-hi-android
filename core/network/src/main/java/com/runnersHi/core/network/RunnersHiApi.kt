package com.runnersHi.core.network

import kotlinx.serialization.json.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** runners-hi-backend REST API. 데이터 없는 응답은 서버가 빈 객체로 주므로 JsonElement로 받는다. */
interface RunnersHiApi {

    @POST("api/auth/review")
    suspend fun loginForReview(@Body request: ReviewLoginRequest): ApiEnvelope<AuthResponse>

    /** 개발 서버 전용(상용은 404). */
    @POST("api/auth/dev")
    suspend fun loginForDev(@Body request: DevLoginRequest): ApiEnvelope<AuthResponse>

    @POST("api/auth/kakao")
    suspend fun loginWithKakao(@Body request: KakaoLoginRequest): ApiEnvelope<AuthResponse>

    /** 안드로이드 웹 로그인 전용. 서버가 Service ID로 Apple 코드를 교환한다. */
    @POST("api/auth/apple/web")
    suspend fun loginWithAppleWeb(@Body request: AppleLoginRequest): ApiEnvelope<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiEnvelope<TokenResponse>

    @POST("api/auth/logout")
    suspend fun logout(): ApiEnvelope<JsonElement>

    @GET("api/app/version/check")
    suspend fun checkVersion(
        @Query("platform") platform: String,
        @Query("version") version: String
    ): ApiEnvelope<AppVersionResponse>

    @GET("api/users/me")
    suspend fun getMyProfile(): ApiEnvelope<MyProfileResponse>

    @PATCH("api/users/nickname")
    suspend fun updateNickname(@Body request: NicknameUpdateRequest): ApiEnvelope<JsonElement>

    @PATCH("api/users/region")
    suspend fun updateRegion(@Body request: RegionUpdateRequest): ApiEnvelope<JsonElement>

    @POST("api/users/me/profile-image/upload-url")
    suspend fun createProfileImageUploadUrl(
        @Body request: ProfileImageUploadRequest
    ): ApiEnvelope<ProfileImageUploadResponse>

    @PATCH("api/users/me/profile-image")
    suspend fun confirmProfileImage(@Body request: ProfileImageConfirmRequest): ApiEnvelope<JsonElement>

    @DELETE("api/users/me/profile-image")
    suspend fun removeProfileImage(): ApiEnvelope<JsonElement>

    @PATCH("api/users/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): ApiEnvelope<JsonElement>

    @PATCH("api/users/notification-settings")
    suspend fun updateNotificationSettings(@Body request: NotificationSettingsRequest): ApiEnvelope<JsonElement>

    @GET("api/notifications")
    suspend fun getNotifications(): ApiEnvelope<List<NotificationResponse>>

    @PATCH("api/notifications/read-all")
    suspend fun markAllNotificationsRead(): ApiEnvelope<JsonElement>

    @POST("api/feedback")
    suspend fun sendFeedback(@Body request: FeedbackRequest): ApiEnvelope<JsonElement>

    @DELETE("api/users/me")
    suspend fun withdraw(): ApiEnvelope<JsonElement>

    @GET("api/terms")
    suspend fun getTerms(): ApiEnvelope<List<TermsResponse>>

    @POST("api/terms/agree")
    suspend fun agreeTerms(@Body request: TermsAgreementRequest): ApiEnvelope<JsonElement>

    @GET("api/regions")
    suspend fun getRegions(): ApiEnvelope<List<RegionResponse>>

    @GET("api/regions/{regionId}/districts")
    suspend fun getDistricts(@Path("regionId") regionId: Long): ApiEnvelope<List<DistrictResponse>>

    @GET("api/rankings")
    suspend fun getRankings(@Query("size") size: Int): ApiEnvelope<RankingListResponse>

    @GET("api/rankings/me")
    suspend fun getMyRanking(): ApiEnvelope<MyRankingResponse>

    @GET("api/missions")
    suspend fun getMissionGroups(): ApiEnvelope<List<MissionGroupResponse>>

    @POST("api/running-records")
    suspend fun createRunningRecord(@Body request: RunningRecordCreateRequest): ApiEnvelope<JsonElement>

    @GET("api/running-records")
    suspend fun getRunningRecords(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int
    ): ApiEnvelope<RunningRecordListResponse>

    @GET("api/notices")
    suspend fun getNotices(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int
    ): ApiEnvelope<NoticeListResponse>

    @GET("api/notices/{noticeId}")
    suspend fun getNoticeDetail(@Path("noticeId") noticeId: Long): ApiEnvelope<NoticeDetailResponse>
}

/** 토큰 갱신 전용. 인증 처리기 안에서 동기로 부른다. */
interface TokenApi {
    @POST("api/auth/refresh")
    fun refresh(@Body request: RefreshTokenRequest): Call<ApiEnvelope<TokenResponse>>
}
