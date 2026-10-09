package com.example.myapplication.data.datasource

import com.example.myapplication.data.dto.ProductDto

interface ProductRemoteDataSource {
    suspend fun getProducts(): List<ProductDto>
    suspend fun getProductById(id: String): ProductDto
}
