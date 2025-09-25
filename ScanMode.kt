package com.example.app.scanner

import android.os.Parcelable
import com.example.app.R
import com.example.app.MyApp.Companion.context
import kotlinx.parcelize.Parcelize

@Parcelize
class ScanMode(
    val scanModeId: Int,
    private val description: String
) : Parcelable {

    override fun toString() = description

    override fun equals(other: Any?) = other is ScanMode && scanModeId == other.scanModeId
    override fun hashCode() = scanModeId

    companion object {
        private val allModes by lazy {
            listOf(
                ScanMode(0, context.getString(R.string.manual_qty_mode)),
                ScanMode(1, context.getString(R.string.total_qty_mode)),
                ScanMode(2, context.getString(R.string.one_by_one_mode))
            ).sortedBy { it.scanModeId }
        }

        val manualQtyMode get() = allModes[0]
        val totalQtyMode get() = allModes[1]
        val oneByOneMode get() = allModes[2]

        fun getAll() = allModes
        fun getById(scanModeId: Int) = allModes.firstOrNull { it.scanModeId == scanModeId }
    }
}