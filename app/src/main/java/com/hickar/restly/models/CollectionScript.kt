package com.hickar.restly.models

data class CollectionScript(
    var id: String = "",
    var type: String = "",
    var exec: List<String> = listOf(),
    var src: String = "",
    var name: String = ""
)