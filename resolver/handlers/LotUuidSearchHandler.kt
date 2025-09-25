package com.example.app.scanner.resolver.handlers

import com.example.app.data.api.lot.LotApiWrapper
import com.example.app.data.enums.status.TaskStatus
import com.example.app.data.room.repository.lot.LotRepository
import com.example.app.scanner.resolver.BaseSearchHandler
import com.example.app.scanner.resolver.CodeResolver.Companion.searchString
import com.example.app.scanner.resolver.CodeResult
import com.example.app.scanner.resolver.SearchObject
import com.example.app.scanner.resolver.SearchType
import java.util.UUID

/**
 * Implementación concreta para Lot por UUID
 */
class LotUuidSearchHandler : BaseSearchHandler() {
    override val type = SearchType.LotUuid

    override suspend fun search(
        code: String,
        list: List<SearchObject>,
        listExclusive: Boolean,
    ): CodeResult? {
        var formula = type.formula ?: return null
        val match = searchString(code, formula, 1)
        if (match.isEmpty()) return null

        return searchFlow(
            code = match,
            listSearch = {
                list.firstOrNull { so -> so.lotUuid == UUID.fromString(it) }
            },
            localSearch = {
                if (listExclusive) null else LotRepository.selectByUuid(UUID.fromString(it))
            },
            remoteSearch = {
                if (listExclusive) null else
                    remoteSearchFlow { callback ->
                        var uuid = UUID.fromString(it)
                        LotApiWrapper.selectByUuidWithCallback(
                            uuid = uuid,
                            callback = { taskStatus, _, result ->
                                when (taskStatus) {
                                    TaskStatus.FINISHED -> callback(result.firstOrNull())
                                    TaskStatus.CRASHED, TaskStatus.CANCELED -> callback(null)
                                    else -> {}
                                }
                            }
                        )
                    }
            },
            onFound = { lot ->
                CodeResult(
                    code = code,
                    status = TaskStatus.FINISHED,
                    msg = "OK",
                    typedList = arrayListOf(lot)
                )
            }
        )
    }
}