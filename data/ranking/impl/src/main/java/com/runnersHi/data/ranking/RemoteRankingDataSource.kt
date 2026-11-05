package com.runnersHi.data.ranking

import com.runnersHi.core.network.MyProfileResponse
import com.runnersHi.core.network.MyRankingResponse
import com.runnersHi.core.network.RankingResponse
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.domain.ranking.model.RankingBoard
import com.runnersHi.domain.ranking.model.RankingEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RemoteRankingDataSource @Inject constructor(
    private val api: RunnersHiApi
) : RankingRemoteDataSource {

    override suspend fun fetchRankingBoard(): RankingBoard = coroutineScope {
        val rankings = async { apiCall { api.getRankings(PAGE_SIZE) } }
        val mine = async { apiCall { api.getMyRanking() } }
        val profile = async { apiCall { api.getMyProfile() } }
        toRankingBoard(rankings.await().rankings, mine.await(), profile.await())
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

internal fun toRankingBoard(
    rankings: List<RankingResponse>,
    mine: MyRankingResponse,
    profile: MyProfileResponse
): RankingBoard {
    val myEntry = RankingEntry(
        rank = mine.rank,
        nickname = mine.nickname,
        tier = mine.tier.toTier(),
        distanceKm = mine.totalDistance / 1000.0,
        isMe = true,
        profileImageUrl = mine.profileImageUrl
    )
    return RankingBoard(
        regionName = profile.district?.name ?: profile.region?.name.orEmpty(),
        entries = rankings.map {
            RankingEntry(
                rank = it.rank,
                nickname = it.nickname,
                tier = it.tier.toTier(),
                distanceKm = it.totalDistance / 1000.0,
                isMe = it.nickname == mine.nickname,
                profileImageUrl = it.profileImageUrl
            )
        },
        myEntry = myEntry
    )
}

private fun String.toTier() = Tier.entries.firstOrNull { it.name == this } ?: Tier.BRONZE
