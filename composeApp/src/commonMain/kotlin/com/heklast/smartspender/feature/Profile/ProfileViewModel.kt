package com.heklast.smartspender.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heklast.smartspender.core.data.UserRepository
import com.heklast.smartspender.core.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val testUid: String? = null
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun load() {
        viewModelScope.launch {
            val uid = testUid ?: return@launch
            _user.value = UserRepository.getUserById(uid)
        }
    }
}