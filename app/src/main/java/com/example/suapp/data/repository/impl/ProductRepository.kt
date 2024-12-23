package com.example.suapp.data.repository.impl

import com.example.suapp.data.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<ProductDto>?
    suspend fun getProduct(id: String): ProductDto
    suspend fun createProduct(product: Product): Boolean
    suspend fun updateProduct(
        id: String,
        name: String,
        price: Double,
        imageName: String,
        imageFile: ByteArray
    )
    suspend fun deleteProduct(id: String)
}