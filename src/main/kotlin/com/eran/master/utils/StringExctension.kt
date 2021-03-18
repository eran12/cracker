package com.eran.master.utils

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
fun String.phoneIncrement(increment: Int): String {
    val num = this.replace("-", "")
    return with(StringBuilder()) {
        append(num.substring(0, 2))
        val incrementedNumber = num.substring(2).toLong().plus(increment)
        val paddingMaxLength = 8
        append(incrementedNumber.toString().padStart(paddingMaxLength, '0'))
        insert(3, '-')
    }.toString()
}
