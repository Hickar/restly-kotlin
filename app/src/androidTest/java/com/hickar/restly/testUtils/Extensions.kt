package com.hickar.restly.testUtils

fun String.random(length: Int = 20): String {
    val charPool = "abcdefghijklmnopqrstuvwxyz1234567890"
    return (1..length)
        .map { kotlin.random.Random.nextInt(0, charPool.length) }
        .map { charPool[it] }
        .joinToString("")
}