package com.example.app.scanner.resolver

import com.example.app.data.room.dto.qualityControl.QualityControlContent
import com.example.app.data.room.dto.wac.WarehouseAreaContent
import com.example.app.data.room.dto.wmr.WarehouseMovementRequestContent
import java.util.UUID

data class SearchObject(
    val uuid: UUID,
    val itemId: Long? = null,
    val itemUuid: UUID? = null,
    val itemCode: String? = null,
    val itemEan: String? = null,
    val lotId: Long? = null,
    val lotUuid: UUID? = null,
    val lotSerialNumber: String? = null,
    val warehouseAreaId: Long? = null,
    val warehouseAreaUuid: UUID? = null,
    val rackId: Long? = null,
    val rackUuid: UUID? = null,
) {
    companion object {
        fun fromWac(wac: WarehouseAreaContent): SearchObject {
            return SearchObject(
                uuid = wac.uuid,
                itemUuid = wac.itemUuid,
                itemCode = wac.itemCode,
                itemEan = wac.itemEan,
                lotId = wac.lotId,
                lotUuid = wac.lotUuid,
                lotSerialNumber = wac.lotSerialNumber,
                warehouseAreaId = wac.warehouseAreaId,
                warehouseAreaUuid = wac.warehouseAreaUuid,
                rackId = wac.rackId,
                rackUuid = wac.rackUuid,
            )
        }

        fun fromQualityControlContent(qcc: QualityControlContent): SearchObject {
            return SearchObject(
                uuid = qcc.uuid,
                itemUuid = qcc.itemUuid,
                itemCode = qcc.itemCode,
                itemEan = qcc.itemEan,
            )
        }

        fun fromWmrContent(content: WarehouseMovementRequestContent): SearchObject {
            return SearchObject(
                uuid = content.uuid,
                itemUuid = content.itemUuid,
                itemCode = content.itemCode,
                itemEan = content.itemEan,
                lotId = content.lotId,
                lotUuid = content.lotUuid,
                lotSerialNumber = content.lotSerialNumber,
                warehouseAreaUuid = content.originWarehouseAreaUuid,
                rackUuid = content.originRackUuid,
            )
        }
    }
}