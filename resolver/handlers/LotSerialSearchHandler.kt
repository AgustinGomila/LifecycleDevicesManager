package com.example.app.scanner.resolver.handlers

import com.example.app.data.api.lot.LotApiWrapper
import com.example.app.data.enums.status.TaskStatus
import com.example.app.data.room.repository.lot.LotRepository
import com.example.app.scanner.resolver.BaseSearchHandler
import com.example.app.scanner.resolver.CodeResult
import com.example.app.scanner.resolver.SearchObject
import com.example.app.scanner.resolver.SearchType

/**
 * Implementación concreta para Lot por ID
 */
class LotSerialSearchHandler : BaseSearchHandler() {
    override val type = SearchType.LotSerial

    override suspend fun search(
        code: String,
        list: List<SearchObject>,
        listExclusive: Boolean,
    ): CodeResult? {
        return searchFlow(
            code = code,
            listSearch = {
                list.firstOrNull { so -> so.lotSerialNumber == it }
            },
            localSearch = {
                if (listExclusive) null else LotRepository.selectBySerialNumber(it)
            },
            remoteSearch = {
                if (listExclusive) null else
                    remoteSearchFlow { callback ->
                        LotApiWrapper.selectBySerialNumberWithCallback(
                            serialNumber = it,
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