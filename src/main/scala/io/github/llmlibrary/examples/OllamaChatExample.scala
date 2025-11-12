package io.github.llmlibrary.examples

import io.github.llmlibrary.client.OllamaClient
import io.github.llmlibrary.config.{Config, LLMConfig}
import io.github.llmlibrary.models.{ChatRequest, Message, Options}
import zio._

/**
 * Simple example program that demonstrates using the Ollama chat endpoint.
 * This program sends a chat message to Ollama and waits for a response.
 */
object OllamaChatExample extends ZIOAppDefault {

  val chatProgram: ZIO[OllamaClient, Throwable, Unit] = for {
    _ <- Console.printLine("=== Ollama Chat Example ===")
    _ <- Console.printLine("Sending chat request to Ollama...")

    // Create a chat request with a simple question
    chatRequest = ChatRequest(
      model = "llama2", // Change this to your installed model
      messages = List(
        Message.system("You are a helpful assistant."),
        Message.user("What is the capital of France? Please answer in one sentence.")
      ),
      stream = Some(false),
      options = Some(Options(
        temperature = Some(0.7),
        topP = Some(0.9),
        numPredict = Some(100)
      ))
    )

    // Send the request and wait for response
    response <- OllamaClient.chat(chatRequest)

    // Print the response
    _ <- Console.printLine("\n--- Response ---")
    _ <- Console.printLine(s"Model: ${response.model}")
    _ <- Console.printLine(s"Role: ${response.message.role}")
    _ <- Console.printLine(s"Content: ${response.message.content}")
    _ <- Console.printLine(s"\n--- Performance Metrics ---")
    _ <- response.totalDuration.map(d => Console.printLine(s"Total Duration: ${d / 1000000}ms")).getOrElse(ZIO.unit)
    _ <- response.evalCount.map(c => Console.printLine(s"Tokens Generated: $c")).getOrElse(ZIO.unit)
    _ <- Console.printLine("\n✓ Chat completed successfully!")

  } yield ()

  override def run: ZIO[Any, Throwable, Unit] = {
    chatProgram.provide(
      Config.live,
      ZLayer.fromFunction((config: LLMConfig) => config.ollama),
      OllamaClient.live
    ).catchAll { error =>
      Console.printLine(s"Error: ${error.getMessage}") *>
      Console.printLine("\nMake sure:") *>
      Console.printLine("1. Ollama is running (run 'ollama serve' in terminal)") *>
      Console.printLine("2. You have pulled a model (run 'ollama pull llama2')") *>
      Console.printLine("3. The .env file has correct OLLAMA_HOST and OLLAMA_PORT") *>
      ZIO.fail(error)
    }
  }
}
