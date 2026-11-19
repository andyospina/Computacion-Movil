package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.ReportReason
import com.example.myapplication.data.datasource.ReportRemoteDataSource
import com.example.myapplication.data.dto.FirestoreReportDto
import com.example.myapplication.data.dto.reportDocumentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal const val REPORTS_COLLECTION = "reports"

class ReportFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReportRemoteDataSource {

    private val reports get() = firestore.collection(REPORTS_COLLECTION)

    override suspend fun hasReported(userId: String, reviewId: String): Boolean =
        reports.document(reportDocumentId(userId, reviewId)).get().await().exists()

    override suspend fun report(userId: String, reviewId: String, reason: ReportReason) {
        reports.document(reportDocumentId(userId, reviewId))
            .set(FirestoreReportDto(userId = userId, reviewId = reviewId, reason = reason.value))
            .await()
    }
}
