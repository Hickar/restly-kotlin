package com.hickar.restly.models

data class CollectionEvent(
    var id: String = "",
    var listen: String = "",
    var script: CollectionScript = CollectionScript(),
    var enabled: Boolean = true
)