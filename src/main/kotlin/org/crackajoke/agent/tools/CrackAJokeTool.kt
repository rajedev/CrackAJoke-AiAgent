package org.crackajoke.agent.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.crackajoke.agent.network.APIClient
import org.crackajoke.agent.network.JokeApi

/**
 * Created by Rajendhiran Easu on 26/08/25.
 * Description: Tool to fetch a joke from the Joke API
 */

object CrackAJokeTool : SimpleTool<CrackAJokeTool.Args>() {

    val jokeApi: JokeApi by lazy {
        APIClient.createService(
            baseUrl = "https://v2.jokeapi.dev/",
            tClass = JokeApi::class.java
        )
    }

    @Serializable
    data class Args(
        val category: String,
        val type: String
    ) : ToolArgs

    override val argsSerializer: KSerializer<Args> = Args.serializer()

    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "fetchJoke",
        description = """"You can use tools. When a tool is called, 
                    wait and display its response exactly as received.
                    Only use the CrackAJokeTool for jokes — do not 
                    create or change jokes yourself."""".trimIndent(),
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "category",
                description = """"Map the joke category to one of the 
                    following: Any, Misc, Programming, Dark, Pun, Spooky, 
                    Christmas, or Miscellaneous.  You may resolve aliases 
                    to these categories (e.g., 'Coding' → Programming, 
                    'Halloween' → Spooky).  If no match is found, default 
                    to 'Any'."""".trimIndent(),
                type = ToolParameterType.String
            ),
            ToolParameterDescriptor(
                name = "type",
                description = """"Set the joke type to 'single' or 'twopart'. 
                    If not specified, default to 'twopart'."""".trimIndent(),
                type = ToolParameterType.String
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        try {
            val category = args.category
            val type = args.type.takeIf { it.isNotBlank() } ?: "twopart"
            //println("Joke Data: $category $type")
            val jokeResponse = jokeApi.getJoke(category, type)
            //println("JokeResponse: $jokeResponse")
            return if (jokeResponse.type == "single") {
                jokeResponse.joke ?: "No joke found."
            } else {
                "${jokeResponse.setup} - ${jokeResponse.delivery}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Failed to fetch joke - ${e.message}"
        }
    }
}
