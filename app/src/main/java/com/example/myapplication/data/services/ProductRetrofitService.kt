package com.example.myapplication.data.services

import com.example.myapplication.data.dto.ProductDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductRetrofitService {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto
}
