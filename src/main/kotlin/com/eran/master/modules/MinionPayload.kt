package com.eran.master.modules

import com.eran.utils.MasterUtils
import java.net.InetAddress
import java.time.Instant

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
data class MinionPayload(
    val address: String,
    val md5: String,
    val lastNumber: String,
    val maxAttempts: Int,
    val increment: Int,
    val masterAddresses: MasterAddresses = MasterAddresses(),
    val timeStamp: Instant = Instant.now()
)

data class MasterAddresses(
    val hostAddress: String? = "http://${InetAddress.getLocalHost().hostAddress}:${MasterUtils.port}"
) {
    val success: String = "$hostAddress/success"
    val error: String = "$hostAddress/error"
    val fails: String = "$hostAddress/fail"
}
