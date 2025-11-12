package io.github.llmlibrary.config

import zio._
import scala.io.Source

case class OllamaConfig(
  host: String,
  port: Int
) {
  def baseUrl: String = s"http://$host:$port"
}

case class LMStudioConfig(
  host: String,
  port: Int
) {
  def baseUrl: String = s"http://$host:$port"
}

case class LLMConfig(
  ollama: OllamaConfig,
  lmStudio: LMStudioConfig
)

object Config {

  /**
   * Load configuration from .env file
   */
  def loadFromEnv(envFile: String = ".env"): Task[LLMConfig] = ZIO.attempt {
    val envVars = if (new java.io.File(envFile).exists()) {
      val source = Source.fromFile(envFile)
      try {
        source.getLines()
          .filterNot(line => line.trim.isEmpty || line.trim.startsWith("#"))
          .map { line =>
            val parts = line.split("=", 2)
            if (parts.length == 2) {
              parts(0).trim -> parts(1).trim
            } else {
              "" -> ""
            }
          }
          .filterNot(_._1.isEmpty)
          .toMap
      } finally {
        source.close()
      }
    } else {
      Map.empty[String, String]
    }

    def getEnvOrDefault(key: String, default: String): String =
      envVars.getOrElse(key, sys.env.getOrElse(key, default))

    val ollamaHost = getEnvOrDefault("OLLAMA_HOST", "localhost")
    val ollamaPort = getEnvOrDefault("OLLAMA_PORT", "11434").toInt

    val lmStudioHost = getEnvOrDefault("LMSTUDIO_HOST", "localhost")
    val lmStudioPort = getEnvOrDefault("LMSTUDIO_PORT", "1234").toInt

    LLMConfig(
      ollama = OllamaConfig(ollamaHost, ollamaPort),
      lmStudio = LMStudioConfig(lmStudioHost, lmStudioPort)
    )
  }

  /**
   * ZIO Layer for configuration
   */
  val live: ZLayer[Any, Throwable, LLMConfig] =
    ZLayer.fromZIO(loadFromEnv())
}
