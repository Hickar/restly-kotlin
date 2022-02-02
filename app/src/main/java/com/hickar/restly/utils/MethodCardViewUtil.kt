package com.hickar.restly.utils

import com.hickar.restly.R
import com.hickar.restly.consts.RequestMethod
import com.hickar.restly.models.CollectionOrigin

object MethodCardViewUtil {
    fun getMethodTextColorId(method: RequestMethod): Int {
        return when (method) {
            RequestMethod.GET, RequestMethod.HEAD -> R.color.light_blue_700
            RequestMethod.POST -> R.color.green_700
            RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS -> R.color.yellow_700
            RequestMethod.DELETE -> R.color.red_700
            else -> R.color.light_blue_700
        }
    }

    fun getMethodBackgroundColorId(method: RequestMethod): Int {
        return when (method) {
            RequestMethod.GET, RequestMethod.HEAD -> R.color.light_blue_200
            RequestMethod.POST -> R.color.green_200
            RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS -> R.color.yellow_200
            RequestMethod.DELETE -> R.color.red_200
            else -> R.color.light_blue_200
        }
    }

    fun getMethodShortMethodName(method: RequestMethod): String {
        return when (method) {
            RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST, RequestMethod.PUT -> method.value
            RequestMethod.PATCH -> "PTCH"
            RequestMethod.OPTIONS -> "OPT"
            RequestMethod.DELETE -> "DEL"
            else -> method.value
        }
    }

    fun getCollectionOriginTextColor(origin: CollectionOrigin): Int {
        return when (origin) {
            CollectionOrigin.LOCAL -> R.color.light_blue_700
            CollectionOrigin.POSTMAN -> R.color.orange_700
            else -> R.color.gray_700
        }
    }

    fun getCollectionOriginBackgroundColorId(origin: CollectionOrigin): Int {
        return when (origin) {
            CollectionOrigin.LOCAL -> R.color.light_blue_200
            CollectionOrigin.POSTMAN -> R.color.orange_200
            else -> R.color.gray_200
        }
    }

    fun getCollectionOriginShortName(origin: CollectionOrigin): String {
        return when (origin) {
            CollectionOrigin.LOCAL -> "LCL"
            CollectionOrigin.POSTMAN -> "PSTN"
            else -> "UKWN"
        }
    }
}