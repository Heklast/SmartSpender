package com.heklast.smartspender.core.data.remote.firebase

import com.heklast.smartspender.core.common.Result
import com.heklast.smartspender.core.data.remote.ExpenseApi
import com.heklast.smartspender.core.data.remote.dto.CreateExpenseRequest
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import com.heklast.smartspender.core.data.remote.dto.ListExpensesResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlinx.datetime.Instant
import kotlin.math.round

class FirebaseExpenseApi : ExpenseApi {

    private fun uid(): String =
        Firebase.auth.currentUser?.uid ?: error("No signed-in user")

    private fun expensesCol() =
        Firebase.firestore.collection("users").document(uid()).collection("expenses")

    private fun round2(a: Double) = round(a * 100.0) / 100.0

    private fun CreateExpenseRequest.toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "amount" to round2(amount),
        "date" to Instant.fromEpochMilliseconds(dateEpochMs), // GitLive supports kotlinx.datetime.Instant
        "category" to category,
        "notes" to notes,
        "tags" to (tags ?: emptyList<String>())
    )

    private fun docToResponse(doc: dev.gitlive.firebase.firestore.DocumentSnapshot): ExpenseResponse {
        val title = runCatching { doc.get<String>("title") }.getOrNull() ?: ""
        val amount = runCatching { doc.get<Double>("amount") }.getOrNull()
            ?: (runCatching { doc.get<Long>("amount").toDouble() }.getOrNull() ?: 0.0)

        val dateInstant: Instant = runCatching { doc.get<Instant>("date") }.getOrElse {
            val ts = runCatching { doc.get<Timestamp>("date") }.getOrNull()
            if (ts != null) Instant.fromEpochMilliseconds(ts.toMilliseconds().toLong())
            else Instant.fromEpochMilliseconds(0)
        }

        val category = runCatching { doc.get<String>("category") }.getOrNull() ?: "OTHER"
        val notes = runCatching { doc.get<String>("notes") }.getOrNull()
        val tags = runCatching { doc.get<List<String>>("tags") }.getOrNull() ?: emptyList()

        return ExpenseResponse(
            id = doc.id,
            title = title,
            amount = amount,
            dateEpochMs = dateInstant.toEpochMilliseconds(),
            category = category,
            notes = notes,
            tags = tags
        )
    }

    override suspend fun create(request: CreateExpenseRequest): Result<String> = try {
        val ref = expensesCol().add(request.toMap())
        Result.Ok(ref.id)
    } catch (t: Throwable) {
        Result.Err(t)
    }

    override suspend fun get(id: String): Result<ExpenseResponse> = try {
        val snap = expensesCol().document(id).get()
        Result.Ok(docToResponse(snap))
    } catch (t: Throwable) {
        Result.Err(t)
    }

    override suspend fun list(
        category: String?,
        fromEpochMs: Long?,
        toEpochMs: Long?,
        limit: Int,
        offset: Int
    ): Result<ListExpensesResponse> = try {
        var q = expensesCol().orderBy("date", Direction.DESCENDING)
        if (category != null) q = q.where { "category" equalTo category }
        if (fromEpochMs != null) q = q.where { "date" greaterThanOrEqualTo Instant.fromEpochMilliseconds(fromEpochMs) }
        if (toEpochMs != null) q = q.where { "date" lessThan Instant.fromEpochMilliseconds(toEpochMs) }

        val snap = q.limit(limit + offset).get()
        val items = snap.documents.drop(offset).map { d -> docToResponse(d) }

        Result.Ok(ListExpensesResponse(items = items, total = null))
    } catch (t: Throwable) {
        Result.Err(t)
    }

    override suspend fun update(id: String, request: CreateExpenseRequest): Result<Unit> = try {
        expensesCol().document(id).set(request.toMap(), merge = true)
        Result.Ok(Unit)
    } catch (t: Throwable) {
        Result.Err(t)
    }

    override suspend fun delete(id: String): Result<Unit> = try {
        expensesCol().document(id).delete()
        Result.Ok(Unit)
    } catch (t: Throwable) {
        Result.Err(t)
    }
}
