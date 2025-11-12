package io.github.llmlibrary.client

import io.github.llmlibrary.config.OllamaConfig
import io.github.llmlibrary.models._
import zio._
import zio.http._
import zio.json._

trait OllamaClient {
  /**
   * Generate a chat completion
   */
  def chat(request: ChatRequest): Task[ChatResponse]

  /**
   * Generate a completion
   */
  def generate(request: GenerateRequest): Task[GenerateResponse]

  /**
   * List local models
   */
  def tags(): Task[TagsResponse]

  /**
   * Pull a model from the Ollama library
   */
  def pull(request: PullRequest): Task[Unit]

  /**
   * Delete a model
   */
  def delete(request: DeleteRequest): Task[Unit]

  /**
   * Show model information
   */
  def show(request: ShowRequest): Task[ShowResponse]

  /**
   * Copy a model
   */
  def copy(request: CopyRequest): Task[Unit]

  /**
   * Create a model from a Modelfile
   */
  def create(request: CreateRequest): Task[Unit]

  /**
   * Generate embeddings
   */
  def embeddings(request: EmbeddingsRequest): Task[EmbeddingsResponse]
}

object OllamaClient {

  case class OllamaClientLive(config: OllamaConfig, client: Client) extends OllamaClient {

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

    private def delete[Req: JsonEncoder](endpoint: String, request: Req): Task[Unit] = {
      val url = baseUrl.addPath(endpoint)
      val body = Body.fromString(request.toJson)

      for {
        response <- client.request(
          Request.delete(url, body).addHeader(Header.ContentType(MediaType.application.json))
        )
        _ <- response.status match {
          case Status.Ok => ZIO.unit
          case status =>
            response.body.asString.flatMap(bodyText =>
              ZIO.fail(new RuntimeException(s"Request failed with status $status: $bodyText"))
            )
        }
      } yield ()
    }

    override def chat(request: ChatRequest): Task[ChatResponse] =
      post[ChatRequest, ChatResponse]("/api/chat", request)

    override def generate(request: GenerateRequest): Task[GenerateResponse] =
      post[GenerateRequest, GenerateResponse]("/api/generate", request)

    override def tags(): Task[TagsResponse] =
      get[TagsResponse]("/api/tags")

    override def pull(request: PullRequest): Task[Unit] = {
      // For pull, we need to handle streaming responses
      // For simplicity, we'll wait for the final response
      val url = baseUrl.addPath("/api/pull")
      val body = Body.fromString(request.toJson)

      for {
        response <- client.request(
          Request.post(url, body).addHeader(Header.ContentType(MediaType.application.json))
        )
        _ <- response.status match {
          case Status.Ok => ZIO.unit
          case status =>
            response.body.asString.flatMap(bodyText =>
              ZIO.fail(new RuntimeException(s"Pull failed with status $status: $bodyText"))
            )
        }
      } yield ()
    }

    override def delete(request: DeleteRequest): Task[Unit] =
      delete[DeleteRequest]("/api/delete", request)

    override def show(request: ShowRequest): Task[ShowResponse] =
      post[ShowRequest, ShowResponse]("/api/show", request)

    override def copy(request: CopyRequest): Task[Unit] = {
      val url = baseUrl.addPath("/api/copy")
      val body = Body.fromString(request.toJson)

      for {
        response <- client.request(
          Request.post(url, body).addHeader(Header.ContentType(MediaType.application.json))
        )
        _ <- response.status match {
          case Status.Ok => ZIO.unit
          case status =>
            response.body.asString.flatMap(bodyText =>
              ZIO.fail(new RuntimeException(s"Copy failed with status $status: $bodyText"))
            )
        }
      } yield ()
    }

    override def create(request: CreateRequest): Task[Unit] = {
      val url = baseUrl.addPath("/api/create")
      val body = Body.fromString(request.toJson)

      for {
        response <- client.request(
          Request.post(url, body).addHeader(Header.ContentType(MediaType.application.json))
        )
        _ <- response.status match {
          case Status.Ok => ZIO.unit
          case status =>
            response.body.asString.flatMap(bodyText =>
              ZIO.fail(new RuntimeException(s"Create failed with status $status: $bodyText"))
            )
        }
      } yield ()
    }

    override def embeddings(request: EmbeddingsRequest): Task[EmbeddingsResponse] =
      post[EmbeddingsRequest, EmbeddingsResponse]("/api/embeddings", request)
  }

  /**
   * ZIO Layer for OllamaClient
   */
  val live: ZLayer[OllamaConfig, Throwable, OllamaClient] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[OllamaConfig]
        client <- ZIO.succeed(Client.default)
      } yield OllamaClientLive(config, client)
    }

  /**
   * Helper methods for easier access
   */
  def chat(request: ChatRequest): ZIO[OllamaClient, Throwable, ChatResponse] =
    ZIO.serviceWithZIO[OllamaClient](_.chat(request))

  def generate(request: GenerateRequest): ZIO[OllamaClient, Throwable, GenerateResponse] =
    ZIO.serviceWithZIO[OllamaClient](_.generate(request))

  def tags(): ZIO[OllamaClient, Throwable, TagsResponse] =
    ZIO.serviceWithZIO[OllamaClient](_.tags())

  def pull(request: PullRequest): ZIO[OllamaClient, Throwable, Unit] =
    ZIO.serviceWithZIO[OllamaClient](_.pull(request))

  def delete(request: DeleteRequest): ZIO[OllamaClient, Throwable, Unit] =
    ZIO.serviceWithZIO[OllamaClient](_.delete(request))

  def show(request: ShowRequest): ZIO[OllamaClient, Throwable, ShowResponse] =
    ZIO.serviceWithZIO[OllamaClient](_.show(request))

  def copy(request: CopyRequest): ZIO[OllamaClient, Throwable, Unit] =
    ZIO.serviceWithZIO[OllamaClient](_.copy(request))

  def create(request: CreateRequest): ZIO[OllamaClient, Throwable, Unit] =
    ZIO.serviceWithZIO[OllamaClient](_.create(request))

  def embeddings(request: EmbeddingsRequest): ZIO[OllamaClient, Throwable, EmbeddingsResponse] =
    ZIO.serviceWithZIO[OllamaClient](_.embeddings(request))
}
