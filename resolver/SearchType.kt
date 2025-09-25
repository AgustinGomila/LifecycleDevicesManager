package com.example.app.scanner.resolver

import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_ITEM
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_LOT
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_LOT_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_RACK
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_RACK_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_WA
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_WAC_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.FORMULA_WA_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_ITEM
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_ITEM_URL
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_LOT
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_LOT_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_RACK
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_RACK_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_WA
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_WAC_UUID
import com.example.app.scanner.resolver.CodePrefix.Companion.PREFIX_WA_UUID

/**
 * Definir tipos de búsqueda
 */
sealed class SearchType(
    val priority: Int,
    val prefix: String? = null,
    val formula: String? = null
) {
    object ItemId : SearchType(1, PREFIX_ITEM, FORMULA_ITEM)
    object ItemEAN : SearchType(2)
    object ItemCode : SearchType(3)
    object ItemRegex : SearchType(4)
    object ItemItemCode : SearchType(1)
    object ItemUrl : SearchType(1, PREFIX_ITEM_URL)
    object LotId : SearchType(1, PREFIX_LOT, FORMULA_LOT)
    object LotUuid : SearchType(1, PREFIX_LOT_UUID, FORMULA_LOT_UUID)
    object LotSerial : SearchType(1)
    object WarehouseAreaId : SearchType(1, PREFIX_WA, FORMULA_WA)
    object WarehouseAreaUuid : SearchType(1, PREFIX_WA_UUID, FORMULA_WA_UUID)
    object RackId : SearchType(1, PREFIX_RACK, FORMULA_RACK)
    object RackUuid : SearchType(1, PREFIX_RACK_UUID, FORMULA_RACK_UUID)
    object WacUuid : SearchType(1, PREFIX_WAC_UUID, FORMULA_WAC_UUID)
    companion object {
        val itemSearches: List<SearchType>
            get() = listOf(
                ItemId,
                ItemEAN,
                ItemCode,
                ItemRegex,
                ItemUrl,
                ItemItemCode,
                LotId,
                LotUuid,
                LotSerial,
                WacUuid
            )

        val locationSearches: List<SearchType>
            get() = listOf(
                RackId,
                RackUuid,
                WarehouseAreaId,
                WarehouseAreaUuid,
            )

        val prefixedSearches: List<SearchType>
            get() = listOf(
                ItemId,
                ItemUrl,
                LotId,
                LotUuid,
                // Order,
                RackId,
                RackUuid,
                WarehouseAreaId,
                WarehouseAreaUuid,
                WacUuid
            )
    }
}