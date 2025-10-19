package com.heklast.smartspender.core.data.remote

import com.heklast.smartspender.core.common.Result
import com.heklast.smartspender.core.data.remote.dto.CreateExpenseRequest
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import com.heklast.smartspender.core.data.remote.dto.ListExpensesResponse

/**
 * Cross-platform contract for expenses network/storage operations.
 * Implemented by FirebaseExpenseApi (GitLive Firestore).
 */
interface ExpenseApi {
    suspend fun create(request: CreateExpenseRequest): Result<String>          // returns new id
    suspend fun get(id: String): Result<ExpenseResponse>
    suspend fun list(
        category: String? = null,
        fromEpochMs: Long? = null,
        toEpochMs: Long? = null,
        limit: Int = 50,
        offset: Int = 0
    ): Result<ListExpensesResponse>
    suspend fun update(id: String, request: CreateExpenseRequest): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
