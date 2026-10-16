package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.dto.ProductData
import com.example.myapplication.data.services.ProductRetrofitService
import javax.inject.Inject

class ProductRetrofitDataSource @Inject constructor(
    private val service: ProductRetrofitService
) : ProductRemoteDataSource {

    override suspend fun getProducts(): List<ProductData> = service.getProducts()

    override suspend fun getProductById(id: String): ProductData = service.getProductById(id)
}
