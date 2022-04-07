package components

import website.GameHandler
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div

external interface SubmitButtonProps : Props {
    var answer: Char?
    var gameHandler: GameHandler
}

val submitButton = FC<SubmitButtonProps>  { props ->
    div {
        button {
            onClick = {
                props.gameHandler.submitAnswer(props.answer)
            }
            +"Submit Answer"
        }
    }
}