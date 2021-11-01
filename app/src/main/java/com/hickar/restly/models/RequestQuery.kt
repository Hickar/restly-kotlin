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
                    parameters = parseQueryParameters(urlParts[1])
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
        val newParams = parseQueryParameters(url)

        val enabledParamsIndices = mutableListOf<Int>()
        for (i in parameters.indices) {
            if (parameters[i].enabled) enabledParamsIndices.add(i)
        }

        when {
            newParams.size < oldParams.size -> {
                when (newParams.size) {
                    0 -> {
                        deleteParameter(0)
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
                        setParameterValue(enabledParamsIndices[i], newParams[i].value)
                    }
                }
            }
            newParams.size > oldParams.size -> {
                addParameter()
                setParameterKey(newParams.size - 1, newParams.last().key)
            }
        }

        domain = parseDomain(newUrl)
        this.url = buildQueryString(domain, parameters)
    }

    fun addParameter() {
        parameters.add(RequestQueryParameter())
        url = buildQueryString(domain, parameters)
    }

    fun setParameterKey(position: Int, value: String) {
        parameters[position].key = value
        if (parameters[position].enabled) {
            url = buildQueryString(domain, parameters)
        }
    }

    fun setParameterValue(position: Int, value: String) {
        parameters[position].value = value
        if (parameters[position].enabled) {
            url = buildQueryString(domain, parameters)
        }
    }

    fun toggleParameter(position: Int) {
        parameters[position].enabled = !parameters[position].enabled
        url = buildQueryString(domain, parameters)
    }

    fun deleteParameter(position: Int) {
        parameters.removeAt(position)
        url = buildQueryString(domain, parameters)
    }

    private fun parseQueryParameters(url: String): MutableList<RequestQueryParameter> {
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

    private fun buildQueryString(domain: String, parameters: MutableList<RequestQueryParameter>): String {
        var queryString = ""

        val enabledParameters = parameters.filter { it.enabled }

        if (enabledParameters.isNotEmpty()) {
            queryString += "?"
            for (i in enabledParameters.indices) {
                if (enabledParameters[i].enabled) {
                    if (i != 0) queryString += "&"
                    queryString += enabledParameters[i].toString()
                }
            }
        }

        return "${domain}${queryString}"
    }

    private fun parseDomain(url: String): String {
        val queryStartIndex = url.indexOf("?")
        return if (queryStartIndex != -1) {
            url.substring(0, queryStartIndex)
        } else {
            url
        }
    }
}