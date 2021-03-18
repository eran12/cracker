package com.eran.minion

import java.util.*
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@SpringBootApplication
class MinionApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = SpringApplication(MinionApplication::class.java)
            app.setDefaultProperties(
                Collections
                    .singletonMap<String, Any>("server.port", "8083")
            )
            app.run(*args)
        }
    }
}
