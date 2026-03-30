package edu.gvsu.cis.kmp_wordy

// ADDED: Ktor imports for HTTP client
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
// ADDED: kotlinx serialization imports for data classes
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//declare the data classes
@Serializable
data class QuoteItem(
    val id: Int, val quote: String, val author: String
)
@Serializable
data class QuoteResponse(
    val quotes: List<QuoteItem>, val total: Int = 0
)
//dictionary data classes
@Serializable
data class Dictionary(
    val word: String,
    val meanings: List<Meaning> = emptyList()
)
@Serializable
data class Meaning(
    val partOfSpeech: String = "",
    val definitions: List<Definition> = emptyList()
)
@Serializable
data class Definition(
    val definition: String? = null,
    val example:String? = null
)
//filter words after tokenization
private val FILTER_WORDS = setOf(
    "a", "an", "the", "be", "is", "am", "are", "was", "were",
    "at", "by", "do", "go", "he", "if", "in", "it", "me",
    "my", "no", "of", "on", "or", "so", "to", "up", "us",
    "we", "as", "at", "but", "for", "not", "you", "all",
    "and", "can", "had", "has", "her", "him", "his", "how",
    "its", "may", "our", "out", "own", "put", "too", "two",
    "who", "why", "yet", "get", "got", "let", "lot", "now",
    "old", "one", "say", "see", "she", "way", "did", "from",
    "i", "with", "that", "this", "they", "have", "what", "when"
)

//define http client
val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true  // ignore extra fields in JSON response
            isLenient = true
        })
    }
}

//fetch
suspend fun WordsfromQuotes(): Set<String>{
    val response: QuoteResponse= httpClient
        .get("https://dummyjson.com/quotes?limit=100")
        .body()

    return  response.quotes.flatMap { quoteItem -> quoteItem.quote.lowercase()
                                                                .split(Regex("[^a-zA-Z]+"))// split where space,comma etc to get words
                                                                .filter{word -> word.length >=2 && word !in FILTER_WORDS}
    }.map { it.uppercase() }.toSet()
}

suspend fun fetchMeaning(word: String): String? {
    return try {
        val entries: List<Dictionary> = httpClient.get("https://api.dictionaryapi.dev/api/v2/entries/en/${word.lowercase()}").body()
        entries.firstOrNull()
            ?.meanings?.firstOrNull()
            ?.definitions?.firstOrNull()
            ?.definition

    }
    catch(e: Exception){null //if word not found and dont crash
    }

}
