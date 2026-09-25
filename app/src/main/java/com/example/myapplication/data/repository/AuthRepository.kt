package com.example.myapplication.data.repository

import com.example.myapplication.data.datasource.AuthRemoteDataSource
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) {

    val currentUser: FirebaseUser?
        get() = remoteDataSource.currentUser

    suspend fun signIn(email: String, password: String) {
        remoteDataSource.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String) {
        remoteDataSource.signUp(email, password)
    }

    fun signOut() {
        remoteDataSource.signOut()
    }
}
