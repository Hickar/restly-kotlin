package com.hickar.restly.extensions

import kotlin.math.pow

fun Long.toDocumentSize(): String {
    var size = 0.0
    var sizeUnit = ""

    when (this) {
        in 0..2.0.pow(10.0).toLong() * 1024 -> {
            size = this.toDouble() / 2.0.pow(10.0)
            sizeUnit = "kb"
        }
        in 2.0.pow(20.0).toLong()..2.0.pow(20.0).toLong() * 1024 -> {
            size = this.toDouble() / 2.0.pow(20.0)
            sizeUnit = "mb"
        }
        in 2.0.pow(30.0).toLong()..2.0.pow(30.0).toLong() * 1024 -> {
            size = this.toDouble() / 2.0.pow(30.0)
            sizeUnit = "gb"
        }
        in 2.0.pow(40.0).toLong()..2.0.pow(40.0).toLong() * 1024 -> {
            size = this.toDouble() / 2.0.pow(40.0)
            sizeUnit = "tb"
        }
    }

    return "%.2f%s".format(size, sizeUnit)
}

fun Long.toResponseTime(): String {
    var time = 0
    var timeUnit = ""

    when (this) {
        in 0..1000 -> {
            time = this.toInt()
            timeUnit = "ms"
        }
        in 1000..1000*60 -> {
            time = this.toInt() / 1000
            timeUnit = "s"
        }
        in 1000*60..1000*60*60 -> {
            time = this.toInt() / 60000
            timeUnit = "m"
        }
    }

    return "$time$timeUnit"
}