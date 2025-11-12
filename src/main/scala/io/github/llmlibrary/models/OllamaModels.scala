package io.github.llmlibrary.models

import zio.json._

// ==================== Common Models ====================

case class Message(
  role: String,
  content: String,
  @jsonField("images") images: Option[List[String]] = None
)

object Message {
  implicit val encoder: JsonEncoder[Message] = DeriveJsonEncoder.gen[Message]
  implicit val decoder: JsonDecoder[Message] = DeriveJsonDecoder.gen[Message]

  def user(content: String, images: Option[List[String]] = None): Message =
    Message("user", content, images)

  def assistant(content: String): Message =
    Message("assistant", content, None)

  def system(content: String): Message =
    Message("system", content, None)
}

case class Options(
  @jsonField("num_predict") numPredict: Option[Int] = None,
  @jsonField("top_k") topK: Option[Int] = None,
  @jsonField("top_p") topP: Option[Double] = None,
  @jsonField("temperature") temperature: Option[Double] = None,
  @jsonField("repeat_penalty") repeatPenalty: Option[Double] = None,
  @jsonField("seed") seed: Option[Int] = None,
  @jsonField("num_ctx") numCtx: Option[Int] = None,
  @jsonField("num_batch") numBatch: Option[Int] = None,
  @jsonField("num_gpu") numGpu: Option[Int] = None,
  @jsonField("main_gpu") mainGpu: Option[Int] = None,
  @jsonField("low_vram") lowVram: Option[Boolean] = None,
  @jsonField("f16_kv") f16Kv: Option[Boolean] = None,
  @jsonField("vocab_only") vocabOnly: Option[Boolean] = None,
  @jsonField("use_mmap") useMmap: Option[Boolean] = None,
  @jsonField("use_mlock") useMlock: Option[Boolean] = None,
  @jsonField("num_thread") numThread: Option[Int] = None,
  @jsonField("num_keep") numKeep: Option[Int] = None,
  @jsonField("typical_p") typicalP: Option[Double] = None,
  @jsonField("repeat_last_n") repeatLastN: Option[Int] = None,
  @jsonField("tfs_z") tfsZ: Option[Double] = None,
  @jsonField("mirostat") mirostat: Option[Int] = None,
  @jsonField("mirostat_tau") mirostatTau: Option[Double] = None,
  @jsonField("mirostat_eta") mirostatEta: Option[Double] = None,
  @jsonField("penalize_newline") penalizeNewline: Option[Boolean] = None,
  @jsonField("stop") stop: Option[List[String]] = None
)

object Options {
  implicit val encoder: JsonEncoder[Options] = DeriveJsonEncoder.gen[Options]
  implicit val decoder: JsonDecoder[Options] = DeriveJsonDecoder.gen[Options]

  val default: Options = Options()
}

// ==================== Chat API ====================

case class ChatRequest(
  @jsonField("model") model: String,
  @jsonField("messages") messages: List[Message],
  @jsonField("stream") stream: Option[Boolean] = Some(false),
  @jsonField("format") format: Option[String] = None, // "json" for JSON response
  @jsonField("options") options: Option[Options] = None,
  @jsonField("keep_alive") keepAlive: Option[String] = None
)

object ChatRequest {
  implicit val encoder: JsonEncoder[ChatRequest] = DeriveJsonEncoder.gen[ChatRequest]
  implicit val decoder: JsonDecoder[ChatRequest] = DeriveJsonDecoder.gen[ChatRequest]
}

case class ChatResponse(
  @jsonField("model") model: String,
  @jsonField("created_at") createdAt: String,
  @jsonField("message") message: Message,
  @jsonField("done") done: Boolean,
  @jsonField("total_duration") totalDuration: Option[Long] = None,
  @jsonField("load_duration") loadDuration: Option[Long] = None,
  @jsonField("prompt_eval_count") promptEvalCount: Option[Int] = None,
  @jsonField("prompt_eval_duration") promptEvalDuration: Option[Long] = None,
  @jsonField("eval_count") evalCount: Option[Int] = None,
  @jsonField("eval_duration") evalDuration: Option[Long] = None
)

object ChatResponse {
  implicit val encoder: JsonEncoder[ChatResponse] = DeriveJsonEncoder.gen[ChatResponse]
  implicit val decoder: JsonDecoder[ChatResponse] = DeriveJsonDecoder.gen[ChatResponse]
}

// ==================== Generate API ====================

case class GenerateRequest(
  @jsonField("model") model: String,
  @jsonField("prompt") prompt: String,
  @jsonField("system") system: Option[String] = None,
  @jsonField("template") template: Option[String] = None,
  @jsonField("context") context: Option[List[Int]] = None,
  @jsonField("stream") stream: Option[Boolean] = Some(false),
  @jsonField("raw") raw: Option[Boolean] = None,
  @jsonField("format") format: Option[String] = None,
  @jsonField("images") images: Option[List[String]] = None,
  @jsonField("options") options: Option[Options] = None,
  @jsonField("keep_alive") keepAlive: Option[String] = None
)

object GenerateRequest {
  implicit val encoder: JsonEncoder[GenerateRequest] = DeriveJsonEncoder.gen[GenerateRequest]
  implicit val decoder: JsonDecoder[GenerateRequest] = DeriveJsonDecoder.gen[GenerateRequest]
}

case class GenerateResponse(
  @jsonField("model") model: String,
  @jsonField("created_at") createdAt: String,
  @jsonField("response") response: String,
  @jsonField("done") done: Boolean,
  @jsonField("context") context: Option[List[Int]] = None,
  @jsonField("total_duration") totalDuration: Option[Long] = None,
  @jsonField("load_duration") loadDuration: Option[Long] = None,
  @jsonField("prompt_eval_count") promptEvalCount: Option[Int] = None,
  @jsonField("prompt_eval_duration") promptEvalDuration: Option[Long] = None,
  @jsonField("eval_count") evalCount: Option[Int] = None,
  @jsonField("eval_duration") evalDuration: Option[Long] = None
)

object GenerateResponse {
  implicit val encoder: JsonEncoder[GenerateResponse] = DeriveJsonEncoder.gen[GenerateResponse]
  implicit val decoder: JsonDecoder[GenerateResponse] = DeriveJsonDecoder.gen[GenerateResponse]
}

// ==================== Tags API ====================

case class ModelInfo(
  @jsonField("name") name: String,
  @jsonField("modified_at") modifiedAt: String,
  @jsonField("size") size: Long,
  @jsonField("digest") digest: String,
  @jsonField("details") details: Option[ModelDetails] = None
)

object ModelInfo {
  implicit val encoder: JsonEncoder[ModelInfo] = DeriveJsonEncoder.gen[ModelInfo]
  implicit val decoder: JsonDecoder[ModelInfo] = DeriveJsonDecoder.gen[ModelInfo]
}

case class ModelDetails(
  @jsonField("format") format: Option[String] = None,
  @jsonField("family") family: Option[String] = None,
  @jsonField("families") families: Option[List[String]] = None,
  @jsonField("parameter_size") parameterSize: Option[String] = None,
  @jsonField("quantization_level") quantizationLevel: Option[String] = None
)

object ModelDetails {
  implicit val encoder: JsonEncoder[ModelDetails] = DeriveJsonEncoder.gen[ModelDetails]
  implicit val decoder: JsonDecoder[ModelDetails] = DeriveJsonDecoder.gen[ModelDetails]
}

case class TagsResponse(
  @jsonField("models") models: List[ModelInfo]
)

object TagsResponse {
  implicit val encoder: JsonEncoder[TagsResponse] = DeriveJsonEncoder.gen[TagsResponse]
  implicit val decoder: JsonDecoder[TagsResponse] = DeriveJsonDecoder.gen[TagsResponse]
}

// ==================== Pull API ====================

case class PullRequest(
  @jsonField("name") name: String,
  @jsonField("insecure") insecure: Option[Boolean] = None,
  @jsonField("stream") stream: Option[Boolean] = Some(true)
)

object PullRequest {
  implicit val encoder: JsonEncoder[PullRequest] = DeriveJsonEncoder.gen[PullRequest]
  implicit val decoder: JsonDecoder[PullRequest] = DeriveJsonDecoder.gen[PullRequest]
}

case class PullResponse(
  @jsonField("status") status: String,
  @jsonField("digest") digest: Option[String] = None,
  @jsonField("total") total: Option[Long] = None,
  @jsonField("completed") completed: Option[Long] = None
)

object PullResponse {
  implicit val encoder: JsonEncoder[PullResponse] = DeriveJsonEncoder.gen[PullResponse]
  implicit val decoder: JsonDecoder[PullResponse] = DeriveJsonDecoder.gen[PullResponse]
}

// ==================== Delete API ====================

case class DeleteRequest(
  @jsonField("name") name: String
)

object DeleteRequest {
  implicit val encoder: JsonEncoder[DeleteRequest] = DeriveJsonEncoder.gen[DeleteRequest]
  implicit val decoder: JsonDecoder[DeleteRequest] = DeriveJsonDecoder.gen[DeleteRequest]
}

case class DeleteResponse(
  @jsonField("status") status: Option[String] = None
)

object DeleteResponse {
  implicit val encoder: JsonEncoder[DeleteResponse] = DeriveJsonEncoder.gen[DeleteResponse]
  implicit val decoder: JsonDecoder[DeleteResponse] = DeriveJsonDecoder.gen[DeleteResponse]
}

// ==================== Show API ====================

case class ShowRequest(
  @jsonField("name") name: String
)

object ShowRequest {
  implicit val encoder: JsonEncoder[ShowRequest] = DeriveJsonEncoder.gen[ShowRequest]
  implicit val decoder: JsonDecoder[ShowRequest] = DeriveJsonDecoder.gen[ShowRequest]
}

case class ShowResponse(
  @jsonField("modelfile") modelfile: Option[String] = None,
  @jsonField("parameters") parameters: Option[String] = None,
  @jsonField("template") template: Option[String] = None,
  @jsonField("details") details: Option[ModelDetails] = None,
  @jsonField("model_info") modelInfo: Option[Map[String, String]] = None
)

object ShowResponse {
  implicit val encoder: JsonEncoder[ShowResponse] = DeriveJsonEncoder.gen[ShowResponse]
  implicit val decoder: JsonDecoder[ShowResponse] = DeriveJsonDecoder.gen[ShowResponse]
}

// ==================== Copy API ====================

case class CopyRequest(
  @jsonField("source") source: String,
  @jsonField("destination") destination: String
)

object CopyRequest {
  implicit val encoder: JsonEncoder[CopyRequest] = DeriveJsonEncoder.gen[CopyRequest]
  implicit val decoder: JsonDecoder[CopyRequest] = DeriveJsonDecoder.gen[CopyRequest]
}

// ==================== Create API ====================

case class CreateRequest(
  @jsonField("name") name: String,
  @jsonField("modelfile") modelfile: String,
  @jsonField("stream") stream: Option[Boolean] = Some(true)
)

object CreateRequest {
  implicit val encoder: JsonEncoder[CreateRequest] = DeriveJsonEncoder.gen[CreateRequest]
  implicit val decoder: JsonDecoder[CreateRequest] = DeriveJsonDecoder.gen[CreateRequest]
}

case class CreateResponse(
  @jsonField("status") status: String
)

object CreateResponse {
  implicit val encoder: JsonEncoder[CreateResponse] = DeriveJsonEncoder.gen[CreateResponse]
  implicit val decoder: JsonDecoder[CreateResponse] = DeriveJsonDecoder.gen[CreateResponse]
}

// ==================== Embeddings API ====================

case class EmbeddingsRequest(
  @jsonField("model") model: String,
  @jsonField("prompt") prompt: String,
  @jsonField("options") options: Option[Options] = None,
  @jsonField("keep_alive") keepAlive: Option[String] = None
)

object EmbeddingsRequest {
  implicit val encoder: JsonEncoder[EmbeddingsRequest] = DeriveJsonEncoder.gen[EmbeddingsRequest]
  implicit val decoder: JsonDecoder[EmbeddingsRequest] = DeriveJsonDecoder.gen[EmbeddingsRequest]
}

case class EmbeddingsResponse(
  @jsonField("embedding") embedding: List[Double]
)

object EmbeddingsResponse {
  implicit val encoder: JsonEncoder[EmbeddingsResponse] = DeriveJsonEncoder.gen[EmbeddingsResponse]
  implicit val decoder: JsonDecoder[EmbeddingsResponse] = DeriveJsonDecoder.gen[EmbeddingsResponse]
}
