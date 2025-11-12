package io.github.llmlibrary.client

import io.github.llmlibrary.config.LMStudioConfig
import io.github.llmlibrary.models._
import zio._
import zio.http._
import zio.json._

trait LMStudioClient {
  /**
   * Generate a chat completion (OpenAI-compatible)
   */
  def chat(request: LMChatRequest): Task[LMChatResponse]

  /**
   * Generate a completion (OpenAI-compatible)
   */
  def completion(request: LMCompletionRequest): Task[LMCompletionResponse]

  /**
   * List available models
   */
  def models(): Task[LMModelsResponse]

  /**
   * Generate embeddings
   */
  def embeddings(request: LMEmbeddingsRequest): Task[LMEmbeddingsResponse]
}

object LMStudioClient {

  case class LMStudioClientLive(config: LMStudioConfig, client: Client) extends LMStudioClient {

    private val baseUrl = URL.decode(config.baseUrl).toOption.get

    private def post[Req: JsonEncoder, Res: JsonDecoder](
      endpoint: String,
      request: Req
    ): Task[Res] = {
      val url = baseUrl.addPath(endpoint)
      val body = Body.fromString(request.toJson)

      for {
        response <- client.request(
          Request.post(url, body).addHeader(Header.ContentType(MediaType.application.json))
        )
        bodyText <- response.body.asString
        result <- response.status match {
          case Status.Ok =>
            ZIO.fromEither(bodyText.fromJson[Res])
              .mapError(err => new RuntimeException(s"Failed to decode response: $err. Body: $bodyText"))
          case status =>
            ZIO.fail(new RuntimeException(s"Request failed with status $status: $bodyText"))
        }
      } yield result
    }

    private def get[Res: JsonDecoder](endpoint: String): Task[Res] = {
      val url = baseUrl.addPath(endpoint)

      for {
        response <- client.request(Request.get(url))
        bodyText <- response.body.asString
        result <- response.status match {
          case Status.Ok =>
            ZIO.fromEither(bodyText.fromJson[Res])
              .mapError(err => new RuntimeException(s"Failed to decode response: $err. Body: $bodyText"))
          case status =>
            ZIO.fail(new RuntimeException(s"Request failed with status $status: $bodyText"))
        }
      } yield result
    }

    override def chat(request: LMChatRequest): Task[LMChatResponse] =
      post[LMChatRequest, LMChatResponse]("/v1/chat/completions", request)

    override def completion(request: LMCompletionRequest): Task[LMCompletionResponse] =
      post[LMCompletionRequest, LMCompletionResponse]("/v1/completions", request)

    override def models(): Task[LMModelsResponse] =
      get[LMModelsResponse]("/v1/models")

    override def embeddings(request: LMEmbeddingsRequest): Task[LMEmbeddingsResponse] =
      post[LMEmbeddingsRequest, LMEmbeddingsResponse]("/v1/embeddings", request)
  }

  /**
   * ZIO Layer for LMStudioClient
   */
  val live: ZLayer[LMStudioConfig, Throwable, LMStudioClient] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[LMStudioConfig]
        client <- ZIO.succeed(Client.default)
      } yield LMStudioClientLive(config, client)
    }

  /**
   * Helper methods for easier access
   */
  def chat(request: LMChatRequest): ZIO[LMStudioClient, Throwable, LMChatResponse] =
    ZIO.serviceWithZIO[LMStudioClient](_.chat(request))

  def completion(request: LMCompletionRequest): ZIO[LMStudioClient, Throwable, LMCompletionResponse] =
    ZIO.serviceWithZIO[LMStudioClient](_.completion(request))

  def models(): ZIO[LMStudioClient, Throwable, LMModelsResponse] =
    ZIO.serviceWithZIO[LMStudioClient](_.models())

  def embeddings(request: LMEmbeddingsRequest): ZIO[LMStudioClient, Throwable, LMEmbeddingsResponse] =
    ZIO.serviceWithZIO[LMStudioClient](_.embeddings(request))
}
