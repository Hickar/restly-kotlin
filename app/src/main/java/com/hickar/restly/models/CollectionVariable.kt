package com.hickar.restly.models

data class CollectionVariable(
    var id: String = "",
    var key: String = "",
    var value: Any? = null,
    var type: String = "",
    var name: String = "",
    var description: String = "",
    var system: Boolean = false,
    var enabled: Boolean = true
)