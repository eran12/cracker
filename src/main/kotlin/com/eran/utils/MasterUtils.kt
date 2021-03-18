package com.eran.utils

import java.net.InetAddress

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
object MasterUtils {
    val port: String = "8080"
    fun getAddress(): String = "http://${InetAddress.getLocalHost().hostAddress}:$port"
}
