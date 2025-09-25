package com.example.app.scanner.resolver

class CodePrefix {
    companion object {
        const val PREFIX_ITEM_URL = "item/view?id="
        const val PREFIX_ITEM = "#IT#"
        const val PREFIX_LOT = "#L#"
        const val PREFIX_LOT_UUID = "#LOT#"
        const val PREFIX_ORDER = "#ORD#"
        const val PREFIX_RACK = "#RK#"
        const val PREFIX_WA = "#WA#"
        const val PREFIX_WA_UUID = "#WAR#"
        const val PREFIX_RACK_UUID = "#RACK#"
        const val PREFIX_WAC_UUID = "#WAC#"

        val allPrefixes = listOf(
            PREFIX_ITEM_URL,
            PREFIX_ITEM,
            PREFIX_LOT,
            PREFIX_LOT_UUID,
            PREFIX_ORDER,
            PREFIX_RACK,
            PREFIX_WA,
            PREFIX_WA_UUID,
            PREFIX_RACK_UUID,
            PREFIX_WAC_UUID
        )

        const val UUID_PATTERN = "([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})"
        const val FORMULA_ITEM = """${PREFIX_ITEM}(\d+)#"""
        const val FORMULA_LOT = """${PREFIX_LOT}(\d+)#"""
        const val FORMULA_LOT_UUID = """${PREFIX_LOT_UUID}${UUID_PATTERN}#"""
        const val FORMULA_ORDER = """${PREFIX_ORDER}(\d+)#"""
        const val FORMULA_RACK = """${PREFIX_RACK}(\d+)#"""
        const val FORMULA_RACK_UUID = """${PREFIX_RACK_UUID}${UUID_PATTERN}#"""
        const val FORMULA_WA = """${PREFIX_WA}(\d+)#"""
        const val FORMULA_WA_UUID = """${PREFIX_WA_UUID}${UUID_PATTERN}#"""
        const val FORMULA_WAC_UUID = """${PREFIX_WAC_UUID}${UUID_PATTERN}#"""
    }
}