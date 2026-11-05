package com.runnersHi.presentation.region

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.usecase.SearchRegionsUseCase
import com.runnersHi.domain.region.usecase.SelectRegionUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegionSelectionViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val selectRegion = object : SelectRegionUseCase {
        override suspend fun invoke(regionId: String, regionName: String) = Result.success(Unit)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(search: suspend (String) -> Result<List<Region>>) =
        RegionSelectionViewModel(
            searchRegionsUseCase = object : SearchRegionsUseCase {
                override suspend fun invoke(query: String) = search(query)
            },
            selectRegionUseCase = selectRegion
        )

    private fun settle() {
        scope.advanceTimeBy(DEBOUNCE_MS)
        scope.runCurrent()
    }

    @Test
    fun `검색어 없이 들어오면 전체 지역 목록을 불러온다`() {
        val queries = mutableListOf<String>()
        val vm = viewModel { query ->
            queries += query
            Result.success(listOf(Region("1", "강릉시"), Region("2", "서울특별시")))
        }

        settle()

        assertEquals(listOf(""), queries)
        assertEquals(listOf("강릉시", "서울특별시"), vm.state.value.searchResults.map { it.name })
        assertNull(vm.state.value.searchResults.first().highlightRange)
    }

    @Test
    fun `검색 결과에 입력한 글자 범위를 표시한다`() {
        val vm = viewModel { Result.success(listOf(Region("1", "서귀포시"))) }
        settle()

        vm.sendEvent(RegionSelectionContract.Event.SearchQueryChanged("귀포"))
        settle()

        assertEquals(1..2, vm.state.value.searchResults.single().highlightRange)
    }

    @Test
    fun `검색어를 지우면 다시 전체 목록을 불러온다`() {
        val queries = mutableListOf<String>()
        val vm = viewModel { query ->
            queries += query
            Result.success(listOf(Region("1", "강릉시")))
        }
        settle()
        vm.sendEvent(RegionSelectionContract.Event.SearchQueryChanged("강"))
        settle()

        vm.sendEvent(RegionSelectionContract.Event.ClearSearchQuery)
        settle()

        assertEquals(listOf("", "강", ""), queries)
        assertEquals("", vm.state.value.searchQuery)
    }

    @Test
    fun `늦게 온 이전 검색 결과가 새 결과를 덮어쓰지 않는다`() {
        val slow = CompletableDeferred<Result<List<Region>>>()
        val vm = viewModel { query ->
            when (query) {
                "서" -> slow.await()
                else -> Result.success(listOf(Region(query, query)))
            }
        }
        settle()
        vm.sendEvent(RegionSelectionContract.Event.SearchQueryChanged("서"))
        settle()

        vm.sendEvent(RegionSelectionContract.Event.SearchQueryChanged("강"))
        settle()
        slow.complete(Result.success(listOf(Region("old", "서울특별시"))))
        scope.runCurrent()

        assertEquals(listOf("강"), vm.state.value.searchResults.map { it.name })
    }

    @Test
    fun `검색이 실패하면 재시도할 수 있게 표시한다`() {
        val vm = viewModel { Result.failure(IllegalStateException("서버 오류")) }

        settle()

        assertTrue(vm.state.value.isNetworkError)
        assertEquals("서버 오류", vm.state.value.errorMessage)
    }

    private companion object {
        const val DEBOUNCE_MS = 301L
    }
}
