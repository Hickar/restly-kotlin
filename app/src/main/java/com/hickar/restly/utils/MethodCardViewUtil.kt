package com.hickar.restly.utils

import com.hickar.restly.R

object MethodCardViewUtil {
    fun getTextColorId(method: String): Int {
        return when (method) {
            "GET", "HEAD" -> R.color.light_blue_700
            "POST" -> R.color.green_700
            "PUT", "PATCH", "OPTIONS" -> R.color.yellow_700
            "DELETE" -> R.color.red_700
            else -> R.color.light_blue_700
        }
    }

    fun getBackgroundColorId(method: String): Int {
        return when (method) {
            "GET", "HEAD" -> R.color.light_blue_200
            "POST" -> R.color.green_200
            "PUT", "PATCH", "OPTIONS" -> R.color.yellow_200
            "DELETE" -> R.color.red_200
            else -> R.color.light_blue_200
        }
    }

    fun getShortMethodName(method: String): String {
        return when (method) {
            "GET", "HEAD", "POST", "PUT" -> method
            "PATCH" -> "PTCH"
            "OPTIONS" -> "OPT"
            "DELETE" -> "DEL"
            else -> method
        }
    }
}