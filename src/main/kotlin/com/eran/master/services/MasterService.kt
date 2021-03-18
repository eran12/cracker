package com.eran.master.services

import com.eran.master.beans.MinionHandler
import com.eran.master.modules.MinionPayload
import com.eran.master.properties.MasterProperties
import com.eran.master.utils.phoneIncrement
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@Service
class MasterService(
    private val masterProperties: MasterProperties = MasterProperties(),
    private val minionHandler: MinionHandler = MinionHandler(masterProperties),
    private val rest: RestTemplate = RestTemplate(),
) {

    private val log = LoggerFactory.getLogger(MasterService::class.java)

    fun start(md5: String): ResponseEntity<String> {
        if (!md5.contains("^[a-zA-Z0-9]+$".toRegex())) {
            return ResponseEntity("MD5 is not valid", HttpStatus.BAD_REQUEST)
        }
        val start = System.currentTimeMillis()
        if (!minionHandler.getMd5Object(md5).found) {
            buildMinions(md5)
        }
        while (!minionHandler.getMd5Object(md5).found) {
            Thread.sleep(100)
        }
        log.info("number found in ${System.currentTimeMillis() - start}")
        return ResponseEntity.ok(minionHandler.getMd5Object(md5).currentNumber)
    }

    fun handleError(minionPayload: MinionPayload, retry: Boolean = true) {
        log.info("handle error")
        if (retry && minionPayload.maxAttempts < masterProperties.maxAttempts) {
            sendMinionRequest(
                minionPayload.copy(
                    maxAttempts = minionPayload.maxAttempts.and(1),
                    timeStamp = Instant.now()
                )
            )
        } else {
            minionHandler.addMinionToExclude(minionPayload)
        }
    }

    fun handleFail(minionPayload: MinionPayload) {
        log.info("handle fail")
        val md5Object = minionHandler.getMd5Object(minionPayload.md5)
        if (minionPayload.lastNumber.length > 11) {
            if (!md5Object.found) {
                md5Object.found = !md5Object.found
                md5Object.currentNumber = "invalid"
                minionHandler.setMd5Object(minionPayload.md5, md5Object)
            }
            return
        }

        val excludeMinion = minionHandler.getMinionFromExclude()
        if (excludeMinion != null) {
            sendMinionRequest(
                excludeMinion.copy(
                    address = minionPayload.address,
                    timeStamp = Instant.now(),
                    maxAttempts = minionPayload.maxAttempts.and(1)
                )
            )
        } else {
            md5Object.currentNumber = minionPayload.lastNumber
            sendMinionRequest(
                minionPayload.copy(timeStamp = Instant.now(), maxAttempts = 0)
            )
        }
    }

    fun handleSuccess(minionPayload: MinionPayload) {
        log.info("handle success: $minionPayload")
        val md5Object = minionHandler.getMd5Object(minionPayload.md5)
        if (!md5Object.found) {
            md5Object.currentNumber = minionPayload.lastNumber
            md5Object.found = !md5Object.found
            minionHandler.setMd5Object(minionPayload.md5, md5Object)
        }
    }

    private fun buildMinions(md5: String) {
        val md5Object = minionHandler.getMd5Object(md5)
        if (!md5Object.found) {
            masterProperties.minions.forEachIndexed { index, minionAddress ->
                sendMinionRequest(
                    MinionPayload(
                        minionAddress,
                        md5,
                        if (index > 1)
                            md5Object.currentNumber.phoneIncrement(masterProperties.increment)
                        else md5Object.currentNumber,
                        masterProperties.maxAttempts,
                        masterProperties.increment
                    )
                )
            }
        }
    }

    private fun sendMinionRequest(minionPayload: MinionPayload) {
        val md5Object = minionHandler.getMd5Object(minionPayload.md5)
        if (!md5Object.found) {
            try {
                minionHandler.addMinion(minionPayload)
                log.info(minionPayload.lastNumber)
                val response = rest.postForEntity(
                    minionPayload.address,
                    minionPayload,
                    String::class.java
                )
                when (response.statusCodeValue) {
                    in 500..600 -> handleError(minionPayload)
                }
            } catch (e: Exception) {
                handleError(minionPayload, false)
            }
        }
    }

    fun checkIfMd5Found(md5: String): ResponseEntity<String> {
        val md5Object = minionHandler.getMd5Object(md5)
        return if (md5Object.found) {
            ResponseEntity.ok(md5Object.currentNumber)
        } else {
            ResponseEntity.ok("Still working to find the MD5 correct number")
        }
    }
}
