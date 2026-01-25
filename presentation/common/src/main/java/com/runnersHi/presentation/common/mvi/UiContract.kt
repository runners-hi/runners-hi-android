package com.runnersHi.presentation.common.mvi

/**
 * MVI 패턴의 기본 Contract 인터페이스
 *
 * 사용법:
 * ```
 * class SplashContract {
 *     data class State(...) : UiState
 *     sealed interface Event : UiEvent { ... }
 *     sealed interface Effect : UiEffect { ... }
 * }
 * ```
 */

/**
 * UI 상태를 나타내는 마커 인터페이스
 * - 화면의 현재 상태를 표현
 * - Immutable data class로 구현
 */
interface UiState

/**
 * 사용자 액션/인텐트를 나타내는 마커 인터페이스
 * - 사용자의 행동 (버튼 클릭, 텍스트 입력 등)
 * - sealed interface로 구현하여 모든 이벤트 타입 정의
 */
interface UiEvent

/**
 * 일회성 사이드 이펙트를 나타내는 마커 인터페이스
 * - 네비게이션, 토스트, 다이얼로그 등
 * - 한 번만 소비되어야 하는 이벤트
 */
interface UiEffect
