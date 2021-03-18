package com.eran.minion.extensions

import java.math.BigInteger
import java.security.MessageDigest

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
fun String.phoneIncrement(): String {
    val num = this.replace("-", "")
    return with(StringBuilder()) {
        append(num.substring(0, 2))
        val incrementedNumber = num.substring(2).toLong().plus(1)
        val paddingMaxLength = 8
        append(incrementedNumber.toString().padStart(paddingMaxLength, '0'))
        insert(3, '-')
    }.toString()
}

fun String.toMd5Hash(): String {
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}

val md: MessageDigest = MessageDigest.getInstance("MD5")
