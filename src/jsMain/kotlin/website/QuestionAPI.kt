package website

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

data class Question(
    val question: String,
    val answer: Char,
    val choices: HashMap<Char, String>
) {
    override fun toString(): String {
        return "$question Answer: $answer: ${choices[answer]}\n"
    }
}

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

@Serializable
data class QuestionAPI(val response_code: Int, val results: List<JsonObject>)

suspend fun getQuestion(): QuestionAPI =
    client.get("https://opentdb.com/api.php?amount=1&category=15&difficulty=easy&type=multiple")


fun bindChoices(list: List<String>): HashMap<Char, String> {
    val choicesMap = hashMapOf<Char, String>()
    var index = 0
    for (character in 'A'..'D') {
        choicesMap[character] = list[index++]
    }
    return choicesMap
}

suspend fun getListQuestions(count: Int): List<Question> {
    val questionList = mutableListOf<Question>()
    repeat(count) {
        val questionAPI: QuestionAPI = getQuestion()
        val results = questionAPI.results[0]
        val choices = (results["incorrect_answers"]!!.jsonArray + results["correct_answer"])
            .shuffled()
            .map { it.toString() }
        val choicesMap = bindChoices(choices)
        val answer: Char = choicesMap.filterValues { it == results["correct_answer"].toString() }
            .keys.first()

        questionList.add(Question(results["question"].toString(), answer, choicesMap))
    }
    return questionList.toList()
}
