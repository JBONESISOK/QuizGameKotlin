package website

import components.questionBox
import components.submitButton
import kotlinx.browser.document
import kotlinx.coroutines.*
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.useEffectOnce
import react.useState

private val scope = MainScope()
external fun alert(msg: String)
external fun prompt(msg: String)

val Game = FC<Props> {
    val incorrectList: MutableList<Question> by useState(mutableListOf())
    var currentQuestionNum: Int by useState(0)
    var numCorrect: Int by useState(0)
    var questionList by useState(emptyList<Question>())
    var currentAnswer: Char? by useState(null)

    useEffectOnce {
        scope.launch {
            val numOfQuestions = prompt("How many questions do you want? ").toString().toIntOrNull()
            if(numOfQuestions == null) alert("You did not enter a number. Default is 5 questions.")
            questionList = getListQuestions(numOfQuestions ?: 5)
        }
    }

    fun addIncorrectAnswer() {
        val newList = incorrectList
        newList.add(questionList[currentQuestionNum])
        println(incorrectList)
    }

    fun continueGame() {
        if (currentQuestionNum < questionList.size - 1) {
            questionList[currentQuestionNum].choices.forEach { (key, _) ->
                val radioButton = document.getElementById(key.toString()) as HTMLInputElement
                radioButton.checked = false
            }
            currentQuestionNum++
        } else {
            alert("Congratulations you completed the game!")
            alert("You got $numCorrect correct!")
            alert("Here's what you got wrong $incorrectList")
        }
    }

    val gameHandlerObj = object : GameHandler {
        override fun setSelectedAnswer(answer: Char?) {
            currentAnswer = answer
        }

        override fun submitAnswer(answer: Char?) {
            when (currentAnswer) {
                null -> alert("You need to select a answer")
                questionList[currentQuestionNum].answer -> {
                    alert("You selected the correct answer!")
                    numCorrect++
                }
                else -> {
                    alert("Incorrect.")
                    addIncorrectAnswer()
                }
            }
            continueGame()
        }
    }
    div {
        if(questionList.isNotEmpty()) {
            questionBox {
                questionObj = questionList[currentQuestionNum]
                gameHandler = gameHandlerObj
            }
            submitButton {
                gameHandler = gameHandlerObj
                answer = currentAnswer
            }
        }
    }
}
