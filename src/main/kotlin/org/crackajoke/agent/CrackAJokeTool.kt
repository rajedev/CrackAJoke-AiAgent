package org.crackajoke.agent.org.crackajoke.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Created by Rajendhiran Easu on 26/08/25.
 * Description:
 */

@Serializable
data class JokeResponse(
    val type: String?,
    val joke: String?,
    val setup: String?,
    val delivery: String?
)

interface JokeApi {
    @GET("joke/{category}")
    suspend fun getJoke(
        @Path("category") category: String,
        @Query("type") type: String = "single"
    ): JokeResponse
}

private val format = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

const val NETWORK_TIMEOUT_SECONDS = 10L

val connectionPool by lazy {
    ConnectionPool(5, 5, TimeUnit.MINUTES)
}

val okHttpClient by lazy {
    OkHttpClient().newBuilder()
        .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectionPool(connectionPool)
        .build()
}
val contentType = "application/json".toMediaType()

@OptIn(ExperimentalSerializationApi::class)
val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("https://v2.jokeapi.dev/")
        .addConverterFactory(format.asConverterFactory(contentType))
        .client(okHttpClient)
        .build()
}

val jokeApi: JokeApi by lazy { retrofit.create(JokeApi::class.java) }

val aiAgent = AIAgent(
    executor = simpleOllamaAIExecutor("http://127.0.0.1:11434"),
    systemPrompt = """
You must use tools when available.
Do not answer the user's question yourself if a tool is available.
If a tool is called, wait for its response and do not continue generating your own answer.
Always use CrackAJokeTool when asked for a joke, and do not create or finish the joke yourself.
""".trimIndent(),    //strategy = singleRunStrategy(),
    llmModel = LLModel(
        LLMProvider.Ollama, id = "llama3.2:latest",
        capabilities = listOf(
            //LLMCapability.Temperature,
            LLMCapability.Tools,
            //LLMCapability.Schema.JSON.Simple,
            LLMCapability.ToolChoice
        )
    ),
    toolRegistry = ToolRegistry {
        tool(CrackAJokeTool)
    }
)

object CrackAJokeTool : SimpleTool<CrackAJokeTool.Args>() {
    @Serializable
    data class Args(
        val category: String,
        val type: String
    ) : ToolArgs

    override val argsSerializer: KSerializer<Args> = Args.serializer()

    override val descriptor: ToolDescriptor = ToolDescriptor(
        name = "fetchJoke",
        description = "You have access to tools. \n" +
                "    If a tool is called, wait for the tool response and display the same response, you no need to do anything. \n" +
                "    Only use the tool `CrackAJokeTool` to tell a joke. \n" +
                "    Never create or modify the joke yourself.",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "category",
                description = "The category of the joke " +
                        "(Any, Misc, Programming, Dark, Pun, Spooky, Christmas, Miscellaneous), " +
                        "you can make the alias to the above resolved category " +
                        "For Eg. Coding, Code Integration like related to be resolved as Programming " +
                        "and similarly for eg. Halloween into Spooky etc, " +
                        "if nothing matches, we can go with ANY as a category",
                type = ToolParameterType.String
            ),
            ToolParameterDescriptor(
                name = "type",
                description = "The type of joke (single or twopart)) if there is no mentions, " +
                        "treat it as a twopart",
                type = ToolParameterType.String
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        val category = args.category
        val type = args.type.takeIf { it.isNotBlank() } ?: "twopart"
        println("Joke Data: $category $type")
        val jokeResponse = jokeApi.getJoke(category, type)
        println("JokeResponse: $jokeResponse")
        return if (jokeResponse.type == "single") {
            jokeResponse.joke ?: "No joke found."
        } else {
            "${jokeResponse.setup} - ${jokeResponse.delivery}"
        }
    }
}
