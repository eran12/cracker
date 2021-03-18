package com.eran.master

import com.eran.master.properties.MasterProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

/**
 * @author Eran Eichenbaum - 10/03/2021.
 */
@SpringBootApplication
@EnableConfigurationProperties(MasterProperties::class)
class MasterApplication

fun main(args: Array<String>) {
    runApplication<MasterApplication>(*args)
}
