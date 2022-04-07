package website

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

data class Question(
    val question: String,
    val answer: Char,
    val choices: HashMap<Char, String>
)
val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}
@Serializable
data class QuestionAPI(val response_code: Int, val results: List<JsonObject>)

suspend fun getQuestion(): QuestionAPI {
    return client.get("https://opentdb.com/api.php?amount=1&category=15&difficulty=easy&type=multiple")
}

suspend fun getListQuestions(count: Int): List<Question> {
    val questionList = mutableListOf<Question>()
    repeat(count) {
        val questionAPI: QuestionAPI = getQuestion()
        val results = questionAPI.results[0]
        val choices = (results["incorrect_answers"]!!.jsonArray + results["correct_answer"]).shuffled()
        val newHashMap = hashMapOf<Char, String>()
        var index = 0
        for (character in 'A'..'D') {
            newHashMap[character] = choices[index++].toString()
        }
        val answer: Char = newHashMap.filterValues { it == results["correct_answer"].toString() }.keys.first()
        questionList.add(
            Question(
                results["question"].toString(),
                answer, newHashMap
            )
        )
    }
    return questionList.toList()
}