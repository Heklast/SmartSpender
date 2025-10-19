package com.heklast.smartspender.core.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val fullName: String="",
    val email: String="",
    val number: Int=0,
    val birthday: String =""
    //pw?
    //photo

)