package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.ProductData

interface ProductRemoteDataSource {
    suspend fun getProducts(): List<ProductData>
    suspend fun getProductById(id: String): ProductData
}
