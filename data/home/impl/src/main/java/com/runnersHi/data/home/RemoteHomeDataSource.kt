package com.runnersHi.data.home

import com.runnersHi.core.network.ApiException
import com.runnersHi.core.network.MyRankingResponse
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.model.ThisWeekData
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.domain.home.model.TierInfo
import com.runnersHi.domain.home.model.TodaysRun
import javax.inject.Inject

/**
 * 홈의 러닝 수치는 Health Connect에서 계산한다. 서버에서는 티어만 받는다.
 * 서버가 레벨과 경험치를 내려주지 않아 레벨은 비워 둔다.
 */
class RemoteHomeDataSource @Inject constructor(
    private val api: RunnersHiApi
) : HomeDataSource {

    override suspend fun getHomeData(userToken: String): HomeData {
        val ranking = try {
            apiCall { api.getMyRanking() }
        } catch (e: ApiException) {
            if (e.code != REGION_NOT_SELECTED) throw e
            null
        }
        return HomeData(
            tier = ranking.toTierInfo(),
            todaysRun = TodaysRun(0.0, 0, 0, 0, 0),
            thisWeek = ThisWeekData(0.0, emptyList())
        )
    }

    private companion object {
        const val REGION_NOT_SELECTED = "U005"
    }
}

internal fun MyRankingResponse?.toTierInfo(): TierInfo {
    val tier = Tier.entries.firstOrNull { it.name == this?.tier } ?: Tier.BRONZE
    return TierInfo(
        tier = tier,
        tierName = tier.name.lowercase().replaceFirstChar { it.uppercase() } + " Runner",
        level = null,
        progressPercent = null
    )
}
