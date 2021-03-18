package com.eran.minion.controller

import com.eran.minion.module.MinionPayload
import com.eran.minion.service.MinionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@RestController
class MinionController(private val minionService: MinionService) {

    @PostMapping
    fun calculate(@RequestBody minionPayload: MinionPayload) {
        minionService.startCalculate(minionPayload)
    }
}
