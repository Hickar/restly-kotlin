package com.hickar.restly.models


data class RequestMultipartData(
    override var key: String = "",
    override var valueText: String = "",
    override var enabled: Boolean = true,
    var valueFile: RequestFile? = null,
    var type: String = TEXT
) : RequestKeyValueData() {
    fun isEmpty(): Boolean {
        return key.isEmpty() && (valueText.isEmpty() || valueFile == null)
    }

    companion object {
        const val TEXT = "TEXT"
        const val FILE = "FILE"
    }
}