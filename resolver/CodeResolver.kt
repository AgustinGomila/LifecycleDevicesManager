package com.example.app.scanner.resolver

import android.media.MediaPlayer
import android.util.Log
import com.example.app.R
import com.example.app.MyApp.Companion.context
import com.example.app.data.enums.status.TaskStatus
import com.example.app.misc.Statics.Companion.isDebuggable
import com.example.app.scanner.resolver.SearchRegister.SearchHandlerRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * Clase que se encarga de analizar y buscar un código en fuentes locales y proporcionadas por la API.
 * Puedes utilizar el Builder para configurar las opciones de búsqueda antes de realizar la búsqueda.
 *
 * @param builder Un objeto [Builder] que te permite configurar las opciones de búsqueda.
 *
 * [Diagrama_de_Flujo]
 * [Inicio]
 *   │
 *   ├─▶ Builder Configura:
 *   │     - Código a buscar
 *   │     - Timeout (Default: 25 seg)
 *   │     - Lista local pre-cargada (Opcional)
 *   │     - Handlers Registrados
 *   │
 *   ▼
 * [Ejecución]
 *   │
 *   ├─▶ Detección Prefijo (Trie) → Actualizar SearchState
 *   │
 *   ├─▶ Ejecutar Handlers Priorizados
 *   │     │
 *   │     ├─▶ Lista Local (Opcional) → ¿Éxito? → Resultado
 *   │     ├─▶ BD Local (Opcional) → ¿Éxito? → Resultado
 *   │     └─▶ API Remota (Opcional) → ¿Éxito? → Resultado
 *   │
 *   ▼
 * [Resultado]
 *   ├─▶ Log Métricas
 *   ├─▶ Notificación Sonora
 *   └─▶ Retornar
 *
 *  Ejemplo:
 *  ```
 *  CodeResolver.Builder()
 *      .withCode("#IT#123")
 *      .timeout(10000)
 *      .enableSearch(SearchType.ItemId)
 *      .onFinish { result -> handleResult(result) }
 *      .build()
 *  ```
 *  @see SearchType Para tipos de búsqueda disponibles
 */
class CodeResolver private constructor(builder: Builder) {

    private var code: String
    private var list: List<SearchObject>
    private var listExclusive: Boolean
    private var searchState: SearchState
    private var timeout: Long
    private var onFinish: (CodeResult) -> Unit

    class Builder {
        fun build(): CodeResolver {
            return CodeResolver(this)
        }

        internal var code: String = ""
        internal var list: List<SearchObject> = listOf()
        internal var listExclusive: Boolean = false
        internal var searchState = SearchState()
        internal var timeout: Long = 25 * 1000L
        internal var onFinish: (CodeResult) -> Unit = {}

        /**
         * Configura el código a buscar.
         */
        fun withCode(code: String) = apply { this.code = code }

        /**
         * Define la lista donde realizar la primera búsqueda.
         */
        fun withList(list: List<SearchObject>) = apply { this.list = list }

        /**
         * Activa la búsqueda exclusiva en la lista.
         */
        @Suppress("unused")
        fun listExclusive() = apply { listExclusive = true }

        /**
         * Grupo de tipo de búsquedas.
         */
        fun enableSearches(types: List<SearchType>) = apply {
            searchState.enable(*types.toTypedArray())
        }

        /**
         * Define el timeout de la operación (Predefinido: 25 seg)
         */
        @Suppress("unused")
        fun timeout(timeout: Int) = apply { this.timeout = timeout * 1000L }

        /**
         * Configura un callback que se llama cuando se completa la búsqueda.
         */
        fun onFinish(callback: (CodeResult) -> Unit) = apply { onFinish = callback }
    }

    companion object {
        private val tag = this::class.java.enclosingClass?.simpleName ?: this::class.java.simpleName

        private val prefixTrie = Trie<SearchType>().apply {
            SearchType.prefixedSearches.forEach { insert(it) }
        }

        fun detectPrefixType(code: String): SearchType? {
            return prefixTrie.findLongestPrefix(code.lowercase())
        }

        fun playSoundNotification(success: Boolean) {
            try {
                val resId = if (success) R.raw.scan_success else R.raw.scan_fail
                val mp = MediaPlayer.create(context, resId)
                mp.setOnCompletionListener { mp.release() }
                mp.start()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        private fun logSearchMetrics(
            successType: SearchType?,
            types: Set<SearchType>,
            duration: Long,
            status: TaskStatus
        ) {
            // NOTA: Implementar Analytics
            // FirebaseAnalytics.getInstance(context).logEvent(
            //     "search_executed", bundleOf(
            //         "types" to types.joinToString(),
            //         "duration" to duration,
            //         "status" to status.name
            //     )
            // )

            Log.d(
                tag, """
        Search executed
        Types: ${types.map { it::class.simpleName }.joinToString()}
        Success type: ${if (successType != null) successType::class.simpleName else "NONE"}
        Duration: ${duration}ms
        Status: $status
    """.trimIndent()
            )
        }

        fun searchString(origin: String, formula: String, position: Int): String {
            val rx = Regex(formula)
            val matches = rx.matchEntire(origin)

            if (matches != null) {
                if (matches.groups.size >= position && matches.groups[position]?.value.toString()
                        .isNotEmpty()
                ) {
                    try {
                        return matches.groupValues[position]
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        val res =
                            "Error doing formula.\r\n formula $formula\r\n string $origin\r\n${ex.message}"
                        Log.e(tag, res)
                    }
                }
            }
            return ""
        }
    }

    private val handlers = SearchHandlerRegistry.handlers

    private val tag = this::class.java.enclosingClass?.simpleName ?: this::class.java.simpleName

    init {
        this.code = builder.code
        this.list = builder.list
        this.listExclusive = builder.listExclusive
        this.searchState = builder.searchState
        this.timeout = builder.timeout
        this.onFinish = builder.onFinish

        if (code.isBlank()) {
            onFinish(CodeResult(code, TaskStatus.CANCELED, "Code cannot be blank", null))
        } else {
            require(list.all { it.isValid() }) { "List contains invalid SearchObjects" }
            require(searchState.activeSearches.isNotEmpty()) { "At least one search type must be enabled" }

            if (isDebuggable()) Log.i(tag, "Get result from code: $code")

            execute()
        }
    }

    private fun SearchObject.isValid(): Boolean = true

    private fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = withTimeout(timeout) {
                    executeSearch()
                }
                onFinish(result)
            } catch (_: TimeoutCancellationException) {
                onFinish(CodeResult(code, TaskStatus.CRASHED, "Timeout", null))
            }
        }
    }

    private suspend fun executeSearch(): CodeResult {
        // Métricas y logging
        val startTime = System.currentTimeMillis()

        // Determinar tipo de búsqueda por prefijo
        val prefixType = detectPrefixType(code)
        if (prefixType == null) {
            // Deshabilitar las búsquedas innecesarias
            searchState.disablePrefixedSearches()
        } else {
            // Habilitar únicamente la búsqueda por prefijo
            prefixType.let {
                searchState.reset()
                searchState.enable(it)
            }
        }

        // Ejecutar búsquedas en orden de prioridad
        val sortedHandlers = handlers
            .filter { it.type in searchState.activeSearches }
            .sortedBy { it.priority }

        Log.d(tag, "Executing search for: ${code}...")
        Log.d(tag, "Active searches: ${searchState.activeSearches.map { it::class.simpleName }}")

        for (handler in sortedHandlers) {
            val result = handler.search(code, list, listExclusive)

            if (result != null) {
                val duration = System.currentTimeMillis() - startTime
                logSearchMetrics(handler.type, searchState.activeSearches, duration, result.status)

                playSoundNotification(true)
                return result
            }
        }

        val duration = System.currentTimeMillis() - startTime
        logSearchMetrics(null, searchState.activeSearches, duration, TaskStatus.CRASHED)

        playSoundNotification(false)
        return CodeResult(code, TaskStatus.CRASHED, context.getString(R.string.CODE_NOT_FOUND), null)
    }
}