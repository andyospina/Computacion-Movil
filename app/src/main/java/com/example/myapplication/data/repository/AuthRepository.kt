package com.example.myapplication.data.repository

import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.AuthRemoteDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) {

    val currentUser: FirebaseUser?
        get() = remoteDataSource.currentUser

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatchingAuth {
        remoteDataSource.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = runCatchingAuth {
        remoteDataSource.signUp(email, password)
    }

    /** Recarga el usuario desde Firebase y devuelve su photoUrl (null si no tiene). */
    suspend fun getProfileImageUrl(): Result<String?> = runCatchingAuth {
        remoteDataSource.reloadUser()
        remoteDataSource.currentUser?.photoUrl?.toString()
    }

    fun signOut() {
        remoteDataSource.signOut()
    }

    private suspend fun <T> runCatchingAuth(block: suspend () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e, e.toAuthErrorMessageRes())
        }
    }
}
