package com.runnersHi.data.ranking

import com.runnersHi.domain.home.model.Tier
import com.runnersHi.domain.ranking.model.RankingBoard
import com.runnersHi.domain.ranking.model.RankingEntry
import kotlinx.coroutines.delay
import javax.inject.Inject

/** iOS RankingBoard.mock과 같은 값 */
class MockRankingDataSource @Inject constructor() : RankingRemoteDataSource {

    private val entries = listOf(
        RankingEntry(1, "가나다라마가나다라마가나다라마", Tier.GOLD, 535.30),
        RankingEntry(2, "가나다라마가나다라마", Tier.BRONZE, 135.30),
        RankingEntry(3, "저녁에 러닝", Tier.GOLD, 45.30),
        RankingEntry(4, "주황색 형광펜", Tier.BRONZE, 5.30),
        RankingEntry(5, "곱창 맛있다", Tier.PLATINUM, 4.30),
        RankingEntry(6, "달려라 하니", Tier.SILVER, 4.10),
        RankingEntry(7, "러닝머신", Tier.GOLD, 3.90),
        RankingEntry(8, "트랙위의 요정", Tier.DIAMOND, 3.70),
        RankingEntry(9, "밤산책가", Tier.BRONZE, 3.50),
        RankingEntry(10, "새벽러너", Tier.SILVER, 3.30),
        RankingEntry(11, "한강지박령", Tier.GOLD, 3.10),
        RankingEntry(12, "페이스메이커", Tier.PLATINUM, 2.90),
        RankingEntry(13, "숨찬호흡", Tier.SILVER, 2.70),
        RankingEntry(14, "완주본능", Tier.BRONZE, 2.50),
        RankingEntry(15, "잘먹는프로도", Tier.SILVER, 1.67, isMe = true),
        RankingEntry(16, "천천히가요", Tier.BRONZE, 1.50),
        RankingEntry(17, "첫걸음", Tier.BRONZE, 1.30),
        RankingEntry(18, "산책중", Tier.BRONZE, 1.10),
        RankingEntry(19, "걷는중", Tier.BRONZE, 0.90),
        RankingEntry(20, "시작이반", Tier.BRONZE, 0.70)
    )

    override suspend fun fetchRankingBoard(): RankingBoard {
        delay(300)
        return RankingBoard(
            regionName = "인천시",
            entries = entries,
            myEntry = entries.firstOrNull { it.isMe }
        )
    }
}
