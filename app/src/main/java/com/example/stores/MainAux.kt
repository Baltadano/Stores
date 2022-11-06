package com.example.stores

interface MainAux {
    fun hideFav(isVisble: Boolean = false)
    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)

}