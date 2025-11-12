# LLMLibrary - Scala Library for Ollama and LM Studio

A comprehensive Scala library for integrating with Ollama and LM Studio using ZIO effects.

## Features

- **Full Ollama API Support**: Chat, Generate, Tags, Pull, Delete, Show, Copy, Create, and Embeddings
- **LM Studio Integration**: OpenAI-compatible API support
- **ZIO Effects**: Fully functional, composable, and type-safe with ZIO
- **Configuration Management**: Easy .env file configuration
- **Type-Safe Models**: Case classes for all API requests and responses with sensible defaults
- **Error Handling**: Proper error handling with descriptive messages

## Installation

### Prerequisites

- Scala 2.13.12
- SBT 1.9.7 (install from https://www.scala-sbt.org/download.html)
- Ollama installed and running (for Ollama features) - https://ollama.ai/
- LM Studio installed and running (for LM Studio features) - https://lmstudio.ai/

#### Installing SBT

**macOS:**
```bash
brew install sbt
```

**Linux:**
```bash
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt
```

**Windows:**
Download from https://www.scala-sbt.org/download.html

### Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd LLMLibrary
```

2. Copy the example environment file and configure it:
```bash
cp .env.example .env
```

3. Edit `.env` with your configuration:
```
OLLAMA_HOST=localhost
OLLAMA_PORT=11434

LMSTUDIO_HOST=localhost
LMSTUDIO_PORT=1234
```

4. Compile the project:
```bash
sbt compile
```

## Quick Start

### Running the Chat Example

```bash
# Make sure Ollama is running and you have a model pulled
ollama pull llama2

# Run the chat example
sbt "runMain io.github.llmlibrary.examples.OllamaChatExample"
```

### Other Examples

```bash
# List available models
sbt "runMain io.github.llmlibrary.examples.OllamaListModelsExample"

# Generate text
sbt "runMain io.github.llmlibrary.examples.OllamaGenerateExample"

# LM Studio chat example
sbt "runMain io.github.llmlibrary.examples.LMStudioChatExample"
```

## Usage

### Ollama Client

#### Chat Completion

```scala
import io.github.llmlibrary.client.OllamaClient
import io.github.llmlibrary.config.Config
import io.github.llmlibrary.models.{ChatRequest, Message, Options}
import zio._

val program = for {
  response <- OllamaClient.chat(ChatRequest(
    model = "llama2",
    messages = List(
      Message.system("You are a helpful assistant."),
      Message.user("Hello!")
    ),
    options = Some(Options(temperature = Some(0.7)))
  ))
  _ <- Console.printLine(response.message.content)
} yield ()

// Run with layers
program.provide(
  Config.live,
  ZLayer.fromFunction((config: LLMConfig) => config.ollama),
  OllamaClient.live
)
```

#### Generate Completion

```scala
val program = for {
  response <- OllamaClient.generate(GenerateRequest(
    model = "llama2",
    prompt = "Write a poem about Scala:",
    options = Some(Options(temperature = Some(0.8)))
  ))
  _ <- Console.printLine(response.response)
} yield ()
```

#### List Models

```scala
val program = for {
  response <- OllamaClient.tags()
  _ <- ZIO.foreach(response.models) { model =>
    Console.printLine(s"${model.name} - ${model.size} bytes")
  }
} yield ()
```

#### Pull a Model

```scala
val program = for {
  _ <- OllamaClient.pull(PullRequest(name = "llama2"))
  _ <- Console.printLine("Model pulled successfully")
} yield ()
```

#### Delete a Model

```scala
val program = for {
  _ <- OllamaClient.delete(DeleteRequest(name = "old-model"))
  _ <- Console.printLine("Model deleted successfully")
} yield ()
```

#### Show Model Information

```scala
val program = for {
  response <- OllamaClient.show(ShowRequest(name = "llama2"))
  _ <- Console.printLine(s"Modelfile: ${response.modelfile}")
} yield ()
```

#### Copy a Model

```scala
val program = for {
  _ <- OllamaClient.copy(CopyRequest(
    source = "llama2",
    destination = "my-llama2"
  ))
  _ <- Console.printLine("Model copied successfully")
} yield ()
```

#### Generate Embeddings

```scala
val program = for {
  response <- OllamaClient.embeddings(EmbeddingsRequest(
    model = "llama2",
    prompt = "Hello world"
  ))
  _ <- Console.printLine(s"Embedding dimensions: ${response.embedding.length}")
} yield ()
```

### LM Studio Client

#### Chat Completion

```scala
import io.github.llmlibrary.client.LMStudioClient
import io.github.llmlibrary.models.{LMChatRequest, LMMessage}

val program = for {
  response <- LMStudioClient.chat(LMChatRequest(
    model = "local-model",
    messages = List(
      LMMessage.system("You are a helpful assistant."),
      LMMessage.user("Hello!")
    ),
    temperature = Some(0.7)
  ))
  _ <- response.choices.headOption match {
    case Some(choice) => Console.printLine(choice.message.content)
    case None => Console.printLine("No response")
  }
} yield ()

// Run with layers
program.provide(
  Config.live,
  ZLayer.fromFunction((config: LLMConfig) => config.lmStudio),
  LMStudioClient.live
)
```

#### List Models

```scala
val program = for {
  response <- LMStudioClient.models()
  _ <- ZIO.foreach(response.data) { model =>
    Console.printLine(s"${model.id}")
  }
} yield ()
```

## API Documentation

### Ollama Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/chat` | POST | Generate chat completions |
| `/api/generate` | POST | Generate text completions |
| `/api/tags` | GET | List available models |
| `/api/pull` | POST | Pull a model from registry |
| `/api/delete` | DELETE | Delete a model |
| `/api/show` | POST | Show model information |
| `/api/copy` | POST | Copy a model |
| `/api/create` | POST | Create a model from Modelfile |
| `/api/embeddings` | POST | Generate embeddings |

### LM Studio Endpoints (OpenAI-compatible)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/v1/chat/completions` | POST | Generate chat completions |
| `/v1/completions` | POST | Generate text completions |
| `/v1/models` | GET | List available models |
| `/v1/embeddings` | POST | Generate embeddings |

## Configuration Options

### Options Case Class

All generation requests support these options:

- `temperature`: Controls randomness (0.0 to 2.0)
- `topP`: Nucleus sampling parameter
- `topK`: Top-k sampling parameter
- `numPredict`: Maximum number of tokens to generate
- `repeatPenalty`: Penalty for repeating tokens
- `seed`: Random seed for reproducibility
- `numCtx`: Context window size
- And many more...

All options have sensible defaults and are optional.

## Error Handling

The library provides descriptive error messages:

```scala
val program = OllamaClient.chat(request).catchAll { error =>
  Console.printLine(s"Error: ${error.getMessage}") *>
  // Handle error appropriately
  ZIO.fail(error)
}
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

See the LICENSE file for details.

## Dependencies

- ZIO 2.0.19
- ZIO HTTP 3.0.0-RC4
- ZIO JSON 0.6.2
- ZIO Config 4.0.0-RC16

## Support

For issues and questions, please open an issue on GitHub.
