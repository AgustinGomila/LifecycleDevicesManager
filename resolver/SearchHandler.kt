package com.example.app.scanner.resolver

interface SearchHandler {
    suspend fun search(
        code: String,
        list: List<SearchObject>,
        listExclusive: Boolean,
    ): CodeResult?

    val type: SearchType
    val priority: Int get() = type.priority
}