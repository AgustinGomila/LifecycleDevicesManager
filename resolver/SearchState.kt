package com.example.app.scanner.resolver

/**
 * Clase para manejar el estado de las búsquedas
 */
class SearchState {
    private val _activeSearches = mutableSetOf<SearchType>()
    val activeSearches: Set<SearchType> get() = _activeSearches

    fun enable(vararg types: SearchType) {
        _activeSearches.addAll(types)
    }

    fun reset() {
        _activeSearches.clear()
    }

    @Suppress("unused")
    fun disable(vararg types: SearchType) {
        _activeSearches.removeAll(types.toSet())
    }

    fun disablePrefixedSearches() {
        _activeSearches.removeAll(SearchType.prefixedSearches.toSet())
    }
}