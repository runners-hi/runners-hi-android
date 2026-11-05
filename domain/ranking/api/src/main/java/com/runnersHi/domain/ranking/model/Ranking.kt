package com.runnersHi.domain.ranking.model

import com.runnersHi.domain.home.model.Tier

data class RankingEntry(
    val rank: Int,
    val nickname: String,
    val tier: Tier,
    val distanceKm: Double,
    val isMe: Boolean = false,
    val profileImageUrl: String? = null
) {
    val displayNickname: String get() = if (isMe) "$nickname (나)" else nickname
}

data class RankingBoard(
    val regionName: String,
    val entries: List<RankingEntry>,
    val myEntry: RankingEntry?
) {
    val topEntries: List<RankingEntry> get() = entries.take(TOP_COUNT)
    val isMyEntryInTop: Boolean get() = myEntry != null && myEntry.rank <= TOP_COUNT

    private companion object {
        const val TOP_COUNT = 5
    }
}
