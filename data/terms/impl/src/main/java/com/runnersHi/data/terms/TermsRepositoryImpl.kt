package com.runnersHi.data.terms

import com.runnersHi.domain.terms.model.TermsItem
import com.runnersHi.domain.terms.repository.TermsRepository
import javax.inject.Inject

class TermsRepositoryImpl @Inject constructor(
    private val remoteDataSource: TermsRemoteDataSource
) : TermsRepository {

    override suspend fun getTermsList(): Result<List<TermsItem>> {
        return runCatching {
            remoteDataSource.getTermsList()
        }
    }

    override suspend fun agreeTerms(agreedTermIds: List<String>): Result<Unit> {
        return runCatching {
            remoteDataSource.agreeTerms(agreedTermIds)
        }
    }
}
