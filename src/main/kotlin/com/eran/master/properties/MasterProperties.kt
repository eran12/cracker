package com.eran.master.properties

import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * @author Eran Eichenbaum - 12/03/2021.
 */
@ConfigurationProperties(prefix = "master")
@ConstructorBinding
data class MasterProperties(
    @NotEmpty
    val minions: Set<String> = emptySet(),

    @Min(0)
    val maxAttempts: Int = 2,
    @Min(0)
    val increment: Int = 10,
    val timeout: Long = increment.times(2).toLong()
)
