package com.runnersHi.data.region

import com.runnersHi.domain.region.model.Region
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock 지역 데이터 소스
 */
class MockRegionDataSource @Inject constructor() : RegionRemoteDataSource {

    private val allRegions = listOf(
        // ㄱ
        Region("1", "가평군"),
        Region("2", "강릉시"),
        Region("3", "강진군"),
        Region("4", "거제시"),
        Region("5", "거창군"),
        Region("6", "경산시"),
        Region("7", "경주시"),
        Region("8", "계룡시"),
        Region("9", "고령군"),
        Region("10", "고성군"),
        Region("11", "고양시"),
        Region("12", "고창군"),
        Region("13", "고흥군"),
        Region("14", "곡성군"),
        Region("15", "공주시"),
        Region("16", "과천시"),
        Region("17", "광명시"),
        Region("18", "광양시"),
        Region("19", "광주광역시"),
        Region("20", "광주시"),
        Region("21", "괴산군"),
        Region("22", "구례군"),
        Region("23", "구리시"),
        Region("24", "구미시"),
        Region("25", "군산시"),
        Region("26", "군위군"),
        Region("27", "군포시"),
        Region("28", "금산군"),
        Region("29", "김제시"),
        Region("30", "김천시"),
        Region("31", "김포시"),
        Region("32", "김해시"),
        // ㄴ
        Region("33", "나주시"),
        Region("34", "남양주시"),
        Region("35", "남원시"),
        Region("36", "남해군"),
        Region("37", "논산시"),
        // ㄷ
        Region("38", "단양군"),
        Region("39", "담양군"),
        Region("40", "당진시"),
        Region("41", "대구광역시"),
        Region("42", "대전광역시"),
        Region("43", "동두천시"),
        Region("44", "동해시"),
        // ㅁ
        Region("45", "목포시"),
        Region("46", "무안군"),
        Region("47", "무주군"),
        Region("48", "문경시"),
        Region("49", "밀양시"),
        // ㅂ
        Region("50", "보령시"),
        Region("51", "보성군"),
        Region("52", "보은군"),
        Region("53", "봉화군"),
        Region("54", "부산광역시"),
        Region("55", "부안군"),
        Region("56", "부여군"),
        Region("57", "부천시"),
        // ㅅ
        Region("58", "사천시"),
        Region("59", "산청군"),
        Region("60", "삼척시"),
        Region("61", "상주시"),
        Region("62", "서귀포시"),
        Region("63", "서산시"),
        Region("64", "서울특별시"),
        Region("65", "서천군"),
        Region("66", "성남시"),
        Region("67", "성주군"),
        Region("68", "세종특별자치시"),
        Region("69", "속초시"),
        Region("70", "수원시"),
        Region("71", "순창군"),
        Region("72", "순천시"),
        Region("73", "시흥시"),
        Region("74", "신안군"),
        // ㅇ
        Region("75", "아산시"),
        Region("76", "안동시"),
        Region("77", "안산시"),
        Region("78", "안성시"),
        Region("79", "안양시"),
        Region("80", "양구군"),
        Region("81", "양산시"),
        Region("82", "양양군"),
        Region("83", "양주시"),
        Region("84", "양평군"),
        Region("85", "여수시"),
        Region("86", "여주시"),
        Region("87", "연천군"),
        Region("88", "영광군"),
        Region("89", "영덕군"),
        Region("90", "영동군"),
        Region("91", "영암군"),
        Region("92", "영양군"),
        Region("93", "영월군"),
        Region("94", "영주시"),
        Region("95", "영천시"),
        Region("96", "예산군"),
        Region("97", "예천군"),
        Region("98", "오산시"),
        Region("99", "옥천군"),
        Region("100", "완도군"),
        Region("101", "완주군"),
        Region("102", "용인시"),
        Region("103", "울릉군"),
        Region("104", "울산광역시"),
        Region("105", "울진군"),
        Region("106", "원주시"),
        Region("107", "음성군"),
        Region("108", "의령군"),
        Region("109", "의성군"),
        Region("110", "의왕시"),
        Region("111", "의정부시"),
        Region("112", "이천시"),
        Region("113", "익산시"),
        Region("114", "인제군"),
        Region("115", "인천광역시"),
        Region("116", "임실군"),
        // ㅈ
        Region("117", "장성군"),
        Region("118", "장수군"),
        Region("119", "장흥군"),
        Region("120", "전주시"),
        Region("121", "정선군"),
        Region("122", "정읍시"),
        Region("123", "제주시"),
        Region("124", "제천시"),
        Region("125", "증평군"),
        Region("126", "진도군"),
        Region("127", "진안군"),
        Region("128", "진주시"),
        Region("129", "진천군"),
        // ㅊ
        Region("130", "창녕군"),
        Region("131", "창원시"),
        Region("132", "천안시"),
        Region("133", "철원군"),
        Region("134", "청도군"),
        Region("135", "청송군"),
        Region("136", "청양군"),
        Region("137", "청주시"),
        Region("138", "춘천시"),
        Region("139", "충주시"),
        // ㅌ
        Region("140", "태백시"),
        Region("141", "태안군"),
        Region("142", "통영시"),
        // ㅍ
        Region("143", "파주시"),
        Region("144", "평창군"),
        Region("145", "평택시"),
        Region("146", "포천시"),
        Region("147", "포항시"),
        // ㅎ
        Region("148", "하남시"),
        Region("149", "하동군"),
        Region("150", "함안군"),
        Region("151", "함양군"),
        Region("152", "함평군"),
        Region("153", "합천군"),
        Region("154", "해남군"),
        Region("155", "홍성군"),
        Region("156", "홍천군"),
        Region("157", "화성시"),
        Region("158", "화순군"),
        Region("159", "횡성군")
    )

    override suspend fun searchRegions(query: String): List<Region> {
        // API 호출 시뮬레이션
        delay(300)

        if (query.isBlank()) {
            return emptyList()
        }

        return allRegions.filter { it.name.contains(query) }
    }

    override suspend fun selectRegion(regionId: String, regionName: String) {
        // API 호출 시뮬레이션
        delay(500)
        // 성공 시 아무것도 하지 않음 (실패 시 Exception throw)
    }
}
