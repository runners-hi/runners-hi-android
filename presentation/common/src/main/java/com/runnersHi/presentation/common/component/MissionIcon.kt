package com.runnersHi.presentation.common.component

import androidx.annotation.DrawableRes
import com.runnersHi.presentation.common.R

/**
 * 도메인의 미션 아이콘 이름을 번들 드로어블로 변환한다.
 * 모르는 이름은 데뷔전 아이콘으로 대체한다.
 */
@DrawableRes
fun missionIconRes(iconName: String): Int = when (iconName) {
    "mission_debut" -> R.drawable.mission_debut
    "mission_region" -> R.drawable.mission_region
    "mission_moonlight" -> R.drawable.mission_moonlight
    "mission_sprint" -> R.drawable.mission_sprint
    "mission_acorn" -> R.drawable.mission_acorn
    "mission_hangawi" -> R.drawable.mission_hangawi
    "mission_songpyeon" -> R.drawable.mission_songpyeon
    "mission_god" -> R.drawable.mission_god
    else -> R.drawable.mission_debut
}
