package com.hickar.restly.models

import com.hickar.restly.extensions.indexOfDiff

class RequestQuery(
    var url: String = ""
) {
    var domain: String = ""
    var parameters: MutableList<RequestQueryParameter> = mutableListOf()

    init {
        if (url.isNotBlank()) {
            val urlParts = url.split("?")

            when (urlParts.size) {
                1 -> {
                    domain = urlParts[0]
                }
                2 -> {
                    domain = urlParts[0]
                    parameters = parseParams(urlParts[1])
                }
                else -> {
                    domain = url
                }
            }
        }
    }

    @JvmName("url_set")
    fun setUrl(url: String) {
        val oldUrl = this.url
        var newUrl = url

        val oldParams = parameters
        val newParams = parseParams(url)

        val enabledParamsIndices = mutableListOf<Int>()
        for (i in parameters.indices) {
            if (parameters[i].enabled) enabledParamsIndices.add(i)
        }

        when {
            newParams.size < oldParams.size -> {
                when (newParams.size) {
                    0 -> {
                        deleteParameter(0)
                        newUrl = newUrl.replace("?", "")
                        domain = newUrl
                    }
                    1 -> {
                        deleteParameter(1)
                    }
                    else -> {
                        val diffIndex = oldUrl.indexOfDiff(url)
                        val separatorIndices = mutableListOf<Int>()

                        for (c in this.url.indices) {
                            if (this.url[c] == '&') separatorIndices.add(c)
                        }

                        for (i in separatorIndices.indices) {
                            if (diffIndex == separatorIndices[i]) deleteParameter(i)
                        }
                    }
                }
            }
            newParams.size == oldParams.size -> {
                for (i in newParams.indices) {
                    if (oldParams[i] != newParams[i]) {
                        setParameterKey(enabledParamsIndices[i], newParams[i].key)
                        setParameterValue(enabledParamsIndices[i], newParams[i].valueText)
                    }
                }
            }
            newParams.size > oldParams.size -> {
                addParameter()
                setParameterKey(newParams.size - 1, newParams.last().key)
            }
        }

        this.url = newUrl
    }

    fun addParameter() {
        url += if (parameters.size == 0) "?" else "&"
        parameters.add(RequestQueryParameter())
    }

    fun setParameterKey(position: Int, value: String) {
        if (parameters[position].enabled) {
            if (parameters[position].toString().isNotBlank()) {
                url = url.replace(parameters[position].toString(), parameters[position].copy(key = value).toString())
            } else {

            }
        }
        parameters[position].key = value
    }

    fun setParameterValue(position: Int, value: String) {
        if (parameters[position].enabled) {
            url = url.replace(parameters[position].toString(), parameters[position].copy(valueText = value).toString())
        }
        parameters[position].valueText = value
    }

    fun toggleParameter(position: Int) {
        parameters[position].enabled = !parameters[position].enabled
    }

    fun deleteParameter(position: Int): RequestQueryParameter {
        if (parameters[position].enabled) {
            val separator = if (position == parameters.size - 1) "&" else ""
            url = url.replace(parameters[position].toString() + separator, "")
        }
        return parameters.removeAt(position)
    }

    private fun parseParams(url: String): MutableList<RequestQueryParameter> {
        val paramsStartIndex = url.indexOf("?")
        val params = mutableListOf<RequestQueryParameter>()

        if (paramsStartIndex != -1) {
            val paramsUrlPart = url.substring(paramsStartIndex + 1, url.length)
            val paramPairs = paramsUrlPart.split("&")

            for (pair in paramPairs) {
                val keyValueSeparatorIndex = pair.indexOf("=")

                var key = ""
                var value = ""

                if (keyValueSeparatorIndex != -1) {
                    key = pair.substring(0, keyValueSeparatorIndex)
                    value = pair.substring(keyValueSeparatorIndex + 1, pair.length)
                } else {
                    key = pair
                }

                params.add(RequestQueryParameter(key, value))
            }
        }


        return params
    }

    private fun buildUrl(domain: String, parameters: MutableList<RequestQueryParameter>): String {
        var parameterString = ""
        for (i in parameters.indices) {
            if (i != 0) parameterString += "&"
            parameterString += parameters[i].toString()
        }

        return "${domain}?${parameterString}"
    }
}