package io.github.llmlibrary.models

import zio.json._

// LM Studio follows OpenAI-compatible API

// ==================== Common Models ====================

case class LMMessage(
  @jsonField("role") role: String,
  @jsonField("content") content: String
)

object LMMessage {
  implicit val encoder: JsonEncoder[LMMessage] = DeriveJsonEncoder.gen[LMMessage]
  implicit val decoder: JsonDecoder[LMMessage] = DeriveJsonDecoder.gen[LMMessage]

  def user(content: String): LMMessage = LMMessage("user", content)
  def assistant(content: String): LMMessage = LMMessage("assistant", content)
  def system(content: String): LMMessage = LMMessage("system", content)
}

// ==================== Chat Completions API ====================

case class LMChatRequest(
  @jsonField("model") model: String,
  @jsonField("messages") messages: List[LMMessage],
  @jsonField("temperature") temperature: Option[Double] = Some(0.7),
  @jsonField("top_p") topP: Option[Double] = Some(1.0),
  @jsonField("n") n: Option[Int] = Some(1),
  @jsonField("stream") stream: Option[Boolean] = Some(false),
  @jsonField("stop") stop: Option[List[String]] = None,
  @jsonField("max_tokens") maxTokens: Option[Int] = None,
  @jsonField("presence_penalty") presencePenalty: Option[Double] = Some(0.0),
  @jsonField("frequency_penalty") frequencyPenalty: Option[Double] = Some(0.0),
  @jsonField("logit_bias") logitBias: Option[Map[String, Int]] = None,
  @jsonField("user") user: Option[String] = None
)

object LMChatRequest {
  implicit val encoder: JsonEncoder[LMChatRequest] = DeriveJsonEncoder.gen[LMChatRequest]
  implicit val decoder: JsonDecoder[LMChatRequest] = DeriveJsonDecoder.gen[LMChatRequest]
}

case class LMChoice(
  @jsonField("index") index: Int,
  @jsonField("message") message: LMMessage,
  @jsonField("finish_reason") finishReason: Option[String] = None
)

object LMChoice {
  implicit val encoder: JsonEncoder[LMChoice] = DeriveJsonEncoder.gen[LMChoice]
  implicit val decoder: JsonDecoder[LMChoice] = DeriveJsonDecoder.gen[LMChoice]
}

case class LMUsage(
  @jsonField("prompt_tokens") promptTokens: Int,
  @jsonField("completion_tokens") completionTokens: Int,
  @jsonField("total_tokens") totalTokens: Int
)

object LMUsage {
  implicit val encoder: JsonEncoder[LMUsage] = DeriveJsonEncoder.gen[LMUsage]
  implicit val decoder: JsonDecoder[LMUsage] = DeriveJsonDecoder.gen[LMUsage]
}

case class LMChatResponse(
  @jsonField("id") id: String,
  @jsonField("object") obj: String,
  @jsonField("created") created: Long,
  @jsonField("model") model: String,
  @jsonField("choices") choices: List[LMChoice],
  @jsonField("usage") usage: Option[LMUsage] = None
)

object LMChatResponse {
  implicit val encoder: JsonEncoder[LMChatResponse] = DeriveJsonEncoder.gen[LMChatResponse]
  implicit val decoder: JsonDecoder[LMChatResponse] = DeriveJsonDecoder.gen[LMChatResponse]
}

// ==================== Completions API ====================

case class LMCompletionRequest(
  @jsonField("model") model: String,
  @jsonField("prompt") prompt: String,
  @jsonField("temperature") temperature: Option[Double] = Some(0.7),
  @jsonField("top_p") topP: Option[Double] = Some(1.0),
  @jsonField("n") n: Option[Int] = Some(1),
  @jsonField("stream") stream: Option[Boolean] = Some(false),
  @jsonField("stop") stop: Option[List[String]] = None,
  @jsonField("max_tokens") maxTokens: Option[Int] = None,
  @jsonField("presence_penalty") presencePenalty: Option[Double] = Some(0.0),
  @jsonField("frequency_penalty") frequencyPenalty: Option[Double] = Some(0.0),
  @jsonField("logit_bias") logitBias: Option[Map[String, Int]] = None,
  @jsonField("user") user: Option[String] = None
)

object LMCompletionRequest {
  implicit val encoder: JsonEncoder[LMCompletionRequest] = DeriveJsonEncoder.gen[LMCompletionRequest]
  implicit val decoder: JsonDecoder[LMCompletionRequest] = DeriveJsonDecoder.gen[LMCompletionRequest]
}

case class LMCompletionChoice(
  @jsonField("text") text: String,
  @jsonField("index") index: Int,
  @jsonField("finish_reason") finishReason: Option[String] = None
)

object LMCompletionChoice {
  implicit val encoder: JsonEncoder[LMCompletionChoice] = DeriveJsonEncoder.gen[LMCompletionChoice]
  implicit val decoder: JsonDecoder[LMCompletionChoice] = DeriveJsonDecoder.gen[LMCompletionChoice]
}

case class LMCompletionResponse(
  @jsonField("id") id: String,
  @jsonField("object") obj: String,
  @jsonField("created") created: Long,
  @jsonField("model") model: String,
  @jsonField("choices") choices: List[LMCompletionChoice],
  @jsonField("usage") usage: Option[LMUsage] = None
)

object LMCompletionResponse {
  implicit val encoder: JsonEncoder[LMCompletionResponse] = DeriveJsonEncoder.gen[LMCompletionResponse]
  implicit val decoder: JsonDecoder[LMCompletionResponse] = DeriveJsonDecoder.gen[LMCompletionResponse]
}

// ==================== Models API ====================

case class LMModelInfo(
  @jsonField("id") id: String,
  @jsonField("object") obj: String,
  @jsonField("created") created: Option[Long] = None,
  @jsonField("owned_by") ownedBy: Option[String] = None
)

object LMModelInfo {
  implicit val encoder: JsonEncoder[LMModelInfo] = DeriveJsonEncoder.gen[LMModelInfo]
  implicit val decoder: JsonDecoder[LMModelInfo] = DeriveJsonDecoder.gen[LMModelInfo]
}

case class LMModelsResponse(
  @jsonField("object") obj: String,
  @jsonField("data") data: List[LMModelInfo]
)

object LMModelsResponse {
  implicit val encoder: JsonEncoder[LMModelsResponse] = DeriveJsonEncoder.gen[LMModelsResponse]
  implicit val decoder: JsonDecoder[LMModelsResponse] = DeriveJsonDecoder.gen[LMModelsResponse]
}

// ==================== Embeddings API ====================

case class LMEmbeddingsRequest(
  @jsonField("model") model: String,
  @jsonField("input") input: String,
  @jsonField("user") user: Option[String] = None
)

object LMEmbeddingsRequest {
  implicit val encoder: JsonEncoder[LMEmbeddingsRequest] = DeriveJsonEncoder.gen[LMEmbeddingsRequest]
  implicit val decoder: JsonDecoder[LMEmbeddingsRequest] = DeriveJsonDecoder.gen[LMEmbeddingsRequest]
}

case class LMEmbeddingData(
  @jsonField("object") obj: String,
  @jsonField("embedding") embedding: List[Double],
  @jsonField("index") index: Int
)

object LMEmbeddingData {
  implicit val encoder: JsonEncoder[LMEmbeddingData] = DeriveJsonEncoder.gen[LMEmbeddingData]
  implicit val decoder: JsonDecoder[LMEmbeddingData] = DeriveJsonDecoder.gen[LMEmbeddingData]
}

case class LMEmbeddingsResponse(
  @jsonField("object") obj: String,
  @jsonField("data") data: List[LMEmbeddingData],
  @jsonField("model") model: String,
  @jsonField("usage") usage: Option[LMUsage] = None
)

object LMEmbeddingsResponse {
  implicit val encoder: JsonEncoder[LMEmbeddingsResponse] = DeriveJsonEncoder.gen[LMEmbeddingsResponse]
  implicit val decoder: JsonDecoder[LMEmbeddingsResponse] = DeriveJsonDecoder.gen[LMEmbeddingsResponse]
}
