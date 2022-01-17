package com.hickar.restly.models

import java.util.*


data class RequestMultipartData(
    override var key: String = "",
    override var value: String = "",
    override var enabled: Boolean = true,
    override var description: String? = null,
    var valueFile: RequestFile? = null,
    var type: String = TEXT,
    override var uid: String = UUID.randomUUID().toString()
) : RequestKeyValueData() {
    fun isEmpty(): Boolean {
        return key.isEmpty() && (value.isEmpty() || valueFile == null)
    }

    companion object {
        const val TEXT = "TEXT"
        const val FILE = "FILE"
    }
}