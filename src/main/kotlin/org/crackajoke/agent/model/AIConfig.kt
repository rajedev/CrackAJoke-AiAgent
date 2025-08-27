package org.crackajoke.agent.model

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLModel

/**
 * Created by Rajendhiran Easu on 27/08/25.
 * Description: Configuration data class for the AI agent
 */

data class AIConfig(
    val executor: SingleLLMPromptExecutor,
    val llm: LLModel,
    val systemPrompt: String,
    val tools: ToolRegistry
)
