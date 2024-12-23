package com.example.suapp.data.repository.impl

import com.example.suapp.data.model.Product
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.UploadData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : ProductRepository {
    override suspend fun getProducts(): List<ProductDto>? {
        return withContext(Dispatchers.IO) {
            postgrest.from("products").select().decodeList<ProductDto>()
        }
    }
    override suspend fun getProduct(id: String): ProductDto {
        return withContext(Dispatchers.IO) {
            postgrest.from("products").select {
                filter { eq("id", id) }
            }.decodeSingle<ProductDto>()
        }
    }
    override suspend fun createProduct(product: Product): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val productDto = ProductDto(
                    name = product.name,
                    price = product.price,
                    id = product.id,
                    image = product.image
                )
                postgrest.from("products").insert(productDto)
                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    override suspend fun deleteProduct(id: String) {
        return withContext(Dispatchers.IO){
            postgrest.from("products").delete{
                filter { eq("id", id) }
            }
        }
    }
    override suspend fun updateProduct(
        id: String,
        name: String,
        price: Double,
        imageName: String,
        imageFile: ByteArray
    ){
        return withContext(Dispatchers.IO){
            if(imageFile.isNotEmpty()){
                val imageUrl = storage.from("Product%20Image")
                    .update(path = "$imageName.png", data = imageFile)
                postgrest.from("products").update({
                    set("name", name)
                    set("price", price)
                    set("image", imageUrl)
                }){
                    filter { eq("id", id) } }
                }
            else{
                postgrest.from("products").update({
                    set("name", name)
                    set("price", price)
                }){
                    filter { eq("id", id) } }
            }
        }
    }
    private fun buildImageUrl(imageFileName: String) = "http://martclinic.cafe24.com:8000/storage/v1/object/public/${imageFileName}\".replace(\" \", \"%20\")"
}