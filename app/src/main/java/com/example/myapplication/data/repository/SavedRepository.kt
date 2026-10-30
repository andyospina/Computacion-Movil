package com.example.myapplication.data.repository

import com.example.myapplication.data.Product
import com.example.myapplication.data.Result
import com.example.myapplication.data.datasource.ProductRemoteDataSource
import com.example.myapplication.data.datasource.SavedRemoteDataSource
import com.example.myapplication.data.identity.CurrentUserIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

/** Productos guardados («favoritos») del usuario de la sesión. */
class SavedRepository @Inject constructor(
    private val remoteDataSource: SavedRemoteDataSource,
    private val productDataSource: ProductRemoteDataSource,
    private val currentUserId: CurrentUserIdProvider
) {

    /** Si el producto está guardado, en tiempo real (para pintar el botón de la ficha). */
    fun observeIsSaved(productId: String): Flow<Result<Boolean>> =
        observeMyIds()
            .map { ids -> productId in ids }
            .distinctUntilChanged()
            .asResult()

    /**
     * Productos guardados, del último al primero. Cada cambio de la lista vuelve a consultar los
     * productos (con sus estadísticas); un id cuyo producto ya no existe se omite en vez de fallar.
     */
    fun observeSavedProducts(): Flow<Result<List<Product>>> =
        observeMyIds()
            .mapLatest { ids ->
                if (ids.isEmpty()) emptyList()
                else {
                    val byId = productDataSource.getProducts().map { it.toProduct() }.associateBy { it.id }
                    ids.mapNotNull { byId[it] }
                }
            }
            .asResult()

    suspend fun setSaved(productId: String, saved: Boolean): Result<Unit> = runCatchingApi {
        val uid = currentUserId.currentUserId() ?: throw NotAuthenticatedException()
        if (saved) remoteDataSource.save(uid, productId) else remoteDataSource.unsave(uid, productId)
    }

    private fun observeMyIds(): Flow<List<String>> {
        val uid = currentUserId.currentUserId() ?: return flowOf(emptyList())
        return remoteDataSource.observeSavedProductIds(uid)
    }
}
