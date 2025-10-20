package com.heklast.smartspender.core.data.remote

import com.heklast.smartspender.core.common.Result

data class UserProfile(
    val uid: String,
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null
)

interface UserApi {
    suspend fun get(uid: String): Result<UserProfile>
    fun watch(uid: String): kotlinx.coroutines.flow.Flow<Result<UserProfile>>
    suspend fun update(uid: String, profile: UserProfile): Result<Unit>
}