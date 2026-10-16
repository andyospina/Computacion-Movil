package com.example.myapplication.data.repository

import com.example.myapplication.data.Product
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) {

    suspend fun getProducts(): Result<List<Product>> = runCatchingApi {
        remoteDataSource.getProducts().map { it.toProduct() }
    }

    suspend fun getProductById(id: String): Result<Product> = runCatchingApi {
        remoteDataSource.getProductById(id).toProduct()
    }
}
