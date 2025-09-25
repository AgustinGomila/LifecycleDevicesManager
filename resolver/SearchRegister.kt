package com.example.app.scanner.resolver

import com.example.app.scanner.resolver.handlers.ItemCodeSearchHandler
import com.example.app.scanner.resolver.handlers.ItemEanSearchHandler
import com.example.app.scanner.resolver.handlers.ItemIdSearchHandler
import com.example.app.scanner.resolver.handlers.ItemItemCodeSearchHandler
import com.example.app.scanner.resolver.handlers.ItemUrlSearchHandler
import com.example.app.scanner.resolver.handlers.LotIdSearchHandler
import com.example.app.scanner.resolver.handlers.LotSerialSearchHandler
import com.example.app.scanner.resolver.handlers.LotUuidSearchHandler
import com.example.app.scanner.resolver.handlers.RackIdSearchHandler
import com.example.app.scanner.resolver.handlers.RackUuidSearchHandler
import com.example.app.scanner.resolver.handlers.WacUuidSearchHandler
import com.example.app.scanner.resolver.handlers.WarehouseAreaIdSearchHandler
import com.example.app.scanner.resolver.handlers.WarehouseAreaUuidSearchHandler

abstract class SearchRegister {
    companion object {
        fun registerDefaultHandlers() {
            SearchHandlerRegistry.apply {
                register(ItemCodeSearchHandler())
                register(ItemEanSearchHandler())
                register(ItemIdSearchHandler())
                register(ItemItemCodeSearchHandler())
                register(ItemUrlSearchHandler())
                register(LotIdSearchHandler())
                register(LotSerialSearchHandler())
                register(LotUuidSearchHandler())
                register(RackIdSearchHandler())
                register(RackUuidSearchHandler())
                register(WacUuidSearchHandler())
                register(WarehouseAreaIdSearchHandler())
                register(WarehouseAreaUuidSearchHandler())
            }
        }
    }

    object SearchHandlerRegistry {
        private val _handlers = mutableListOf<SearchHandler>()
        val handlers: List<SearchHandler> get() = _handlers

        fun register(handler: SearchHandler) {
            _handlers.add(handler)
        }
    }
}