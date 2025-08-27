package org.crackajoke.agent

import ai.koog.agents.core.agent.AIAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.ConnectException

/**
 * Created by Rajendhiran Easu on 26/08/25.
 * Description: A simple console-based chat agent that interacts with the user.
 * It prompts the user for their name and then engages in a conversation,
 * responding with random phrases until the user types "/bye" to exit.
 */

fun main() = runBlocking {
    val userName = initializeUser()
    val aiAgent by lazy {
        val config = AiExecutor.Ollama.getConfig()
        AIAgent(
            executor = config.executor,
            systemPrompt = config.systemPrompt,
            llmModel = config.llm,
            toolRegistry = config.tools
        )
    }
    while (true) {
        print("$userName: ")
        val userInput = readlnOrNull()
        if (userInput == "/bye") {
            println("Agent: Bye, Take care!")
            break
        }
        userInput?.let {
            val (spinner, maxLen) = loading()
            var result: String
            try {
                result = aiAgent.run(it)
            } catch (_: ConnectException) {
                println("AI Model is Unavailable")
                break
            } catch (e: Exception) {
                println("Error occurred ${e.message}")
                break
            } finally {
                spinner.cancelAndJoin()
                clearAgentLoading(maxLen)
            }
            print("Agent: $result \n")
        }
    }
}

private fun initializeUser(): String {
    println("\n\nWelcome to the Crack A Joke Agent")
    println("Type /bye to exit the conversation")
    print("\nTell me your name, else I will call you User: ")

    val userName = readlnOrNull()?.takeIf { it.isNotBlank() } ?: "User"
    println("Agent: Thanks $userName, tell me what kind of joke you want?\n")
    return userName
}

private fun clearAgentLoading(maxLen: Int) {
    print("\r${" ".repeat(maxLen)}\r")
}

private fun CoroutineScope.loading(): Pair<Job, Int> {
    val symbols = listOf("🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘")
    val prefix = "AI Agent: "
    val maxLength = prefix.length + 2
    return Pair(first = launch {
        var i = 0
        while (isActive) {
            print("\rAgent: ${symbols[i++ % symbols.size]}")
            delay(100L)
        }
    }, second = maxLength)
}
