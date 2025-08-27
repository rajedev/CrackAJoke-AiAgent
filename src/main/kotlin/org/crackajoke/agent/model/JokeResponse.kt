package org.crackajoke.agent.model

import kotlinx.serialization.Serializable

/**
 * Created by Rajendhiran Easu on 27/08/25.
 * Description: Data class to map the joke API response
 */

@Serializable
data class JokeResponse(
    val type: String?,
    val joke: String?,
    val setup: String?,
    val delivery: String?
)
