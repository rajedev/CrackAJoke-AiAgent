package org.crackajoke.agent

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import org.crackajoke.agent.model.AIConfig
import org.crackajoke.agent.tools.CrackAJokeTool

/**
 * Created by Rajendhiran Easu on 27/08/25.
 * Description: Enum class to manage different AI executors and their configurations
 */

enum class AiExecutor {
    Ollama {
        override fun getConfig() = AIConfig(
            executor = simpleOllamaAIExecutor(baseUrl = "http://127.0.0.1:11434"),
            llm = model("gpt-oss:20b", listOf(LLMCapability.Tools, LLMCapability.ToolChoice), LLMProvider.Ollama),
            systemPrompt = systemPrompt,
            tools = toolsRegistry
        )
    },
    OPENAI {
        override fun getConfig() = AIConfig(
            executor = simpleOpenAIExecutor(""),
            llm = model("", listOf(LLMCapability.Tools, LLMCapability.ToolChoice), LLMProvider.OpenAI),
            systemPrompt = systemPrompt,
            tools = toolsRegistry
        )
    },
    GEMINAI {
        override fun getConfig() = AIConfig(
            executor = simpleGoogleAIExecutor(""),
            llm = model("gemini-1.5-pro", listOf(LLMCapability.Tools, LLMCapability.ToolChoice), LLMProvider.Google),
            systemPrompt = systemPrompt,
            tools = toolsRegistry
        )
    };

    abstract fun getConfig(): AIConfig
}

private val systemPrompt = """
                You must use tools when available.
                Do not answer the user's question yourself if a tool is available.
                If a tool is called, wait for its response and do not 
                continue generating your own answer.  Always use CrackAJokeTool 
                when asked for a joke, and do not create or finish the joke yourself.
                """.trimIndent()

private val model: (String, List<LLMCapability>, LLMProvider) -> LLModel = { modelName, llmCapability, llmProvider ->
    LLModel(
        provider = llmProvider,
        id = modelName,
        capabilities = llmCapability,
        contextLength = 8192
    )
}

private val toolsRegistry by lazy {
    ToolRegistry {
        tool(CrackAJokeTool)
    }
}
