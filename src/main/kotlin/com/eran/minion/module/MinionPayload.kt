package com.eran.minion.module

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
    val masterAddresses: MasterAddresses,
    val timeStamp: Instant = Instant.now()
)

data class MasterAddresses(
    val hostAddress: String,
    val success: String,
    val error: String,
    val fails: String
)
