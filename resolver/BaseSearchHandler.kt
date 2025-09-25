package com.example.app.scanner.resolver

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Implementación base para handlers
 */
abstract class BaseSearchHandler : SearchHandler {
    protected suspend fun <T> searchFlow(
        code: String,
        listSearch: suspend (String) -> SearchObject? = { null },
        listExclusive: Boolean = false,
        localSearch: suspend (String) -> T? = { null },
        remoteSearch: suspend (String) -> Flow<T?>? = { null },
        onFound: (Any) -> CodeResult
    ): CodeResult? {
        // 1. Búsqueda en lista local
        listSearch(code)?.let { return onFound(it) }
        if (listExclusive) return null

        // 2. Búsqueda en base de datos local
        localSearch(code)?.let { return onFound(it) }

        // 3. Búsqueda remota con manejo de callback
        return remoteSearch(code)?.firstOrNull { it != null }?.let { onFound(it) }
    }

    // Función de utilidad para convertir callbacks a Flow
    protected fun <T> remoteSearchFlow(
        block: (callback: (T?) -> Unit) -> Unit
    ): Flow<T?> = callbackFlow {
        block { result ->
            trySend(result).isSuccess
            close()
        }

        awaitClose { /* Limpieza opcional si se cancela */ }
    }
}