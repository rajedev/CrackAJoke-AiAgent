package org.crackajoke.agent.org.crackajoke.agent

import kotlinx.coroutines.runBlocking

/**
 * Created by Rajendhiran Easu on 26/08/25.
 * Description: A simple console-based chat agent that interacts with the user.
 * It prompts the user for their name and then engages in a conversation,
 * responding with random phrases until the user types "/bye" to exit.
 */

fun main() = runBlocking {
    println("\n\nWelcome to the Crack A Joke Agent")
    println("Type /bye to exit the conversation")
    print("\nTell me your name, else I will call you User: ")

    val userName = readlnOrNull()?.takeIf { it.isNotBlank() } ?: "User"
    println("Agent: Thanks $userName, tell me what kind of joke you want?\n")

    while (true) {
        print("$userName: ")
        val userInput = readlnOrNull()
        if (userInput == "/bye") {
            println("Agent: Bye, Take care!")
            break
        }
        //println("Agent: ${getRandomResponse()}")
        userInput?.let {
            val result = aiAgent.run(it)
            println("Agent: $result")
        }
    }
    println("Conversation Ended \n")
}

fun getRandomResponse(): String {
    return listOf(
        "Hi", "how are you?", "What can I do for you?", "Tell me more",
        "Go on...", "Interesting", "I see", "Could you explain further?",
        "Why do you say that?", "How does that make you feel?"
    ).random()
}
