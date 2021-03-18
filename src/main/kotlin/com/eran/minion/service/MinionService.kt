package com.eran.minion.service

import com.eran.minion.module.MinionPayload
import com.eran.utils.phoneIncrement
import com.eran.utils.toMd5Hash
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@Service
class MinionService(private val rest: RestTemplate = RestTemplate()) {

    private val log = LoggerFactory.getLogger(MinionService::class.java)

    private val executor = Executors.newSingleThreadExecutor()

    fun startCalculate(minionPayload: MinionPayload) {
        log.debug("start calculate")
        val firstPhone = minionPayload.lastNumber
        var phone = minionPayload.lastNumber
        log.debug("check md5 for: $phone")
        executor.execute {
            repeat(minionPayload.increment) {
                if (phone.toMd5Hash().compareTo(minionPayload.md5) == 0) {
                    handleSuccess(minionPayload.copy(lastNumber = phone))
                    return@execute
                }
                phone = phone.phoneIncrement(1)
            }
            log.info("done checking range md5 form: $firstPhone to $phone")
            handleFail(minionPayload.copy(lastNumber = phone))
        }
    }

    private fun handleFail(minionPayload: MinionPayload) {
        log.debug("handle minion fail")
        sendRequest(minionPayload, minionPayload.masterAddresses.fails)
    }

    private fun handleSuccess(minionPayload: MinionPayload) {
        log.info("handle minion success")
        sendRequest(minionPayload, minionPayload.masterAddresses.success)
    }

    private fun sendRequest(minionPayload: MinionPayload, url: String) {
        log.debug("sending request to master: ${minionPayload.lastNumber}")
        try {
            val response = rest.postForEntity(
                url,
                minionPayload,
                String::class.java
            )
            when (response.statusCodeValue) {
                in 500..600 -> handleError(minionPayload)
            }
        } catch (e: Exception) {
            handleError(minionPayload)
        }
    }

    private fun handleError(minionPayload: MinionPayload) {
        log.debug("handling minion error: ${minionPayload.lastNumber}")
        sendRequest(minionPayload, minionPayload.masterAddresses.error)
    }
}
