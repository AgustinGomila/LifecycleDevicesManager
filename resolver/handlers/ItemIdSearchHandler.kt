package com.example.app.scanner.resolver.handlers

import com.example.app.data.api.item.ItemApiWrapper
import com.example.app.data.enums.status.TaskStatus
import com.example.app.data.room.repository.item.ItemRepository
import com.example.app.scanner.resolver.BaseSearchHandler
import com.example.app.scanner.resolver.CodeResolver.Companion.searchString
import com.example.app.scanner.resolver.CodeResult
import com.example.app.scanner.resolver.SearchObject
import com.example.app.scanner.resolver.SearchType

/**
 * Implementación concreta para Item por ID
 */
class ItemIdSearchHandler : BaseSearchHandler() {
    override val type = SearchType.ItemId

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
                list.firstOrNull { so -> so.itemId == it.toLong() }
            },
            localSearch = {
                if (listExclusive) null else ItemRepository.selectById(it.toLong(), true)
            },
            remoteSearch = {
                if (listExclusive) null else
                    remoteSearchFlow { callback ->
                        ItemApiWrapper.selectByIdWithCallback(
                            id = it.toLong(),
                            onlyActive = true,
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