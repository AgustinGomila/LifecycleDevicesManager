package com.example.app.scanner.resolver.handlers

import com.example.app.data.api.item.ItemCodeApiWrapper
import com.example.app.data.enums.status.TaskStatus
import com.example.app.data.room.repository.item.ItemCodeRepository
import com.example.app.scanner.resolver.BaseSearchHandler
import com.example.app.scanner.resolver.CodeResult
import com.example.app.scanner.resolver.SearchObject
import com.example.app.scanner.resolver.SearchType

/**
 * Implementación concreta para Lot por ID
 */
class ItemItemCodeSearchHandler : BaseSearchHandler() {
    override val type = SearchType.ItemItemCode

    override suspend fun search(
        code: String,
        list: List<SearchObject>,
        listExclusive: Boolean,
    ): CodeResult? {
        return searchFlow(
            code = code,
            localSearch = {
                if (listExclusive) null else ItemCodeRepository.selectByCode(it).firstOrNull()
            },
            remoteSearch = {
                if (listExclusive) null else
                    remoteSearchFlow { callback ->
                        ItemCodeApiWrapper.selectByCodeWithCallback(
                            code = it,
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
            onFound = { itemCode ->
                CodeResult(
                    code = code,
                    status = TaskStatus.FINISHED,
                    msg = "OK",
                    typedList = arrayListOf(itemCode)
                )
            }
        )
    }
}