package io.github.llmlibrary.examples

import io.github.llmlibrary.client.LMStudioClient
import io.github.llmlibrary.config.{Config, LLMConfig}
import io.github.llmlibrary.models.{LMChatRequest, LMMessage}
import zio._

/**
 * Example program demonstrating the LM Studio chat endpoint.
 */
object LMStudioChatExample extends ZIOAppDefault {

  val chatProgram: ZIO[LMStudioClient, Throwable, Unit] = for {
    _ <- Console.printLine("=== LM Studio Chat Example ===")
    _ <- Console.printLine("Sending chat request to LM Studio...")

    // First, list available models
    modelsResponse <- LMStudioClient.models()
    modelName = modelsResponse.data.headOption.map(_.id).getOrElse("local-model")
    _ <- Console.printLine(s"Using model: $modelName\n")

    // Create a chat request
    chatRequest = LMChatRequest(
      model = modelName,
      messages = List(
        LMMessage.system("You are a helpful assistant."),
        LMMessage.user("What is the capital of France? Please answer in one sentence.")
      ),
      temperature = Some(0.7),
      maxTokens = Some(100)
    )

    // Send the request and wait for response
    response <- LMStudioClient.chat(chatRequest)

    // Print the response
    _ <- Console.printLine("--- Response ---")
    _ <- Console.printLine(s"Model: ${response.model}")
    _ <- response.choices.headOption match {
      case Some(choice) =>
        Console.printLine(s"Role: ${choice.message.role}") *>
        Console.printLine(s"Content: ${choice.message.content}") *>
        choice.finishReason.map(r => Console.printLine(s"Finish Reason: $r")).getOrElse(ZIO.unit)
      case None =>
        Console.printLine("No response choices received")
    }
    _ <- response.usage.map { usage =>
      Console.printLine(s"\n--- Token Usage ---") *>
      Console.printLine(s"Prompt Tokens: ${usage.promptTokens}") *>
      Console.printLine(s"Completion Tokens: ${usage.completionTokens}") *>
      Console.printLine(s"Total Tokens: ${usage.totalTokens}")
    }.getOrElse(ZIO.unit)
    _ <- Console.printLine("\n✓ Chat completed successfully!")

  } yield ()

  override def run: ZIO[Any, Throwable, Unit] = {
    chatProgram.provide(
      Config.live,
      ZLayer.fromFunction((config: LLMConfig) => config.lmStudio),
      LMStudioClient.live
    ).catchAll { error =>
      Console.printLine(s"Error: ${error.getMessage}") *>
      Console.printLine("\nMake sure:") *>
      Console.printLine("1. LM Studio is running") *>
      Console.printLine("2. A model is loaded in LM Studio") *>
      Console.printLine("3. The .env file has correct LMSTUDIO_HOST and LMSTUDIO_PORT") *>
      ZIO.fail(error)
    }
  }
}
