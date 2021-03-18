package com.eran.master.beans

import com.eran.master.modules.Md5Object
import com.eran.master.modules.MinionPayload
import com.eran.master.properties.MasterProperties
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.scheduling.annotation.Scheduled

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class MinionHandler(private val properties: MasterProperties) {
    private val minionsList = hashMapOf<String, MinionPayload>()
    private val minionsExcluded = hashMapOf<String, MinionPayload>()
    private val md5Map = hashMapOf<String, Md5Object>()

    companion object {
        private val log = LoggerFactory.getLogger(MinionHandler::class.java)
    }

    fun addMinion(minionPayload: MinionPayload) {
        if (minionsExcluded[minionPayload.address] != null) {
            log.info("before adding to list remove from exclude")
            removeMinionFromExclude(minionPayload)
        }
        log.info("adding minion to list: $minionPayload")
        minionsList[minionPayload.address] = minionPayload
    }

    fun addMinionToExclude(minionPayload: MinionPayload) {
        removeMinionFromList(minionPayload)
        log.info("adding minion to exclude: $minionPayload")
        minionsExcluded[minionPayload.address] = minionPayload
    }

    private fun removeMinionFromList(minionPayload: MinionPayload): Boolean {
        log.info("remove minion from list: $minionPayload")
        return minionsList.remove(minionPayload.address, minionPayload)
    }

    private fun removeMinionFromExclude(minionPayload: MinionPayload): Boolean {
        log.info("remove minion from exclude: $minionPayload")
        return minionsExcluded.remove(minionPayload.address, minionPayload)
    }

    fun getMinionFromExclude(): MinionPayload? {
        val minionPayload = minionsExcluded.values.firstOrNull()
        log.info("get minion from exclude: $minionPayload")
        return minionPayload
    }

    fun getMd5Object(md5: String): Md5Object {
        val md5FromMap = md5Map[md5]
        return if (md5FromMap == null) {
            setMd5Object(md5, Md5Object())
            md5Map[md5]!!
        } else {
            md5FromMap
        }
    }

    fun setMd5Object(md5: String, md5Object: Md5Object) {
        md5Map[md5] = md5Object
    }

    @Scheduled(fixedDelay = 5000L)
    fun checkMinionsRequestsTimeout() {
        if (minionsList.isNotEmpty()) {
            val currentTime = Instant.now()
            minionsList.values.forEach {
                if (it.timeStamp.plusSeconds(properties.timeout).isBefore(currentTime)) {
                    minionsList.remove(it.address)
                }
            }
        }
    }
}
