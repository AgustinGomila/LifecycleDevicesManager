package com.example.app.scanner

import android.util.Log
import com.example.app.MyApp.Companion.settingsVm
import com.example.app.data.enums.collector.CollectorType

class Collector {
    companion object {
        // Este flag es para reinicializar el colector después de cambiar en Settings.
        var collectorTypeChanged = false

        var collectorType: CollectorType
            get() {
                return try {
                    settingsVm.collectorType
                } catch (ex: java.lang.Exception) {
                    Log.e(this::class.java.simpleName, ex.message.toString())
                    settingsVm.collectorType = CollectorType.none
                    CollectorType.none
                }
            }
            set(value) {
                settingsVm.collectorType = value
            }

        fun isNfcRequired(): Boolean {
            return settingsVm.useNfc
        }
    }
}