package io.github.llmlibrary.examples

import io.github.llmlibrary.client.OllamaClient
import io.github.llmlibrary.config.{Config, LLMConfig}
import io.github.llmlibrary.models.{GenerateRequest, Options}
import zio._

/**
 * Example program demonstrating the Ollama generate endpoint.
 */
object OllamaGenerateExample extends ZIOAppDefault {

  val generateProgram: ZIO[OllamaClient, Throwable, Unit] = for {
    _ <- Console.printLine("=== Ollama Generate Example ===")
    _ <- Console.printLine("Sending generate request to Ollama...")

    // Create a generate request
    generateRequest = GenerateRequest(
      model = "llama2",
      prompt = "Write a haiku about programming:",
      system = Some("You are a creative poet."),
      stream = Some(false),
      options = Some(Options(
        temperature = Some(0.8),
        numPredict = Some(100)
      ))
    )

    // Send the request and wait for response
    response <- OllamaClient.generate(generateRequest)

    // Print the response
    _ <- Console.printLine("\n--- Generated Text ---")
    _ <- Console.printLine(response.response)
    _ <- Console.printLine("\n--- Performance Metrics ---")
    _ <- response.totalDuration.map(d => Console.printLine(s"Total Duration: ${d / 1000000}ms")).getOrElse(ZIO.unit)
    _ <- response.evalCount.map(c => Console.printLine(s"Tokens Generated: $c")).getOrElse(ZIO.unit)
    _ <- Console.printLine("\n✓ Generation completed successfully!")

  } yield ()

  override def run: ZIO[Any, Throwable, Unit] = {
    generateProgram.provide(
      Config.live,
      ZLayer.fromFunction((config: LLMConfig) => config.ollama),
      OllamaClient.live
    ).catchAll { error =>
      Console.printLine(s"Error: ${error.getMessage}") *>
      ZIO.fail(error)
    }
  }
}
