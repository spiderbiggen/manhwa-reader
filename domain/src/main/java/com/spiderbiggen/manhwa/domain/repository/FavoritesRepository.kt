package com.spiderbiggen.manhwa.domain.repository

interface FavoritesRepository {
    fun setFavorite(manhwaId: String, isFavorite: Boolean)
    fun isFavorite(manhwaId: String): Boolean
    fun favorites(): Set<String>
}