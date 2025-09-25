package com.example.app.scanner.resolver.handlers

import com.example.app.data.api.item.ItemApiWrapper
import com.example.app.data.enums.status.TaskStatus
import com.example.app.data.room.repository.item.ItemRepository
import com.example.app.scanner.resolver.BaseSearchHandler
import com.example.app.scanner.resolver.CodeResult
import com.example.app.scanner.resolver.SearchObject
import com.example.app.scanner.resolver.SearchType

/**
 * Implementación concreta para Item por ID
 */
class ItemEanSearchHandler : BaseSearchHandler() {
    override val type = SearchType.ItemEAN

    override suspend fun search(
        code: String,
        list: List<SearchObject>,
        listExclusive: Boolean,
    ): CodeResult? {
        return searchFlow(
            code = code,
            listSearch = {
                list.firstOrNull { so -> so.itemEan == it }
            },
            localSearch = {
                if (listExclusive) null else ItemRepository.selectByEan(it).firstOrNull()
            },
            remoteSearch = {
                if (listExclusive) null else
                    remoteSearchFlow { callback ->
                        ItemApiWrapper.selectByEanWithCallback(
                            ean = it,
                            onlyActive = false,
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
            onFound = { item ->
                CodeResult(
                    code = code,
                    status = TaskStatus.FINISHED,
                    msg = "OK",
                    typedList = arrayListOf(item)
                )
            }
        )
    }
}