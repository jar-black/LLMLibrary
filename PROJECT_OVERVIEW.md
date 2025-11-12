# LLMLibrary - Project Overview

## Project Structure

```
LLMLibrary/
├── build.sbt                    # SBT build configuration
├── project/
│   └── build.properties         # SBT version
├── .env.example                 # Example environment configuration
├── .env                         # Environment configuration (gitignored)
├── README.md                    # User documentation
├── PROJECT_OVERVIEW.md          # This file
└── src/
    └── main/
        └── scala/
            └── io/
                └── github/
                    └── llmlibrary/
                        ├── config/
                        │   └── Config.scala           # Configuration loader
                        ├── models/
                        │   ├── OllamaModels.scala     # Ollama API models
                        │   └── LMStudioModels.scala   # LM Studio API models
                        ├── client/
                        │   ├── OllamaClient.scala     # Ollama HTTP client
                        │   └── LMStudioClient.scala   # LM Studio HTTP client
                        └── examples/
                            ├── OllamaChatExample.scala           # Chat example
                            ├── OllamaGenerateExample.scala       # Generate example
                            ├── OllamaListModelsExample.scala     # List models example
                            └── LMStudioChatExample.scala         # LM Studio chat example
```

## Architecture

### Configuration Layer

The `Config` module provides configuration management:
- Reads from `.env` file or environment variables
- Provides `OllamaConfig` and `LMStudioConfig`
- Exposes a ZIO Layer for dependency injection

### Models Layer

#### OllamaModels.scala
Contains case classes for all Ollama API endpoints:
- **Common**: `Message`, `Options`
- **Chat**: `ChatRequest`, `ChatResponse`
- **Generate**: `GenerateRequest`, `GenerateResponse`
- **Tags**: `ModelInfo`, `TagsResponse`
- **Pull**: `PullRequest`, `PullResponse`
- **Delete**: `DeleteRequest`, `DeleteResponse`
- **Show**: `ShowRequest`, `ShowResponse`
- **Copy**: `CopyRequest`
- **Create**: `CreateRequest`, `CreateResponse`
- **Embeddings**: `EmbeddingsRequest`, `EmbeddingsResponse`

#### LMStudioModels.scala
Contains case classes for LM Studio (OpenAI-compatible) API:
- **Common**: `LMMessage`, `LMUsage`
- **Chat**: `LMChatRequest`, `LMChatResponse`, `LMChoice`
- **Completion**: `LMCompletionRequest`, `LMCompletionResponse`
- **Models**: `LMModelInfo`, `LMModelsResponse`
- **Embeddings**: `LMEmbeddingsRequest`, `LMEmbeddingsResponse`

### Client Layer

#### OllamaClient
Provides methods for all Ollama API endpoints:
```scala
trait OllamaClient {
  def chat(request: ChatRequest): Task[ChatResponse]
  def generate(request: GenerateRequest): Task[GenerateResponse]
  def tags(): Task[TagsResponse]
  def pull(request: PullRequest): Task[Unit]
  def delete(request: DeleteRequest): Task[Unit]
  def show(request: ShowRequest): Task[ShowResponse]
  def copy(request: CopyRequest): Task[Unit]
  def create(request: CreateRequest): Task[Unit]
  def embeddings(request: EmbeddingsRequest): Task[EmbeddingsResponse]
}
```

#### LMStudioClient
Provides methods for LM Studio API endpoints:
```scala
trait LMStudioClient {
  def chat(request: LMChatRequest): Task[LMChatResponse]
  def completion(request: LMCompletionRequest): Task[LMCompletionResponse]
  def models(): Task[LMModelsResponse]
  def embeddings(request: LMEmbeddingsRequest): Task[LMEmbeddingsResponse]
}
```

### Examples

Four example programs demonstrate library usage:
1. **OllamaChatExample**: Basic chat interaction with Ollama
2. **OllamaGenerateExample**: Text generation with Ollama
3. **OllamaListModelsExample**: List all available Ollama models
4. **LMStudioChatExample**: Chat interaction with LM Studio

## Key Features

### 1. Type Safety
All API requests and responses are represented as strongly-typed case classes with proper JSON encoding/decoding.

### 2. Sensible Defaults
All optional parameters have reasonable defaults:
```scala
case class Options(
  numPredict: Option[Int] = None,
  temperature: Option[Double] = None,
  // ... many more options with defaults
)
```

### 3. ZIO Integration
Full ZIO integration with:
- Effect types for all operations
- Layer-based dependency injection
- Proper error handling
- Composable effects

### 4. Error Handling
Comprehensive error handling with descriptive messages:
```scala
response.status match {
  case Status.Ok => // parse response
  case status => ZIO.fail(new RuntimeException(s"Request failed with status $status: $bodyText"))
}
```

### 5. Configuration Flexibility
Configuration can come from:
- `.env` file
- Environment variables
- Programmatic configuration

## API Mapping

### Ollama API Endpoints

| Endpoint | HTTP Method | Scala Method | Description |
|----------|-------------|--------------|-------------|
| `/api/chat` | POST | `chat()` | Chat completions |
| `/api/generate` | POST | `generate()` | Text generation |
| `/api/tags` | GET | `tags()` | List models |
| `/api/pull` | POST | `pull()` | Pull model |
| `/api/delete` | DELETE | `delete()` | Delete model |
| `/api/show` | POST | `show()` | Show model info |
| `/api/copy` | POST | `copy()` | Copy model |
| `/api/create` | POST | `create()` | Create model |
| `/api/embeddings` | POST | `embeddings()` | Generate embeddings |

### LM Studio API Endpoints (OpenAI-compatible)

| Endpoint | HTTP Method | Scala Method | Description |
|----------|-------------|--------------|-------------|
| `/v1/chat/completions` | POST | `chat()` | Chat completions |
| `/v1/completions` | POST | `completion()` | Text completions |
| `/v1/models` | GET | `models()` | List models |
| `/v1/embeddings` | POST | `embeddings()` | Generate embeddings |

## Dependencies

```scala
// ZIO Core - Effect system
"dev.zio" %% "zio" % "2.0.19"
"dev.zio" %% "zio-streams" % "2.0.19"

// ZIO HTTP - HTTP client
"dev.zio" %% "zio-http" % "3.0.0-RC4"

// ZIO JSON - JSON serialization
"dev.zio" %% "zio-json" % "0.6.2"

// ZIO Config - Configuration management
"dev.zio" %% "zio-config" % "4.0.0-RC16"
"dev.zio" %% "zio-config-typesafe" % "4.0.0-RC16"
"dev.zio" %% "zio-config-magnolia" % "4.0.0-RC16"
```

## Usage Pattern

The typical usage pattern follows ZIO's layer-based dependency injection:

```scala
val program: ZIO[OllamaClient, Throwable, Unit] = for {
  response <- OllamaClient.chat(request)
  _ <- Console.printLine(response.message.content)
} yield ()

program.provide(
  Config.live,                                           // Load configuration
  ZLayer.fromFunction((config: LLMConfig) => config.ollama), // Extract Ollama config
  OllamaClient.live                                      // Create client
)
```

## Extension Points

The library is designed to be easily extended:

1. **New Endpoints**: Add new methods to the client traits
2. **Custom Models**: Extend the model case classes
3. **Alternative Backends**: Implement the client traits with different HTTP clients
4. **Streaming Support**: Add streaming support using ZIO Streams

## Testing

To test the library:

1. Start Ollama: `ollama serve`
2. Pull a model: `ollama pull llama2`
3. Run examples: `sbt "runMain io.github.llmlibrary.examples.OllamaChatExample"`

For LM Studio:
1. Start LM Studio and load a model
2. Enable the local server
3. Run: `sbt "runMain io.github.llmlibrary.examples.LMStudioChatExample"`

## Future Enhancements

Potential improvements:
- [ ] Streaming response support
- [ ] Retry logic with exponential backoff
- [ ] Request timeout configuration
- [ ] Connection pooling
- [ ] Metrics and monitoring
- [ ] Async streaming for pull/create operations
- [ ] Model file DSL for creating models
- [ ] Unit tests with mock HTTP responses
- [ ] Integration tests
- [ ] Published to Maven Central

## Notes

- All case classes use ZIO JSON for serialization/deserialization
- The `@jsonField` annotation maps Scala fields to JSON field names
- All optional parameters use `Option[T]` for type safety
- The library uses the default ZIO HTTP client (can be customized)
- Error messages include the response body for debugging
