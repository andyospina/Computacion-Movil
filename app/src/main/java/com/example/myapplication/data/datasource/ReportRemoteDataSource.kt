package com.example.myapplication.data.datasource

import com.example.myapplication.data.ReportReason

interface ReportRemoteDataSource {
    /** Si [userId] ya reportó la reseña [reviewId]. */
    suspend fun hasReported(userId: String, reviewId: String): Boolean

    suspend fun report(userId: String, reviewId: String, reason: ReportReason)
}
