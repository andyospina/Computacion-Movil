package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.NotFoundException
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.dto.FirestoreProductData
import com.example.myapplication.data.dto.FirestoreProductDto
import com.example.myapplication.data.dto.FirestoreReviewDto
import com.example.myapplication.data.dto.ProductData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val PRODUCTS_COLLECTION = "products"

class ProductFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRemoteDataSource {

    // Dos consultas (productos y reseñas) en vez de una por producto.
    override suspend fun getProducts(): List<ProductData> {
        val products = firestore.collection(PRODUCTS_COLLECTION).get().await()
            .toObjects(FirestoreProductDto::class.java)
        val reviewsByProduct = firestore.collection(REVIEWS_COLLECTION).get().await()
            .toObjects(FirestoreReviewDto::class.java)
            .groupBy { it.productId }
        return products.map { FirestoreProductData(it, reviewsByProduct[it.id].orEmpty()) }
    }

    override suspend fun getProductById(id: String): ProductData {
        val snapshot = firestore.collection(PRODUCTS_COLLECTION).document(id).get().await()
        if (!snapshot.exists()) throw NotFoundException("products/$id no existe")
        val product = snapshot.toObject(FirestoreProductDto::class.java)
            ?: throw NotFoundException("products/$id no se pudo convertir")
        val reviews = firestore.collection(REVIEWS_COLLECTION).whereEqualTo("productId", id).get().await()
            .toObjects(FirestoreReviewDto::class.java)
        return FirestoreProductData(product, reviews)
    }
}
