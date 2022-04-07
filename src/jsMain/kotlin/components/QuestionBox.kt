package components

import react.FC
import react.Props
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import website.GameHandler
import website.Question

external interface QuestionBoxProps : Props {
    var questionObj: Question
    var gameHandler: GameHandler
}

val questionBox = FC<QuestionBoxProps> { props ->
    div {
        p {
            +"${props.questionObj.question}: "
        }
        props.questionObj.choices.forEach { (key, value) ->
            input {
                name = props.questionObj.question
                id = key.toString()
                type = InputType.radio
                onChange = {
                    props.gameHandler.setSelectedAnswer(key)
                }
            }
            label {
                htmlFor = key.toString()
                +"$key: $value"
            }
        }
    }
}