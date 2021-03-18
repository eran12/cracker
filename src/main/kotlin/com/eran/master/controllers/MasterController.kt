package com.eran.master.controllers

import com.eran.master.modules.MinionPayload
import com.eran.master.services.MasterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@RestController
class MasterController(val masterService: MasterService) {

    @GetMapping("/{md5}")
    fun uploadMd5(@PathVariable("md5") md5: String): ResponseEntity<String> {
        return masterService.start(md5)
    }

    @PostMapping("/error")
    fun error(@RequestBody payload: MinionPayload) {
        masterService.handleError(payload)
    }

    @PostMapping("/fail")
    fun fail(@RequestBody payload: MinionPayload) {
        masterService.handleFail(payload)
    }

    @PostMapping("/success")
    fun success(@RequestBody payload: MinionPayload) {
        masterService.handleSuccess(payload)
    }

    @GetMapping("/check/{md5}")
    fun checkIfMd5Found(@PathVariable("md5") md5: String): ResponseEntity<String> {
        return masterService.checkIfMd5Found(md5)
    }
}
