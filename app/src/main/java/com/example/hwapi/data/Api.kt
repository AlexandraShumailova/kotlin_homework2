package com.example.hwapi.data

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface Api {
    @GET("products")
    suspend fun getProductsList(): Products

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
}

interface ProductsRepository {
    suspend fun getProductsList(): Flow<Result<List<Product>>>
}