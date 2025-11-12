package io.github.llmlibrary.examples

import io.github.llmlibrary.client.OllamaClient
import io.github.llmlibrary.config.{Config, LLMConfig}
import zio._

/**
 * Example program demonstrating listing all available Ollama models.
 */
object OllamaListModelsExample extends ZIOAppDefault {

  val listModelsProgram: ZIO[OllamaClient, Throwable, Unit] = for {
    _ <- Console.printLine("=== Ollama List Models Example ===")
    _ <- Console.printLine("Fetching available models...\n")

    // List all models
    response <- OllamaClient.tags()

    // Print each model
    _ <- Console.printLine(s"Found ${response.models.length} model(s):\n")
    _ <- ZIO.foreach(response.models) { model =>
      Console.printLine(s"Name: ${model.name}") *>
      Console.printLine(s"  Size: ${model.size / (1024 * 1024)} MB") *>
      Console.printLine(s"  Modified: ${model.modifiedAt}") *>
      Console.printLine(s"  Digest: ${model.digest.take(16)}...") *>
      model.details.flatMap(_.family).map(f =>
        Console.printLine(s"  Family: $f")
      ).getOrElse(ZIO.unit) *>
      Console.printLine("")
    }
    _ <- Console.printLine("✓ Models listed successfully!")

  } yield ()

  override def run: ZIO[Any, Throwable, Unit] = {
    listModelsProgram.provide(
      Config.live,
      ZLayer.fromFunction((config: LLMConfig) => config.ollama),
      OllamaClient.live
    ).catchAll { error =>
      Console.printLine(s"Error: ${error.getMessage}") *>
      ZIO.fail(error)
    }
  }
}
