package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.local.AppDatabase
import com.example.subz.data.repository.CloudSyncRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val user: FirebaseUser?) : AuthState
    data class Error(val message: String) : AuthState
}
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val database: AppDatabase,
    private val cloudSyncRepository: CloudSyncRepository
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun register(fullName: String, email: String, password: String) {
        if (fullName.isBlank()|| email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields must be filled")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullName
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        _authState.value = AuthState.Success(auth.currentUser)
                    }
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Registration Failed"
                    _authState.value = AuthState.Error(errorMessage)
                }
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password must be filled")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch(Dispatchers.IO) {
                            cloudSyncRepository.restoreDataFromCloud()
                            _authState.value = AuthState.Success(auth.currentUser)
                        }
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Login failed. Please check your account."
                        _authState.value = AuthState.Error(errorMessage)
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            cloudSyncRepository.backupDataToCloud()
            auth.signOut()
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}