package com.example.app.scanner.resolver.handlers

import com.example.app.data.api.wac.WacApiWrapper
import com.example.app.data.enums.status.TaskStatus
import com.example.app.data.room.dto.wac.WarehouseAreaContent
import com.example.app.scanner.resolver.BaseSearchHandler
import com.example.app.scanner.resolver.CodeResolver.Companion.searchString
import com.example.app.scanner.resolver.CodeResult
import com.example.app.scanner.resolver.SearchObject
import com.example.app.scanner.resolver.SearchType
import java.util.UUID

/**
 * Implementación concreta para Wac por UUID
 */
class WacUuidSearchHandler : BaseSearchHandler() {
    override val type = SearchType.WacUuid

    override suspend fun search(
        code: String,
        list: List<SearchObject>,
        listExclusive: Boolean,
    ): CodeResult? {
        var formula = type.formula ?: return null
        val match = searchString(code, formula, 1)
        if (match.isEmpty()) return null

        return searchFlow<WarehouseAreaContent>(
            code = match,
            listSearch = {
                list.firstOrNull { so -> so.uuid == UUID.fromString(it) }
            },
            remoteSearch = {
                if (listExclusive) null else
                    remoteSearchFlow { callback ->
                        WacApiWrapper.selectByUuidWithCallback(
                            uuid = UUID.fromString(it),
                            addContainers = false,
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
            onFound = { wac ->
                CodeResult(
                    code = code,
                    status = TaskStatus.FINISHED,
                    msg = "OK",
                    typedList = arrayListOf(wac)
                )
            }
        )
    }
}